package net.madmanmarkau.multihome.util;

import net.madmanmarkau.multihome.MultiHomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageUtil {
    private static Logger log = MultiHomePlugin.getInst().getLogger();

    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GOLD + message);
    }

    public static void logSevere(String message) {
        log.log(Level.SEVERE, message);
    }

    public static void logWarning(String message) {
        log.log(Level.WARNING, message);
    }

    public static void logInfo(String message) {
        log.log(Level.INFO, message);
    }

    public static void logFine(String message) {
        log.log(Level.FINE, message);
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }
}
