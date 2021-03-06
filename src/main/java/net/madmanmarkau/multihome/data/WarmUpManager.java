package net.madmanmarkau.multihome.data;

import net.madmanmarkau.multihome.MultiHomePlugin;

/**
 * @author MadManMarkAu
 */
public abstract class WarmUpManager {
    MultiHomePlugin plugin;

    public WarmUpManager(MultiHomePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Clears all current warmups.
     */
    public abstract void clearWarmups();

    /**
     * Returns a WarmUpEntry object for the specified warmup. If warmup is not found, returns null.
     *
     * @param player Player to retrieve warmup for.
     * @return WarmUpEntry object for this warmup. Otherwise null.
     */
    public abstract WarmUpEntry getWarmup(String player);

    /**
     * Adds a new warmup or updates an existing one.
     *
     * @param warmup Date object for when this warmup expires.
     */
    public abstract void addWarmup(WarmUpEntry warmup);

    /**
     * Remove an existing warmup.
     *
     * @param player Player to remove warmup from.
     */
    public abstract void removeWarmup(String player);

    abstract public void taskComplete(WarmUpEntry warmup);
}
