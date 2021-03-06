package net.madmanmarkau.multihome.data;

import net.madmanmarkau.multihome.MultiHomePlugin;
import org.bukkit.entity.Player;

import java.util.Date;

public abstract class CoolDownManager {
    protected final MultiHomePlugin plugin;

    /**
     * @param plugin The plug-in.
     */
    public CoolDownManager(MultiHomePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Clears all current cooldowns.
     */
    public abstract void clearCooldowns();

    /**
     * Returns the expiry time for the specified cooldown. If cooldown is not found, returns null.
     *
     * @param player Player to retrieve cooldown for.
     * @return Date object for this cooldown. Otherwise null.
     */
    public CoolDownEntry getCooldown(Player player) {
        return this.getCooldown(player.getName());
    }

    /**
     * Returns the expiry time for the specified cooldown. If cooldown is not found, returns null.
     *
     * @param player Player to retrieve cooldown for.
     * @return Date object for this cooldown. Otherwise null.
     */
    public abstract CoolDownEntry getCooldown(String player);

    /**
     * Adds a new cooldown or updates an existing one.
     *
     * @param player Player to set cooldown on.
     * @param expiry Date object for when this cooldown expires.
     */
    public void addCooldown(Player player, Date expiry) {
        this.addCooldown(player.getName(), expiry);
    }

    /**
     * Adds a new cooldown or updates an existing one.
     *
     * @param player Player to set cooldown on.
     * @param expiry Date object for when this cooldown expires.
     */
    public void addCooldown(String player, Date expiry) {
        this.addCooldown(new CoolDownEntry(player, expiry));
    }

    /**
     * Adds a new cooldown or updates an existing one.
     *
     * @param cooldown Cooldown entry.
     */
    public abstract void addCooldown(CoolDownEntry cooldown);

    /**
     * Remove an existing cooldown.
     *
     * @param player Player to remove cooldown from.
     */
    public void removeCooldown(Player player) {
        this.removeCooldown(player.getName());
    }

    /**
     * Remove an existing cooldown.
     *
     * @param player Player to remove cooldown from.
     */
    public abstract void removeCooldown(String player);
}
