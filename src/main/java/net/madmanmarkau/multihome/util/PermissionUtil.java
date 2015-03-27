package net.madmanmarkau.multihome.util;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Sleaker
 */
public class PermissionUtil {
    private static PermissionsHandler handler;
    private static Permission vault = null;

    private enum PermissionsHandler {
        VAULT, SUPERPERMS
    }

    public static boolean initialize(JavaPlugin plugin) {
        RegisteredServiceProvider<Permission> vaultPermissionProvider = null;

        try {
            vaultPermissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        } catch (Exception e) {
            // Eat errors
        }

        if (vaultPermissionProvider != null) {
            vault = vaultPermissionProvider.getProvider();
            handler = PermissionsHandler.VAULT;
            MessageUtil.logInfo("Using Vault for permissions system.");
            return true;
        } else {
            handler = PermissionsHandler.SUPERPERMS;
            MessageUtil.logWarning("A permission plugin was not detected! Defaulting to Bukkit's permissions system.");
            MessageUtil.logWarning("Groups disabled. All players defaulting to \"default\" group.");
            return true;
        }
    }

    public static boolean has(Player player, String permission) {
        boolean blnHasPermission;

        switch (handler) {
            case VAULT:
                blnHasPermission = vault.has(player, permission);
                break;
            default:
                blnHasPermission = player.hasPermission(permission);
        }

        return blnHasPermission;
    }

    public static String getGroup(Player player) {
        if (player != null) {
            switch (handler) {
                case VAULT:
                    return vault.getPrimaryGroup(player);
                default:
                    break;
            }
        }

        return "default";
    }
}
