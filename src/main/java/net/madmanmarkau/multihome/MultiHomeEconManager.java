package net.madmanmarkau.multihome;

import net.madmanmarkau.multihome.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;


public class MultiHomeEconManager {

    public static EconomyHandler handler;
    private static Economy vault = null;
    public static MultiHomePlugin plugin;

    public enum EconomyHandler {
        VAULT, NONE
    }

    protected static void initialize(MultiHomePlugin plugin) {
        MultiHomeEconManager.plugin = plugin;

        if (Settings.isEconomyEnabled()) {
            RegisteredServiceProvider<Economy> vaultEconomyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (vaultEconomyProvider != null) {
                handler = EconomyHandler.VAULT;
                vault = vaultEconomyProvider.getProvider();
                MessageUtil.logInfo("Economy enabled using: Vault");
                return;
            }

            handler = EconomyHandler.NONE;
            MessageUtil.logWarning("An economy plugin wasn't detected!");
        } else {
            handler = EconomyHandler.NONE;
        }
    }

    // Determine if player has enough money to cover [amount]
    public static boolean hasEnough(String player, double amount) {
        switch (handler) {
            case VAULT:
                if (vault != null) {
                    return vault.has(player, amount);
                }
                break;
        }

        return true;
    }

    // Remove [amount] from players account
    public static boolean chargePlayer(String player, double amount) {
        switch (handler) {
            case VAULT:
                if (vault != null) {
                    return vault.bankWithdraw(player, amount).transactionSuccess();
                }
                break;
        }

        return true;
    }

    // Format the monetary amount into a string, according to the configured format
    public static String formatCurrency(double amount) {
        switch (handler) {
            case VAULT:
                if (vault != null) {
                    return vault.format(amount);
                }
                break;
        }

        return amount + "";
    }
}