package net.madmanmarkau.multihome;

import net.madmanmarkau.multihome.data.HomeEntry;
import net.madmanmarkau.multihome.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MultiHomePlayerListener implements Listener {
    MultiHomePlugin plugin;

    public MultiHomePlayerListener(MultiHomePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (PermissionUtil.has(player, "multihome.homeondeath") && Settings.isHomeOnDeathEnabled()) {
            HomeEntry homeEntry = plugin.getHomeManager().getHome(player, "");
            if (homeEntry != null) {
                event.setRespawnLocation(homeEntry.getHomeLocation(plugin.getServer()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        String player = event.getPlayer().getName();
        plugin.getWarmUpManager().removeWarmup(player);
    }
}
