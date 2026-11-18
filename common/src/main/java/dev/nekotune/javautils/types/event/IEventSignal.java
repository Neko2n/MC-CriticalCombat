package dev.nekotune.javautils.types.event;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault 
public sealed interface IEventSignal<T> permits Event {
    public void post(final T payload);
}