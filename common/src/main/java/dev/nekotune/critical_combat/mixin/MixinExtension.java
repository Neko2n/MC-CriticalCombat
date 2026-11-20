package dev.nekotune.critical_combat.mixin;

@FunctionalInterface
public interface MixinExtension<T> {
    public T critical_combat$getExtension();
}
