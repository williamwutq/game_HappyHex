'''
A package to detect, and potentially install, machine learning model running environments and dependencies.

This package provides import checks for TensorFlow and PyTorch.

If called as a script, it can attempt to install missing dependencies using pip.
It should be called with an argument specifying which dependency to check/install:
- "tf" for TensorFlow
- "torch" for PyTorch
- "all" for both TensorFlow and PyTorch

It will prints on console the status of each dependency (installed or installing), or error if installation fails.

Example usage:
    python envs.py tf
    python envs.py torch
    python envs.py all
'''

def has_tensorflow() -> bool:
    '''
    Check if TensorFlow is installed.

    Returns:
        bool: True if TensorFlow is installed, False otherwise.
    '''
    try:
        import tensorflow
        try:
            from tensorflow import keras
        except ImportError:
            import keras
        return True
    except ImportError:
        return False

def has_torch() -> bool:
    '''
    Check if PyTorch is installed.

    Returns:
        bool: True if PyTorch is installed, False otherwise.
    '''
    try:
        import torch
        return True
    except ImportError:
        return False

def install_tensorflow() -> None:
    '''
    Install TensorFlow using pip.

    Raises:
        RuntimeError: If the installation fails.
    '''
    import subprocess
    import sys
    # Macos detection, so if on macos, install tensorflow-macos
    if sys.platform == 'darwin':
        try:
            subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'tensorflow-macos'])
        except subprocess.CalledProcessError as e:
            raise RuntimeError("Failed to install TensorFlow for macOS") from e
    else:
        try:
            subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'tensorflow'])
        except subprocess.CalledProcessError as e:
            raise RuntimeError("Failed to install TensorFlow") from e

def install_torch() -> None:
    '''
    Install PyTorch using pip.

    Raises:
        RuntimeError: If the installation fails.
    '''
    import subprocess
    import sys
    try:
        subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'torch', 'torchvision', 'torchaudio'])
    except subprocess.CalledProcessError as e:
        raise RuntimeError("Failed to install PyTorch") from e

def check_gpu_tensorflow() -> bool:
    '''
    Check if TensorFlow can access a GPU.

    Returns:
        bool: True if TensorFlow can access a GPU, False otherwise.
    '''
    try:
        import tensorflow as tf
        return tf.config.list_physical_devices('GPU') != []
    except ImportError:
        return False

def check_gpu_torch() -> bool:
    '''
    Check if PyTorch can access a GPU.

    Returns:
        bool: True if PyTorch can access a GPU, False otherwise.
    '''
    try:
        import torch
        return torch.cuda.is_available()
    except ImportError:
        return False

if __name__ == "__main__":
    # Get the running argument
    import sys
    if len(sys.argv) == 0:
        exit(0)
    elif sys.argv[1] == "tf":
        if not has_tensorflow():
            print("TensorFlow: Installing")
            try:
                install_tensorflow()
            except RuntimeError as e:
                print(f"TensorFlow: Error {e}")
        else:
            print("TensorFlow: Installed")
    elif sys.argv[1] == "torch":
        if not has_torch():
            print("PyTorch: Installing")
            try:
                install_torch()
            except RuntimeError as e:
                print(f"PyTorch: Error {e}")
        else:
            print("PyTorch: Installed")
    elif sys.argv[1] == "all":
        if not has_tensorflow():
            print("TensorFlow: Installing")
            try:
                install_tensorflow()
            except RuntimeError as e:
                print(f"TensorFlow: Error {e}")
        else:
            print("TensorFlow: Installed")
        if not has_torch():
            print("PyTorch: Installing")
            try:
                install_torch()
            except RuntimeError as e:
                print(f"PyTorch: Error {e}")
        else:
            print("PyTorch: Installed")
    elif sys.argv[1] == "gpu":
        if len(sys.argv) > 2 and sys.argv[2] == "tf":
            if check_gpu_tensorflow():
                print("TensorFlow: GPU Available")
            else:
                print("TensorFlow: GPU Unavailable")
        elif len(sys.argv) > 2 and sys.argv[2] == "torch":
            if check_gpu_torch():
                print("PyTorch: GPU Available")
            else:
                print("PyTorch: GPU Unavailable")
        else:
            exit(1)
    else:
        exit(1)
