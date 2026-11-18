package dev.nekotune.javautils.types.event;

import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault 
public sealed interface IEventHook<T> permits Event {
    public void subscribe(final Consumer<T> listener);
}