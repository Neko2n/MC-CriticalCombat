package dev.nekotune.javautils.types.event;

import java.util.LinkedList;
import java.util.function.Consumer;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault 
public non-sealed class Event<T> implements IEventHook<T>, IEventSignal<T> {

    private final LinkedList<Consumer<T>> listeners = new LinkedList<>();

    @Override
    @OverridingMethodsMustInvokeSuper 
    public void subscribe(final Consumer<T> listener) {
        this.listeners.add(listener);
    }

    @Override
    @OverridingMethodsMustInvokeSuper 
    public void post(final T payload) {
        for (final Consumer<T> listener : this.listeners) {
            listener.accept(payload);
        }
    }
}
