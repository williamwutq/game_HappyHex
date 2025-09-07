# Copied from HappyHex machine learning project.
# Copyrighted by contributors to github.com/williamwutq/hpyhexml.
# Distributed under the MIT License.

'''
A package for implementing hexagonal convolutional neural networks (HexCNN) using TensorFlow and Keras.
This module provides a function to precompute the neighbor index map for a hexagonal grid,
which is essential for defining the convolutional operations in a hexagonal grid structure.
'''

import tensorflow as tf
import keras
from keras.layers import Layer
from ..hex import Hex, HexEngine

def precompute_neighbor_index_map(radius: int) -> dict[int, tuple[int, int, int, int, int, int, int]]:
    '''
    Precompute the neighbor index map for a hexagonal grid of a given radius.

    This function uses a HexEngine instance to calculate the indices of neighboring hexes
    for each hex in the grid. The neighbors are defined as the six hexes surrounding a
    center hex, plus the center hex itself, creating a kernel of size 7.

    Parameters:
        radius (int): The radius of the hexagonal grid.
    Returns:
        map (dict[int, tuple[int, int, int, int, int, int, int]]): A dictionary mapping each hex index to
        a tuple of indices of its neighbors. The tuple contains the indices of the six neighbors
        and the index of the center hex itself. If a neighbor does not exist (i.e., it is outside the grid),
        its index will be -1.
    '''
    engine = HexEngine(radius)
    neighbor_map = {}
    positions = (
        Hex(-1, -1), Hex(-1, 0), Hex(0, -1), Hex(0, 0), Hex(0, 1), Hex(1, 0), Hex(1, 1)
    )
    for index in range(len(engine)):
        center_hex = engine.coordinate_block(index)
        neighbors = []
        for offset in positions:
            neighbor_hex = center_hex + offset
            neighbor_idx = engine.index_block(neighbor_hex)
            neighbors.append(neighbor_idx if neighbor_idx >= 0 else -1)
        neighbor_map[index] = tuple(neighbors)
    return neighbor_map

def get_radius_from_blocks(length: int) -> int:
    '''
    Get the radius of the hexagonal grid based on the number of blocks.

    This function calculates the radius of a hexagonal grid given the number of blocks.
    The radius is determined by solving the equation for the number of blocks in a hexagonal grid.

    Parameters:
        length (int): The number of blocks in the hexagonal grid.
    Returns:
        int: The radius of the hexagonal grid.
    '''
    return HexEngine.solve_radius(length)

@keras.saving.register_keras_serializable()
class HexConv(Layer):
    '''
    A Keras layer that implements hexagonal convolution using precomputed neighbor indices.
    This layer applies a hexagonal convolution operation to the input tensor, using a fixed kernel size of 7,
    which corresponds to the six neighbors of a hexagon plus the center hexagon itself.
    The layer uses precomputed neighbor indices to efficiently gather the values from the input tensor
    based on the hexagonal grid structure.
    '''
    maps = {} # Map length to precomputed neighbor index map

    def __init__(self, output_dim: int, shrink: bool=False, **kwargs):
        '''
        Initialize the HexConv layer with the specified output dimension.

        The output dimension should be defined here as the number of output features for the convolution operation.
        The input dimension is determined by the input shape during the build phase.

        Shrink is a boolean parameter that determines whether to apply shrinking to the output. When set to True, 
        the output hexagonal grid will be shrunken to a smaller radius. For example, if the input has a radius of 4 (37 blocks),
        setting shrink to True will reduce the output radius to 3 (19 blocks). This is natural for convolutional layers.
        When set to False, the output will have the same radius as the input, but when encountering invalid neighbors,
        their values will be padded with zeros.

        Because of the nature of convolutional layers, the shrink parameter does not affect the weights.

        Parameters:
            output_dim (int): The number of output features for the convolution operation.
            shrink (bool): Whether to apply shrinking to the output. Defaults to False.
        Raises:
            ValueError: If the output dimension is not a positive integer.
        '''
        if not isinstance(output_dim, int) or output_dim <= 0:
            raise ValueError(f"Invalid output dimension: {output_dim}. Must be a positive integer.")
        if not isinstance(shrink, bool):
            raise ValueError(f"Invalid shrink value: {shrink}. Must be a boolean.")
        # Handle activation and regularizers, accept both identifier strings and objects
        activation = kwargs.pop('activation', None)
        self.activation = keras.activations.get(activation) if activation is not None else None
        kernel_regularizer = kwargs.pop('kernel_regularizer', None)
        self.kernel_regularizer = keras.regularizers.get(kernel_regularizer) if kernel_regularizer is not None else None
        activity_regularizer = kwargs.pop('activity_regularizer', None)
        self.activity_regularizer = keras.regularizers.get(activity_regularizer) if activity_regularizer is not None else None
        kwargs.pop('kernel_size', None) # We don't care about kernel size here, it is fixed at 7
        # Initialize the parent class
        super().__init__(**kwargs)
        # Initialize instance
        self.shrink = shrink
        self.output_dim = output_dim
        self.indices_cache = {}  # Cache for indices to avoid recomputation, starts empty
        self.kernel_size = 7  # fixed, due to 7-point Piece.positions

    def build(self, input_shape):
        '''
        Build the layer with the given input shape and cache neighbor indices for efficient computation.

        This method computes and caches the neighbor indices for the given input shape,
        which is essential for efficient hexagonal convolution operations in the layer.

        The method also initializes the kernel weights and bias for the convolution operation.
        The kernel weights are initialized with a glorot uniform initializer, and the bias is initialized to zeros.
        
        The input_shape is expected to be of the form (batch_size, num_blocks, in_features).

        Parameters:
            input_shape (tuple): The shape of the input tensor.
        Raises:
            ValueError: If the number of blocks in the input tensor is not valid.
        '''
        # input_shape: (batch_size, num_blocks, in_features)
        self.in_features = input_shape[-1]
        num_blocks_int = int(input_shape[1])  # Number of hexagonal blocks in the input

        # One kernel weight per neighbor position
        self.kernel_weights = self.add_weight(
            shape=(self.kernel_size, self.in_features, self.output_dim),
            initializer="glorot_uniform",
            trainable=True,
            name="hex_kernel_weights",
            regularizer=self.kernel_regularizer
        )

        self.bias = self.add_weight(
            shape=(self.output_dim,),
            initializer="zeros",
            trainable=True,
            name="hex_bias"
        )
        
        self.__recompute_indices(num_blocks_int)  # Precompute indices for the given number of blocks


    def call(self, inputs):
        '''
        Apply the hexagonal convolution operation to the input tensor.

        Invalid neighbors (with index -1) are masked to zero in the output.

        If the instance setting `shrink` to True, the output will be shrunken to a smaller radius,
        meaning that only valid neighbors will be included in the output.
        If `shrink` is False, the output will have the same radius as the input,
        and invalid neighbors will be zeroed out.

        Parameters:
            inputs (tf.Tensor): Input tensor of shape (batch_size, num_blocks, in_features).
        Returns:
            tf.Tensor: Output tensor of shape (batch_size, num_blocks or shrunken, output_dim).
        Raises:
            ValueError: If the number of blocks in the input tensor is not valid.
        '''
        num_blocks_int = int(inputs.shape[1]) if inputs.shape[1] is not None else tf.shape(inputs)[1]

        # Ensure indices are valid for the current number of blocks
        if num_blocks_int not in self.indices_cache:
            indices_list = self.__recompute_indices(num_blocks_int)
        else:
            indices_list = self.indices_cache[num_blocks_int]

        gathered = []  # List of tensors of shape (batch_size, num_blocks, in_features)
        for k in range(self.kernel_size):
            indices = indices_list[k]

            # Replace -1 with 0, we’ll zero it out later
            masked_indices = tf.where(indices >= 0, indices, tf.zeros_like(indices))

            # Gather values from inputs
            selected = tf.gather(inputs, masked_indices, axis=1)

            # Mask out invalid (-1) positions with zero
            mask = tf.cast(tf.expand_dims(indices >= 0, -1), tf.float32)
            selected = selected * mask

            gathered.append(selected)  # shape: (batch, num_blocks, in_features)

        # Apply per-kernel linear transform and sum
        outputs = []
        for k in range(self.kernel_size):
            proj = tf.linalg.matmul(gathered[k], self.kernel_weights[k])  # (batch, num_blocks, output_dim)
            outputs.append(proj)

        # Sum the outputs and add bias
        result = tf.add_n(outputs) + self.bias

        # Apply activation if specified
        if self.activation is not None:
            result = self.activation(result)

        # Apply activity regularization if specified
        if self.activity_regularizer is not None:
            self.add_loss(self.activity_regularizer(result))
        
        return result  # shape: (batch, num_blocks or shrunken, output_dim)
    
    def get_config(self):
        '''
        Get the configuration of the layer for serialization.

        This method returns a dictionary containing the configuration of the layer,
        including the output dimension, shrink parameter, and other settings.
        
        Returns:
            dict: A dictionary containing the configuration of the layer.
        '''
        config = super().get_config()
        config.update({
            'output_dim': self.output_dim,
            'shrink': self.shrink,
            'kernel_size': self.kernel_size,
            'activation': keras.activations.serialize(self.activation) if self.activation is not None else None,
            'kernel_regularizer': keras.regularizers.serialize(self.kernel_regularizer) if self.kernel_regularizer is not None else None,
            'activity_regularizer': keras.regularizers.serialize(self.activity_regularizer) if self.activity_regularizer is not None else None
        })
        return config
    
    def __recompute_indices(self, num_blocks: int):
        '''
        Recompute the neighbor indices for the given number of blocks.
        This method repopulates the indices cache for the specified number of blocks,
        ensuring that the neighbor indices are up-to-date and valid for the hexagonal grid structure.

        Note:
            The result produced by this method is dependent on the `shrink` parameter. Whether the grid
            is shrunken or not will affect the indices that are returned. Since the parameter should be
            set during initialization for the entire layer, it is not necessary to be able to adapt dynamically
            to different behaviors.

            When `shrink` is set to True, the output hexagonal grid will be shrunken to a smaller radius.
            This is achieved by requiring that all indices in the neighbor map are valid (not -1) for them to be included.
            For example, if the input has a radius of 4 (37 blocks), setting `shrink` to True will reduce the output radius to 3 (19 blocks).
            This is natural for convolutional layers.

            When `shrink` is set to False, the output will have the same radius as the input, and this method will set the indices of invalid
            neighbors to -1, allowing them to be included in the output but masked out later.

        All results are cached in the `indices_cache` attribute of the HexConv instance for future use.

        Parameters:
            num_blocks (int): The number of blocks in the hexagonal grid.
        Raises:
            ValueError: If the number of blocks is not a positive integer is not valid for hexagonal grids.
        Returns:
            list[tf.Tensor]: A list of tensors containing the indices for each neighbor position.
        '''
        if not isinstance(num_blocks, int) or num_blocks <= 0:
            raise ValueError(f"Invalid number of blocks: {num_blocks}. Must be a positive integer.")
        if num_blocks not in self.maps:
            # If neighbor_map is not precomputed for this number of blocks, compute it
            radius = get_radius_from_blocks(num_blocks)
            if radius == -1:
                raise ValueError(f"Invalid number of blocks: {num_blocks}. Cannot determine radius.")
            else:
                neighbor_map = precompute_neighbor_index_map(radius)
                self.maps[num_blocks] = neighbor_map
        else:
            # Use precomputed map
            neighbor_map = self.maps[num_blocks]
        indices_list = []
        for k in range(self.kernel_size):
            indices = []
            for i in range(num_blocks):
                # Add into indices if exist in the neighbor map and,
                # if shrink is required, require that all indices are valid (not -1)
                # or if shrink is not required, allow -1 indices, and we add them as -1s
                if i in neighbor_map and (all(idx != -1 for idx in neighbor_map[i]) or not self.shrink):
                    indices.append(neighbor_map[i][k])
            indices_list.append(tf.constant(indices, dtype=tf.int32))
        self.indices_cache[num_blocks] = indices_list
        return indices_list

@keras.saving.register_keras_serializable()
class HexDynamicConv(Layer):
    '''
    A Keras layer that implements hexagonal convolution using precomputed neighbor indices.
    This layer applies a hexagonal convolution operation to the input tensor, using a fixed kernel size of 7,
    which corresponds to the six neighbors of a hexagon plus the center hexagon itself.
    The layer uses precomputed neighbor indices to efficiently gather the values from the input tensor
    based on the hexagonal grid structure.
    
    This variation of HexConv allows for dynamic kernel weights, where each neighbor position can have its own set of weights from dynamic input.
    
    It is very important to note that this layer require strict input shapes, and mismatches will raise errors:
    - The first input tensor should be of shape (batch_size, num_blocks, in_features), representing the hexagonal grid input.
    - The second input tensor should be of shape (batch_size, kernel_size, dyn_dim), representing the dynamic input for the kernel.
    
    The dynamic input is expected to provide additional features for each neighbor position, allowing for more flexible
    and adaptive convolution operations. If the dynamic input is unnecessary, use HexConv instead.
    '''
    maps = {} # Map length to precomputed neighbor index map

    def __init__(self, output_dim: int, shrink: bool=False, **kwargs):
        '''
        Initialize the HexConv layer with the specified output dimension.

        The output dimension should be defined here as the number of output features for the convolution operation.
        The input dimension is determined by the input shape during the build phase.

        Shrink is a boolean parameter that determines whether to apply shrinking to the output. When set to True, 
        the output hexagonal grid will be shrunken to a smaller radius. For example, if the input has a radius of 4 (37 blocks),
        setting shrink to True will reduce the output radius to 3 (19 blocks). This is natural for convolutional layers.
        When set to False, the output will have the same radius as the input, but when encountering invalid neighbors,
        their values will be padded with zeros.

        Because of the nature of convolutional layers, the shrink parameter does not affect the weights.

        Parameters:
            output_dim (int): The number of output features for the convolution operation.
            shrink (bool): Whether to apply shrinking to the output. Defaults to False.
        Raises:
            ValueError: If the output dimension is not a positive integer.
        '''
        if not isinstance(output_dim, int) or output_dim <= 0:
            raise ValueError(f"Invalid output dimension: {output_dim}. Must be a positive integer.")
        if not isinstance(shrink, bool):
            raise ValueError(f"Invalid shrink value: {shrink}. Must be a boolean.")
        # Handle activation and regularizers, accept both identifier strings and objects
        activation = kwargs.pop('activation', None)
        self.activation = keras.activations.get(activation) if activation is not None else None
        kernel_regularizer = kwargs.pop('kernel_regularizer', None)
        self.kernel_regularizer = keras.regularizers.get(kernel_regularizer) if kernel_regularizer is not None else None
        activity_regularizer = kwargs.pop('activity_regularizer', None)
        self.activity_regularizer = keras.regularizers.get(activity_regularizer) if activity_regularizer is not None else None
        kwargs.pop('kernel_size', None) # We don't care about kernel size here, it is fixed at 7
        # Initialize the parent class
        super().__init__(**kwargs)
        # Initialize instance
        self.shrink = shrink
        self.output_dim = output_dim
        self.indices_cache = {}  # Cache for indices to avoid recomputation, starts empty
        self.kernel_size = 7  # fixed, due to 7-point Piece.positions

    def build(self, input_shape):
        '''
        Build the layer with the given input shape and cache neighbor indices for efficient computation.

        This method computes and caches the neighbor indices for the given input shape using an internal method,
        which is essential for efficient hexagonal convolution operations in the layer.

        The method also initializes the kernel weights and bias for the convolution operation.
        The kernel weights are initialized with a glorot uniform initializer, and the bias is initialized to zeros.
        
        The input shape is expected to be a list of two tensors:
        - The first tensor is of shape (batch_size, num_blocks, in_features), representing the hexagonal grid input.
        - The second tensor is of shape (batch_size, kernel_size, dyn_dim), representing the dynamic input for the kernel.

        Parameters:
            input_shape (tuple): The shape of the input tensor.
        Raises:
            ValueError: If the number of blocks in the input tensor is not valid.
        '''
        # input_shape[0]: (batch_size, num_blocks, in_features)
        # input_shape[1]: (batch_size, kernel_size, dyn_dim)
        self.in_features = input_shape[0][-1]
        num_blocks_int = int(input_shape[0][1])  # Number of hexagonal blocks in the input

        self.dyn_dim = input_shape[1][-1]  # Dynamic dimension for the kernel
        if not input_shape[1][1] == self.kernel_size:
            raise ValueError(f"Invalid input shape for dynamic convolution: {input_shape[1]}. "
                             f"Expected second dimension to be {self.kernel_size}, got {input_shape[1][1]}.")

        # Dynamic kernel weights, dyn_dim weights per neighbor position
        self.kernel_dyn_weights = self.add_weight(
            shape=(self.kernel_size, self.dyn_dim, self.in_features, self.output_dim),
            initializer="glorot_uniform",
            trainable=True,
            name="hex_kernel_dyn_weights",
            regularizer=self.kernel_regularizer
        )

        # One kernel weight per neighbor position
        self.kernel_weights = self.add_weight(
            shape=(self.kernel_size, self.in_features, self.output_dim),
            initializer="glorot_uniform",
            trainable=True,
            name="hex_kernel_weights",
            regularizer=self.kernel_regularizer
        )

        # Bias for the output
        self.bias = self.add_weight(
            shape=(self.output_dim,),
            initializer="zeros",
            trainable=True,
            name="hex_bias"
        )
        
        self.__recompute_indices(num_blocks_int)  # Precompute indices for the given number of blocks


    def call(self, inputs):
        '''
        Apply the hexagonal convolution operation to the input tensor.

        Invalid neighbors (with index -1) are masked to zero in the output.

        If the instance setting `shrink` to True, the output will be shrunken to a smaller radius,
        meaning that only valid neighbors will be included in the output.
        If `shrink` is False, the output will have the same radius as the input,
        and invalid neighbors will be zeroed out.

        This method applies a dynamic convolution operation, where each neighbor position can have its own set of weights
        from the dynamic input. The dynamic input is expected to be of shape (batch_size, kernel_size, dyn_dim),
        where `kernel_size` is the number of neighbors (7 for hexagonal convolution) and `dyn_dim` is the dimension of the dynamic input.

        Parameters:
            inputs (tf.Tensor): Input tensor of shape (batch_size, num_blocks, in_features) for the hexagonal grid,
                                and (batch_size, kernel_size, dyn_dim) for the dynamic input.
        Returns:
            tf.Tensor: Output tensor of shape (batch_size, num_blocks or shrunken, output_dim).
        Raises:
            ValueError: If the number of blocks in the input tensor is not valid.
        '''
        grid_input = inputs[0]  # Shape: (batch_size, num_blocks, in_features)
        dyn_input = inputs[1]  # Shape: (batch_size, kernel_size, dyn_dim)
        num_blocks_int = int(grid_input.shape[1]) if grid_input.shape[1] is not None else tf.shape(grid_input)[1]

        # Ensure indices are valid for the current number of blocks
        if num_blocks_int not in self.indices_cache:
            indices_list = self.__recompute_indices(num_blocks_int)
        else:
            indices_list = self.indices_cache[num_blocks_int]

        gathered = []  # List of tensors of shape (batch_size, num_blocks, in_features)
        for k in range(self.kernel_size):
            indices = indices_list[k]

            # Replace -1 with 0, we’ll zero it out later
            masked_indices = tf.where(indices >= 0, indices, tf.zeros_like(indices))

            # Gather values from inputs
            selected = tf.gather(grid_input, masked_indices, axis=1)

            # Mask out invalid (-1) positions with zero
            mask = tf.cast(tf.expand_dims(indices >= 0, -1), tf.float32)
            selected = selected * mask

            gathered.append(selected)  # shape: (batch, num_blocks, in_features)

        # Apply per-kernel linear transform and sum
        outputs = []
        for k in range(self.kernel_size):
            # gathered[k]: (batch, num_blocks, in_features)
            # dyn_input[:, k, :]: (batch, dyn_dim)
            # kernel_dyn_weights[k]: (dyn_dim, in_features, output_dim)
            # Compute dynamic kernel: weighted sum over dyn_dim
            dyn_kernel = tf.einsum('bd,dio->bio', dyn_input[:, k, :], self.kernel_dyn_weights[k])
            # dyn_kernel: (batch, in_features, output_dim)
            # Apply dynamic kernel to gathered[k]
            dyn_proj = tf.matmul(gathered[k], dyn_kernel)  # (batch, num_blocks, output_dim)
            # Also apply static kernel
            static_proj = tf.linalg.matmul(gathered[k], self.kernel_weights[k])  # (batch, num_blocks, output_dim)
            proj = dyn_proj + static_proj
            outputs.append(proj)

        # Sum the outputs and add bias
        result = tf.add_n(outputs) + self.bias

        # Apply activation if specified
        if self.activation is not None:
            result = self.activation(result)

        # Apply activity regularization if specified
        if self.activity_regularizer is not None:
            self.add_loss(self.activity_regularizer(result))
        
        return result  # shape: (batch, num_blocks or shrunken, output_dim)
    
    def get_config(self):
        '''
        Get the configuration of the layer for serialization.

        This method returns a dictionary containing the configuration of the layer,
        including the output dimension, shrink parameter, and other settings.
        
        Returns:
            dict: A dictionary containing the configuration of the layer.
        '''
        config = super().get_config()
        config.update({
            'output_dim': self.output_dim,
            'shrink': self.shrink,
            'kernel_size': self.kernel_size,
            'activation': keras.activations.serialize(self.activation) if self.activation is not None else None,
            'kernel_regularizer': keras.regularizers.serialize(self.kernel_regularizer) if self.kernel_regularizer is not None else None,
            'activity_regularizer': keras.regularizers.serialize(self.activity_regularizer) if self.activity_regularizer is not None else None
        })
        return config
    
    def __recompute_indices(self, num_blocks: int):
        '''
        Recompute the neighbor indices for the given number of blocks.
        This method repopulates the indices cache for the specified number of blocks,
        ensuring that the neighbor indices are up-to-date and valid for the hexagonal grid structure.

        Note:
            The result produced by this method is dependent on the `shrink` parameter. Whether the grid
            is shrunken or not will affect the indices that are returned. Since the parameter should be
            set during initialization for the entire layer, it is not necessary to be able to adapt dynamically
            to different behaviors.

            When `shrink` is set to True, the output hexagonal grid will be shrunken to a smaller radius.
            This is achieved by requiring that all indices in the neighbor map are valid (not -1) for them to be included.
            For example, if the input has a radius of 4 (37 blocks), setting `shrink` to True will reduce the output radius to 3 (19 blocks).
            This is natural for convolutional layers.

            When `shrink` is set to False, the output will have the same radius as the input, and this method will set the indices of invalid
            neighbors to -1, allowing them to be included in the output but masked out later.

        All results are cached in the `indices_cache` attribute of the HexConv instance for future use.

        Parameters:
            num_blocks (int): The number of blocks in the hexagonal grid.
        Raises:
            ValueError: If the number of blocks is not a positive integer is not valid for hexagonal grids.
        Returns:
            list[tf.Tensor]: A list of tensors containing the indices for each neighbor position.
        '''
        if not isinstance(num_blocks, int) or num_blocks <= 0:
            raise ValueError(f"Invalid number of blocks: {num_blocks}. Must be a positive integer.")
        if num_blocks not in self.maps:
            # If neighbor_map is not precomputed for this number of blocks, compute it
            radius = get_radius_from_blocks(num_blocks)
            if radius == -1:
                raise ValueError(f"Invalid number of blocks: {num_blocks}. Cannot determine radius.")
            else:
                neighbor_map = precompute_neighbor_index_map(radius)
                self.maps[num_blocks] = neighbor_map
        else:
            # Use precomputed map
            neighbor_map = self.maps[num_blocks]
        indices_list = []
        for k in range(self.kernel_size):
            indices = []
            for i in range(num_blocks):
                # Add into indices if exist in the neighbor map and,
                # if shrink is required, require that all indices are valid (not -1)
                # or if shrink is not required, allow -1 indices, and we add them as -1s
                if i in neighbor_map and (all(idx != -1 for idx in neighbor_map[i]) or not self.shrink):
                    indices.append(neighbor_map[i][k])
            indices_list.append(tf.constant(indices, dtype=tf.int32))
        self.indices_cache[num_blocks] = indices_list
        return indices_list


# Add common maps for all instances of HexConv
for r in range(2, 13):
    HexConv.maps[get_radius_from_blocks(r)] = precompute_neighbor_index_map(r)