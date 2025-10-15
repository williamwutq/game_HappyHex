package util.fgui;

import java.awt.Component;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The {@code GraphicsDisplayer} interface defines a contract for displaying and managing
 * graphical components in a user interface. It provides methods to display a component,
 * remove the currently displayed component, and retrieve the currently displayed component.
 * <p>
 * This interface extends {@link Consumer} to allow for functional-style usage, enabling
 * components to be displayed using lambda expressions or method references.
 * <p>
 * Implementations of this interface should ensure that resources associated with components
 * are properly managed, including freeing up resources when components are removed or replaced.
 * <p>
 * The interface also includes a default method to accept a {@link GraphicsProvider}, which
 * starts the provider and displays its component, ensuring any previously displayed component
 * is removed first.
 * <p>
 * Static factory method {@link #of} is provided to create instances of {@code GraphicsDisplayer}
 * using custom display logic or simple state management via an array.
 *
 * @see Component
 * @see GraphicsProvider
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public interface GraphicsDisplayer extends Consumer<GraphicsProvider> {
    /**
     * Display the given component. If there is currently a component being displayed, it should be
     * shutdown and removed before displaying the new component. If the component is null,
     * this method should do nothing. To remove the current component so there is no component
     * being displayed, use the {@link #remove()} method instead of passing null to this method.
     *
     * @param component the component to display
     */
    void display(Component component);
    /**
     * Remove the currently displayed component, if any. This should free up any resources
     * associated with the component and make sure it is no longer visible. If there is no
     * component currently displayed, this method should do nothing.
     */
    void remove();
    /**
     * Display the component provided by the given {@code GraphicsProvider}. This method should
     * call {@link GraphicsProvider#start()} before retrieving the component to display, and
     * should ensure that any previously displayed component is properly removed and resources
     * freed before displaying the new component.
     * <p>
     * If the provided {@code GraphicsProvider} is {@code null}, this method should do nothing.
     *
     * @param provider the {@code GraphicsProvider} supplying the component to display
     */
    default void accept(GraphicsProvider provider){
        if (provider == null) return;
        provider.start();
        display(provider.get());
    }
    /**
     * Get the currently displayed component, or {@code null} if no component is being displayed.
     *
     * @return the currently displayed component, or {@code null} if none
     */
    Component current();
    /**
     * Get a {@code Consumer<GraphicsProvider>} that can be used to display components using this
     * {@code GraphicsDisplayer}. The returned consumer will call the {@link #accept(GraphicsProvider)}
     * method of this displayer when its {@code accept} method is called.
     *
     * @return a consumer that displays components using this displayer
     */
    default Consumer<GraphicsProvider> asGraphicsProviderConsumer() {
        return this;
    }
    /**
     * Get a {@code Consumer<Component>} that can be used to display components using this
     * {@code GraphicsDisplayer}. The returned consumer will call the {@link #display(Component)}
     * method of this displayer when its {@code accept} method is called.
     *
     * @return a consumer that displays components using this displayer
     */
    default Consumer<Component> asComponentConsumer() {
        return this::display;
    }
    /**
     * Create a {@code GraphicsDisplayer} that uses the given consumer to display components
     * and the given supplier to retrieve the currently displayed component.
     * <p>
     * The returned {@code GraphicsDisplayer} will call the provided consumer with the new
     * component when {@link #display(Component)} is called, and will call the consumer with
     * {@code null} when {@link #remove()} is called. If the currently displayed component
     * implements {@link AutoCloseable}, its {@code close()} method will be called when it
     * is removed or replaced.
     * <p>
     * The supplier is used to return the currently displayed component when
     * {@link #current()} is called.
     *
     * @param displayer a consumer that handles displaying components
     * @param current a supplier that provides the currently displayed component
     * @return a new {@code GraphicsDisplayer} instance
     */
    static GraphicsDisplayer of(Consumer<Component> displayer, Supplier<Component> current){
        return new GraphicsDisplayer() {
            private GraphicsProvider curr = null;
            @Override
            public void accept(GraphicsProvider provider) {
                if (provider == null) return;
                provider.start();
                if (curr != null){
                    curr.close();
                }
                curr = provider;
                displayer.accept(provider.get());
            }
            @Override
            public void display(Component component) {
                if (component == null) return;
                if (curr != null){
                    curr.close();
                    displayer.accept(component);
                    curr = null;
                }
            }
            @Override
            public void remove() {
                if (curr != null){
                    curr.close();
                    displayer.accept(null);
                    curr = null;
                }
            }
            @Override
            public Component current() {
                return current.get();
            }
        };
    }
    /**
     * Create a {@code GraphicsDisplayer} that uses the first element of the given array
     * to hold the currently displayed component. The array must have at least one element.
     * <p>
     * The returned {@code GraphicsDisplayer} will set the first element of the array to
     * the new component when {@link #display(Component)} is called, and will set it to
     * {@code null} when {@link #remove()} is called. If the currently displayed component
     * implements {@link AutoCloseable}, its {@code close()} method will be called when it
     * is removed or replaced.
     * <p>
     * This method is useful for simple cases where you want to keep track of the currently
     * displayed component without needing a full-fledged state management system.
     * <p>
     * Note that the array can be modified externally if needed, although such operation is
     * not recommended.
     *
     * @param holder an array with at least one element to hold the current component
     * @return a new {@code GraphicsDisplayer} instance
     * @throws IllegalArgumentException if the holder array is null or has no elements
     */
    static GraphicsDisplayer of(Component[] holder){
        if (holder == null || holder.length == 0) {
            throw new IllegalArgumentException("Holder array must have at least one element to hold the current component.");
        }
        return new GraphicsDisplayer() {
            @Override
            public void remove() {
                if (holder[0] != null) {
                    if (holder[0] instanceof AutoCloseable) {
                        try {
                            ((AutoCloseable) holder[0]).close();
                        } catch (Exception ignored) {}
                    }
                    holder[0] = null;
                }
            }
            @Override
            public void display(Component component) {
                if (component == null) return;
                if (holder[0] != null) {
                    if (holder[0] instanceof AutoCloseable) {
                        try {
                            ((AutoCloseable) holder[0]).close();
                        } catch (Exception ignored) {
                        }
                    }
                }
                holder[0] = component;
            }
            @Override
            public Component current() {
                return holder[0];
            }
        };
    }
}
