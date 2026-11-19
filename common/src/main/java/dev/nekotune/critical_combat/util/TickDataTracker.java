package dev.nekotune.critical_combat.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tracks the average value of a given data source across a certain number of
 * ticks.
 * 
 * Does __not__ automatically hook into onTickEvent.
 */
@ParametersAreNonnullByDefault
public abstract class TickDataTracker<T> implements Supplier<T> {

    private final int historyTicks;
    private final Supplier<T> presentValueSupplier;
    private final LinkedList<T> history = new LinkedList<>();

    public TickDataTracker(final int historyTicks, final Supplier<T> presentValueSupplier) {
        this.historyTicks = Math.max(historyTicks, 1);
        this.presentValueSupplier = presentValueSupplier;
        final T pValue = this.presentValueSupplier.get();
        for (int i = 0; i <= this.historyTicks; i++) {
            this.history.push(pValue);
        }
    }

    /**
     * Computes the value returned by {@link TickDataTracker#get()}.
     * Returns the difference between the present value and the old values divided by
     * the number of ticks passed since the old value was recorded.
     * 
     * @param presentValue The current value
     * @param oldValues    The values that have been recorded each tick
     * @return Computational result
     */
    protected abstract T compute(final T presentValue, final Collection<T> oldValues);

    /**
     * Should be called inside a tick loop to update the tracker.
     */
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        this.history.push(this.presentValueSupplier.get());
        this.history.removeLast();
    }

    /**
     * @return Computed average value across multiple ticks
     * @see TickDataTracker#compute
     * @see Supplier#get
     */
    @Override
    public final T get() {
        return this.compute(this.presentValueSupplier.get(), this.history.stream().toList());
    }
}
