package util.fgui;

import util.function.Failable;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * An interface for a component that can be displayed in the game.
 * A frame is a component that can be displayed in the game window.
 * It can be recalled, resized, and repainted.
 * <p>
 * The interface is intended for Components that can be, and might need to be,
 * displayed anywhere in arbitrary hierarchy in the {@code java.awt} framework.
 * <p>
 * Implementations of this interface should ensure that once {@link #start()} is called,
 * the component returned by {@link #get()} is properly initialized and ready for display.
 * When the component is no longer needed, the {@link #close()} method should be
 * called to release any resources held by the component. Component can be reused
 * but {@code GraphicsProvider} instances should not be reused after
 * {@link #close()} is called.
 * <p>
 * Static factory method {@link #of} is provided to create {@code GraphicsProvider} instances
 * from existing components or suppliers of components.
 *
 * @see Component
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
@FunctionalInterface
public interface GraphicsProvider extends Supplier<Component>, AutoCloseable {
    /**
     * Get the component to be displayed.
     * @return the component to be displayed
     */
    Component get();
    /**
     * Repaint the contents of the component.
     */
    default void repaint(){
        Component comp = get();
        if(comp != null) get().repaint();
    }
    /**
     * Set the size of the component. Until the next call to setSize, the frame should not change size.
     * <p>
     * If the size is not set, the component should use its preferred size that is non-zero in both dimensions.
     * @implNote The default implementation sets the size, preferred size, minimum size, and maximum size
     *          of the component to the specified width and height. This might not be needed for all implementations.
     * @param width the width of the frame
     * @param height the height of the frame
     */
    default void setSize(int width, int height){
        Component comp = get();
        if (comp == null) return;
        Dimension size = new Dimension(width, height);
        comp.setSize(size);
        comp.setPreferredSize(size);
        comp.setMinimumSize(size);
        comp.setMaximumSize(size);
    }
    /**
     * Start any threads or resources needed by the component.
     * If the component does not need to use any resources, this method does nothing.
     * <p>
     * The method is called exactly once before the component is used.
     */
    default void start(){
        // Default implementation does nothing. If the component carry resources, it should override this method.
    }
    /**
     * Close the component and release any resources it holds.
     * If the component does not hold any resources, this method does nothing.
     * <p>
     * The method is called exactly once when the component is no longer needed.
     */
    default void close(){
        // Default implementation does nothing. If the component carry resources, it should override this method.
    }
    /**
     * Create a {@code GraphicsProvider} that always provides the same component.
     * <p>
     * The component should not use resources that need to be released, but it may change
     * in the background. If the component uses resources that need to be released,
     * consider using {@link #of(Component, Runnable, Runnable)} instead.
     * @param component the component to be provided
     * @return a {@code GraphicsProvider} that always provides the same component
     */
    static GraphicsProvider of(Component component){
        return new GraphicsProvider() {
            @Override
            public Component get() {
                return component;
            }
        };
    }
    /**
     * Create a {@code GraphicsProvider} that always provides the same component with its
     * initializer and finalizer.
     * <p>
     * The initializer is called once when the component is started, and the finalizer is
     * called once when the component is closed. This is useful for components that use
     * resources that need to be released, such as threads or file handles.
     * @param initializer the initializer to be called when the component is started
     * @param finalizer the finalizer to be called when the component is closed
     * @param component the component to be provided
     * @return a {@code GraphicsProvider} that always provides the same component
     */
    static GraphicsProvider of(Component component, Runnable initializer, Runnable finalizer){
        return new GraphicsProvider() {
            @Override
            public Component get() {
                return component;
            }
            @Override
            public void start() {
                initializer.run();
            }
            @Override
            public void close() {
                finalizer.run();
            }
        };
    }
    /**
     * Create a {@code GraphicsProvider} that provides a component from the given supplier.
     * The supplier is called each time the component is needed, so it can provide different
     * components at different times.
     * <p>
     * The supplier should not use resources that need to be released. If it is the case
     * consider using {@link #of(Supplier, Runnable, Runnable)} instead.
     *
     * @param supplier the supplier of the component
     * @return a {@code GraphicsProvider} that provides a component from the given supplier
     */
    static GraphicsProvider of(Supplier<Component> supplier){
        return supplier::get;
    }
    /**
     * Create a {@code GraphicsProvider} that provides a component from the given callable.
     * The callable is called each time the component is needed, so it can provide different
     * components at different times.
     * <p>
     * The callable should not use resources that need to be released. If this is the case,
     * consider using {@link #of(Callable, Failable, Failable)} instead. If the callable fails,
     * the component will be {@code null}. When this happens, methods like {@link #repaint()} and
     * {@link #setSize(int, int)} will do nothing.
     *
     * @param callable the callable used to generate the component
     * @return a {@code GraphicsProvider} that provides a component from the given callable
     */
    static GraphicsProvider of(Callable<Component> callable){
        return () -> {
            try {
                return callable.call();
            } catch (Exception ignored) {
                return null;
            }
        };
    }
    /**
     * Create a {@code GraphicsProvider} that provides a component from the given supplier
     * with its initializer and finalizer.
     * <p>
     * The initializer is called once when the component is started, and the finalizer is
     * called once when the component is closed. This is useful for components that use
     * resources that need to be released, such as threads or file handles.
     * @param supplier the supplier of the component
     * @param initializer the initializer to be called when the component is started
     * @param finalizer the finalizer to be called when the component is closed
     * @return a {@code GraphicsProvider} that provides a component from the given supplier
     */
    static GraphicsProvider of(Supplier<Component> supplier, Runnable initializer, Runnable finalizer){
        return new GraphicsProvider() {
            @Override
            public Component get() {
                return supplier.get();
            }
            @Override
            public void start() {
                initializer.run();
            }
            @Override
            public void close() {
                finalizer.run();
            }
        };
    }
    /**
     * Create a {@code GraphicsProvider} that provides a component from the given callable
     * with its initializer and finalizer.
     * <p>
     * The initializer is called once when the component is started, and the finalizer is
     * called once when the component is closed. This is useful for components that use
     * resources that need to be released, such as threads or file handles.
     * <p>
     * If the callable fails, the component will be {@code null}. When this happens,
     * methods like {@link #repaint()} and {@link #setSize(int, int)} will do nothing.
     *
     * @param callable the callable used to generate the component
     * @param initializer the initializer to be called when the component is started
     * @param finalizer the finalizer to be called when the component is closed
     * @return a {@code GraphicsProvider} that provides a component from the given callable
     */
    static GraphicsProvider of(Callable<Component> callable, Failable<Exception> initializer, Failable<Exception> finalizer){
        return new GraphicsProvider() {
            @Override
            public Component get() {
                try {
                    return callable.call();
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            public void start() {
                try {
                    initializer.run();
                } catch (Exception ignored) {
                }
            }
            @Override
            public void close() {
                try {
                    finalizer.run();
                } catch (Exception ignored) {
                }
            }
        };
    }
}
