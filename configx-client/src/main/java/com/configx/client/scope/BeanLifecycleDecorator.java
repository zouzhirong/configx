package com.configx.client.scope;

/**
 * A helper interface providing optional decoration of bean instances and their
 * destruction callbacks. Users can supply custom implementations of this
 * strategy if they want tighter control over method invocation on the bean or
 * its destruction callback.
 *
 * @param <T> the type of auxiliary context object that can be passed between
 *            methods. Implementations can choose what type of data to supply as
 *            it is passed around unchanged by the caller.
 * @author Dave Syer
 */
public interface BeanLifecycleDecorator<T> {

    /**
     * Optionally decorate and provide a new instance of a compatible bean for
     * the caller to use instead of the input.
     *
     * @param bean    the bean to optionally decorate
     * @param context the context as created by
     *                {@link #decorateDestructionCallback(Runnable)}
     * @return the replacement bean for the caller to use
     */
    Object decorateBean(Object bean, Context<T> context);

    /**
     * Optionally decorate the destruction callback provided, and also return
     * some context that can be used later by the
     * {@link #decorateBean(Object, Context)} method.
     *
     * @param callback the destruction callback that will be used by the container
     * @return a context wrapper
     */
    Context<T> decorateDestructionCallback(Runnable callback);

    static class Context<T> {

        private final T auxiliary;
        private final Runnable callback;

        public Context(Runnable callback, T auxiliary) {
            this.callback = callback;
            this.auxiliary = auxiliary;
        }

        public Runnable getCallback() {
            return callback;
        }

        public T getAuxiliary() {
            return auxiliary;
        }

    }

}
