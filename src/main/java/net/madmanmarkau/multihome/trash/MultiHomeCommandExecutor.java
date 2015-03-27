package net.madmanmarkau.multihome.trash;

import com.censoredsoftware.library.command.type.BaseCommand;
import com.censoredsoftware.library.command.type.CommandResult;
import net.madmanmarkau.multihome.MultiHomeEconManager;
import net.madmanmarkau.multihome.MultiHomePlugin;
import net.madmanmarkau.multihome.Settings;
import net.madmanmarkau.multihome.data.CoolDownEntry;
import net.madmanmarkau.multihome.data.HomeEntry;
import net.madmanmarkau.multihome.data.InviteEntry;
import net.madmanmarkau.multihome.data.WarmUpEntry;
import net.madmanmarkau.multihome.util.MessageUtil;
import net.madmanmarkau.multihome.util.MiscUtil;
import net.madmanmarkau.multihome.util.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;

public class MultiHomeCommandExecutor extends BaseCommand {
    MultiHomePlugin plugin;

    public MultiHomeCommandExecutor(MultiHomePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult onCommand(CommandSender sender, Command cmd, String[] args) {
        if (!(sender instanceof Player)) {
            // Command sent by console/plugin
            return onCommandFromConsole(sender, cmd, args);
        }
        // Command sent by player
        return onCommandFromPlayer((Player) sender, cmd, args);
    }

    private CommandResult onCommandFromPlayer(Player player, Command cmd, String[] args) {
        if (cmd.getName().compareToIgnoreCase("home") == 0 || cmd.getName().compareToIgnoreCase("mhome") == 0) {

            if (args.length == 0) {
                MultiHomeCommands.goDefaultHome(this.plugin, player);
            } else if (args.length == 1) {
                String homeArgs[] = MiscUtil.splitHome(args[0]);

                if (homeArgs.length > 1) {
                    MultiHomeCommands.goPlayerNamedHome(this.plugin, player, homeArgs[0], homeArgs[1]);
                } else {
                    MultiHomeCommands.goNamedHome(this.plugin, player, homeArgs[0]);
                }
            } else {
                Settings.sendMessageTooManyParameters(player);
            }

        } else if (cmd.getName().compareToIgnoreCase("sethome") == 0 || cmd.getName().compareToIgnoreCase("msethome") == 0) {

            if (args.length == 0) {
                MultiHomeCommands.setDefaultHome(this.plugin, player);
            } else if (args.length == 1) {
                String homeArgs[] = MiscUtil.splitHome(args[0]);

                if (homeArgs.length > 1) {
                    MultiHomeCommands.setPlayerNamedHome(this.plugin, player, homeArgs[0], homeArgs[1]);
                } else {
                    MultiHomeCommands.setNamedHome(this.plugin, player, homeArgs[0]);
                }
            } else {
                Settings.sendMessageTooManyParameters(player);
            }

        } else if (cmd.getName().compareToIgnoreCase("deletehome") == 0 || cmd.getName().compareToIgnoreCase("mdeletehome") == 0) {

            if (args.length == 0) {
                MultiHomeCommands.deleteDefaultHome(this.plugin, player);
            } else if (args.length == 1) {
                String homeArgs[] = MiscUtil.splitHome(args[0]);

                if (homeArgs.length > 1) {
                    MultiHomeCommands.deletePlayerNamedHome(this.plugin, player, homeArgs[0], homeArgs[1]);
                } else {
                    MultiHomeCommands.deleteNamedHome(this.plugin, player, homeArgs[0]);
                }
            } else {
                Settings.sendMessageTooManyParameters(player);
            }

        } else if (cmd.getName().compareToIgnoreCase("listhomes") == 0 || cmd.getName().compareToIgnoreCase("mlisthomes") == 0) {


        } else if (cmd.getName().compareToIgnoreCase("invitehome") == 0 || cmd.getName().compareToIgnoreCase("minvitehome") == 0) {

            if (args.length == 1) {
                MultiHomeCommands.inviteDefaultHome(this.plugin, player, args[0]);
            } else if (args.length == 2) {
                MultiHomeCommands.inviteNamedHome(this.plugin, player, args[0], args[1]);
            }

        } else if (cmd.getName().compareToIgnoreCase("invitehometimed") == 0 || cmd.getName().compareToIgnoreCase("minvitehometimed") == 0) {

            if (args.length == 2) {
                MultiHomeCommands.inviteDefaultTimedHome(this.plugin, player, args[0], MiscUtil.decodeTime(args[1]));
            } else if (args.length == 3) {
                MultiHomeCommands.inviteNamedTimedHome(this.plugin, player, args[0], MiscUtil.decodeTime(args[1]), args[2]);
            } else {
                Settings.sendMessageTooManyParameters(player);
            }

        } else if (cmd.getName().compareToIgnoreCase("uninvitehome") == 0 || cmd.getName().compareToIgnoreCase("muninvitehome") == 0) {

            if (args.length == 1) {
                MultiHomeCommands.uninviteDefaultHome(this.plugin, player, args[0]);
            } else if (args.length == 2) {
                MultiHomeCommands.uninviteNamedHome(this.plugin, player, args[0], args[1]);
            } else {
                Settings.sendMessageTooManyParameters(player);
            }

        } else if (cmd.getName().compareToIgnoreCase("listinvites") == 0 || cmd.getName().compareToIgnoreCase("mlistinvites") == 0) {

            if (args.length == 0) {
                MultiHomeCommands.listInvitesToMe(this.plugin, player);
            } else {
                Settings.sendMessageTooManyParameters(player);
            }

        } else if (cmd.getName().compareToIgnoreCase("listmyinvites") == 0 || cmd.getName().compareToIgnoreCase("mlistmyinvites") == 0) {

            if (args.length == 0) {
                MultiHomeCommands.listInvitesToOthers(this.plugin, player);
            } else {
                Settings.sendMessageTooManyParameters(player);
            }

        }
    }

    public static void goDefaultHome(MultiHomePlugin plugin, Player player) {
        if (PermissionUtil.has(player, "multihome.defaulthome.go")) {
            double amount = 0;

            //Check for economy first - and make sure the player either has permission for free homes or has enough money
            if (Settings.isEconomyEnabled() && !PermissionUtil.has(player, "multihome.free.defaulthome.go")) {
                if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getHomeCost(player))) {
                    Settings.sendMessageNotEnoughMoney(player, Settings.getHomeCost(player));
                    return;
                } else {
                    amount = Settings.getHomeCost(player);
                }
            }

            // Get user cooldown timer.
            CoolDownEntry cooldown = plugin.getCoolDownManager().getCooldown(player.getName());

            if (cooldown != null && !PermissionUtil.has(player, "multihome.ignore.cooldown")) {
                Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getExpiry().getTime() - new Date().getTime()), 1000) / 1000);
                return;
            }

            int warmupTime = Settings.getSettingWarmup(player);
            HomeEntry homeEntry = plugin.getHomeManager().getHome(player, "");

            if (homeEntry != null) {
                if (warmupTime > 0 && !PermissionUtil.has(player, "multihome.ignore.warmup")) {
                    // Warpup required.
                    WarmUpEntry warmup = new WarmUpEntry(player.getName(), MiscUtil.dateInFuture(warmupTime), homeEntry.getHomeLocation(plugin.getServer()), amount);
                    plugin.getWarmUpManager().addWarmup(warmup);
                    Settings.sendMessageWarmup(player, warmupTime);
                } else {
                    // Can transfer instantly

                    //Double Check the charge before teleporting the player
                    if (!PermissionUtil.has(player, "multihome.free.defaulthome.go") && amount != 0) {
                        if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
                            return;
                        } else {
                            Settings.sendMessageDeductForHome(player, amount);
                        }
                    }

                    MiscUtil.teleportPlayer(player, homeEntry.getHomeLocation(plugin.getServer()), plugin);

                    int cooldownTime = Settings.getSettingCooldown(player);
                    if (cooldownTime > 0)
                        plugin.getCoolDownManager().addCooldown(player.getName(), MiscUtil.dateInFuture(cooldownTime));
                }
            } else {
                Settings.sendMessageNoDefaultHome(player);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to warp to default home location. Permission not granted.", plugin);
        }
    }

    public static void goNamedHome(MultiHomePlugin plugin, Player player, String home) {
        if (PermissionUtil.has(player, "multihome.namedhome.go")) {
            double amount = 0;

            // Get user cooldown timer.
            CoolDownEntry cooldown = plugin.getCoolDownManager().getCooldown(player.getName());

            if (cooldown != null && !PermissionUtil.has(player, "multihome.ignore.cooldown")) {
                Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getExpiry().getTime() - new Date().getTime()), 1000) / 1000);
                return;
            }

            //Check for economy first - and make sure the player either has permission for free homes or has enough money
            if (Settings.isEconomyEnabled() && !PermissionUtil.has(player, "multihome.free.namedhome.go")) {
                if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getNamedHomeCost(player))) {
                    Settings.sendMessageNotEnoughMoney(player, Settings.getNamedHomeCost(player));
                    return;
                } else {
                    amount = Settings.getNamedHomeCost(player);
                }
            }

            int warmupTime = Settings.getSettingWarmup(player);
            HomeEntry homeEntry = plugin.getHomeManager().getHome(player, home);

            if (homeEntry != null) {
                if (warmupTime > 0 && !PermissionUtil.has(player, "multihome.ignore.warmup")) {
                    // Warpup required.
                    WarmUpEntry warmup = new WarmUpEntry(player.getName(), MiscUtil.dateInFuture(warmupTime), homeEntry.getHomeLocation(plugin.getServer()), amount);
                    plugin.getWarmUpManager().addWarmup(warmup);
                    Settings.sendMessageWarmup(player, warmupTime);
                } else {
                    // Can transfer instantly

                    //Double Check the charge before teleporting the player
                    if (!PermissionUtil.has(player, "multihome.free.namedhome.go") && amount != 0) {
                        if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
                            return;
                        } else {
                            Settings.sendMessageDeductForHome(player, amount);
                        }
                    }

                    MiscUtil.teleportPlayer(player, homeEntry.getHomeLocation(plugin.getServer()), plugin);

                    int cooldownTime = Settings.getSettingCooldown(player);
                    if (cooldownTime > 0)
                        plugin.getCoolDownManager().addCooldown(player.getName(), MiscUtil.dateInFuture(cooldownTime));
                }
            } else {
                Settings.sendMessageNoHome(player, home);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to warp to home location [" + home + "]. Permission not granted.", plugin);
        }
    }

    public static void goPlayerNamedHome(MultiHomePlugin plugin, Player player, String owner, String home) {
        if (PermissionUtil.has(player, "multihome.othershome.go") || plugin.getInviteManager().getInvite(owner, home, player.getName()) != null) {
            double amount = 0;

            // Get user cooldown timer.
            CoolDownEntry cooldown = plugin.getCoolDownManager().getCooldown(player.getName());

            if (cooldown != null && !PermissionUtil.has(player, "multihome.ignore.cooldown")) {
                Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getExpiry().getTime() - new Date().getTime()), 1000) / 1000);
                return;
            }

            //Check for economy first - and make sure the player either has permission for free homes or has enough money
            if (Settings.isEconomyEnabled() && !PermissionUtil.has(player, "multihome.free.othershome.go")) {
                if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getOthersHomeCost(player))) {
                    Settings.sendMessageNotEnoughMoney(player, Settings.getOthersHomeCost(player));
                    return;
                } else {
                    amount = Settings.getOthersHomeCost(player);
                }
            }

            int warmupTime = Settings.getSettingWarmup(player);

            if (plugin.getHomeManager().getUserExists(owner)) {
                HomeEntry homeEntry = plugin.getHomeManager().getHome(owner, home);

                if (homeEntry != null) {
                    if (warmupTime > 0 && !PermissionUtil.has(player, "multihome.ignore.warmup")) {
                        // Warpup required.
                        WarmUpEntry warmup = new WarmUpEntry(player.getName(), MiscUtil.dateInFuture(warmupTime), homeEntry.getHomeLocation(plugin.getServer()), amount);
                        plugin.getWarmUpManager().addWarmup(warmup);
                        Settings.sendMessageWarmup(player, warmupTime);
                        MessageUtil.logInfo("Player " + player.getName() + " warped to player " + owner + "'s home location: " + home, plugin);
                    } else {
                        // Can transfer instantly

                        //Double Check the charge before teleporting the player
                        if (!PermissionUtil.has(player, "multihome.free.othershome.go") && amount != 0) {
                            if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
                                return;
                            } else {
                                Settings.sendMessageDeductForHome(player, amount);
                            }
                        }

                        MiscUtil.teleportPlayer(player, homeEntry.getHomeLocation(plugin.getServer()), plugin);

                        int cooldownTime = Settings.getSettingCooldown(player);
                        if (cooldownTime > 0)
                            plugin.getCoolDownManager().addCooldown(player.getName(), MiscUtil.dateInFuture(cooldownTime));
                    }
                } else {
                    Settings.sendMessageNoHome(player, owner + ":" + home);
                }
            } else {
                Settings.sendMessageNoPlayer(player, owner);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to warp to " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
        }
    }

    public static void setDefaultHome(MultiHomePlugin plugin, Player player) {
        if (PermissionUtil.has(player, "multihome.defaulthome.set")) {
            int numHomes = plugin.getHomeManager().getUserHomeCount(player);
            int maxHomes = Settings.getSettingMaxHomes(player);
            double amount = 0;

            if (numHomes < maxHomes || maxHomes == -1 || plugin.getHomeManager().getHome(player, "") != null) {
                //Check for economy first - and make sure the player either has permission for free homes or has enough money
                if (Settings.isEconomyEnabled() && !PermissionUtil.has(player, "multihome.free.defaulthome.set")) {
                    if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getSetHomeCost(player))) {
                        Settings.sendMessageNotEnoughMoney(player, Settings.getSetHomeCost(player));
                        return;
                    } else {
                        amount = Settings.getSetHomeCost(player);
                    }
                }

                //Double Check the charge before settings home
                if (!PermissionUtil.has(player, "multihome.free.defaulthome.set") && amount != 0) {
                    if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
                        return;
                    } else {
                        Settings.sendMessageDeductForHome(player, amount);
                    }
                }

                plugin.getHomeManager().addHome(player, "", player.getLocation());
                Settings.sendMessageDefaultHomeSet(player);
                MessageUtil.logInfo("Player " + player.getName() + " set defult home location", plugin);
            } else {
                Settings.sendMessageMaxHomes(player, numHomes, maxHomes);
                MessageUtil.logInfo("Player " + player.getName() + " tried to set default home location. Too many set already.", plugin);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to set default home location. Permission not granted.", plugin);
        }
    }

    public static void setNamedHome(MultiHomePlugin plugin, Player player, String home) {
        if (PermissionUtil.has(player, "multihome.namedhome.set")) {
            int numHomes = plugin.getHomeManager().getUserHomeCount(player);
            int maxHomes = Settings.getSettingMaxHomes(player);
            double amount = 0;

            if (numHomes < maxHomes || maxHomes == -1 || plugin.getHomeManager().getHome(player, home) != null) {
                //Check for economy first - and make sure the player either has permission for free homes or has enough money
                if (Settings.isEconomyEnabled() && !PermissionUtil.has(player, "multihome.free.namedhome.set")) {
                    if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getSetNamedHomeCost(player))) {
                        Settings.sendMessageNotEnoughMoney(player, Settings.getSetNamedHomeCost(player));
                        return;
                    } else {
                        amount = Settings.getSetNamedHomeCost(player);
                    }
                }

                //Double Check the charge before settings home
                if (!PermissionUtil.has(player, "multihome.free.namedhome.set") && amount != 0) {
                    if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
                        return;
                    } else {
                        Settings.sendMessageDeductForHome(player, amount);
                    }
                }

                plugin.getHomeManager().addHome(player, home, player.getLocation());
                Settings.sendMessageHomeSet(player, home);
                MessageUtil.logInfo("Player " + player.getName() + " set home location [" + home + "]", plugin);
            } else {
                Settings.sendMessageMaxHomes(player, numHomes, maxHomes);
                MessageUtil.logInfo("Player " + player.getName() + " tried to set home location [" + home + "]. Too many set already.", plugin);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to set home location [" + home + "]. Permission not granted.", plugin);
        }
    }

    public static void setPlayerNamedHome(MultiHomePlugin plugin, Player player, String owner, String home) {
        if (PermissionUtil.has(player, "multihome.othershome.set")) {
            plugin.getHomeManager().addHome(owner, home, player.getLocation());
            Settings.sendMessageHomeSet(player, owner + ":" + home);
            MessageUtil.logInfo("Player " + player.getName() + " set player " + owner + "'s home location [" + home + "]", plugin);
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to set player " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
        }
    }

    public static void deleteDefaultHome(MultiHomePlugin plugin, Player player) {
        if (plugin.getHomeManager().getHome(player, "") != null) {
            Settings.sendMessageCannotDeleteDefaultHome(player);
        } else {
            Settings.sendMessageNoDefaultHome(player);
        }
        MessageUtil.logInfo("Player " + player.getName() + " tried to delete deafult home location. Cannot do.", plugin);
    }

    public static void deleteNamedHome(MultiHomePlugin plugin, Player player, String home) {
        if (PermissionUtil.has(player, "multihome.namedhome.delete")) {
            if (plugin.getHomeManager().getHome(player, home) != null) {
                plugin.getHomeManager().removeHome(player, home);
                Settings.sendMessageHomeDeleted(player, home);
                MessageUtil.logInfo("Player " + player.getName() + " deleted home location [" + home + "].", plugin);
            } else {
                Settings.sendMessageNoHome(player, home);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to delete home location [" + home + "]. Permission not granted.", plugin);
        }
    }

    public static void deletePlayerNamedHome(MultiHomePlugin plugin, Player player, String owner, String home) {
        if (PermissionUtil.has(player, "multihome.othershome.delete")) {
            if (plugin.getHomeManager().getHome(owner, home) != null) {
                plugin.getHomeManager().removeHome(owner, home);
                Settings.sendMessageHomeDeleted(player, owner + ":" + home);
                MessageUtil.logInfo("Player " + player.getName() + " deleted " + owner + "'s home location [" + home + "].", plugin);
            } else {
                Settings.sendMessageNoHome(player, home);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to delete " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
        }
    }


    public static void listPlayerHomesConsole(MultiHomePlugin plugin, CommandSender sender, String owner) {

    }

    public static void inviteDefaultHome(MultiHomePlugin plugin, Player player, String target) {
        if (PermissionUtil.has(player, "multihome.defaulthome.invite")) {
            if (plugin.getHomeManager().getHome(player, "") != null) {
                plugin.getInviteManager().addInvite(player.getName(), "", target);
                Settings.sendMessageInviteOwnerHome(player, target, "");

                Player targetPlayer = MiscUtil.getExactPlayer(target, plugin);
                if (targetPlayer != null) {
                    Settings.sendMessageInviteTargetHome(targetPlayer, player.getName(), "");
                }

                MessageUtil.logInfo("Player " + player.getName() + " invited " + target + " to their default home.", plugin);
            } else {
                Settings.sendMessageNoDefaultHome(player);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to invite " + target + " to their default home. Permission not granted.", plugin);
        }
    }

    public static void inviteNamedHome(MultiHomePlugin plugin, Player player, String target, String home) {
        if (PermissionUtil.has(player, "multihome.namedhome.invite")) {
            if (plugin.getHomeManager().getHome(player, home) != null) {
                plugin.getInviteManager().addInvite(player.getName(), home, target);
                Settings.sendMessageInviteOwnerHome(player, target, home);

                Player targetPlayer = MiscUtil.getExactPlayer(target, plugin);
                if (targetPlayer != null) {
                    Settings.sendMessageInviteTargetHome(targetPlayer, player.getName(), home);
                }

                MessageUtil.logInfo("Player " + player.getName() + " invited " + target + " to their home location [" + home + "].", plugin);
            } else {
                Settings.sendMessageNoHome(player, home);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to invite " + target + " to their home location [" + home + "]. Permission not granted.", plugin);
        }
    }


    public static void inviteDefaultTimedHome(MultiHomePlugin plugin, Player player, String target, int time) {
        if (PermissionUtil.has(player, "multihome.defaulthome.invitetimed")) {
            if (plugin.getHomeManager().getHome(player, "") != null) {
                plugin.getInviteManager().addInvite(player.getName(), "", target, MiscUtil.dateInFuture(time));
                Settings.sendMessageInviteTimedOwnerHome(player, target, "", time);

                Player targetPlayer = MiscUtil.getExactPlayer(target, plugin);
                if (targetPlayer != null) {
                    Settings.sendMessageInviteTimedTargetHome(targetPlayer, player.getName(), "", time);
                }

                MessageUtil.logInfo("Player " + player.getName() + " invited " + target + " to their default home for " + time + " seconds.", plugin);
            } else {
                Settings.sendMessageNoDefaultHome(player);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to invite " + target + " to their default home for " + time + " seconds. Permission not granted.", plugin);
        }
    }

    public static void inviteNamedTimedHome(MultiHomePlugin plugin, Player player, String target, int time, String home) {
        if (PermissionUtil.has(player, "multihome.namedhome.invitetimed")) {
            if (plugin.getHomeManager().getHome(player, home) != null) {
                plugin.getInviteManager().addInvite(player.getName(), home, target, MiscUtil.dateInFuture(time));
                Settings.sendMessageInviteOwnerHome(player, target, home);

                Player targetPlayer = MiscUtil.getExactPlayer(target, plugin);
                if (targetPlayer != null) {
                    Settings.sendMessageInviteTimedTargetHome(targetPlayer, player.getName(), home, time);
                }

                MessageUtil.logInfo("Player " + player.getName() + " invited " + target + " to their home location [" + home + "] for " + time + " seconds.", plugin);
            } else {
                Settings.sendMessageNoHome(player, home);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to invite " + target + " to their home location [" + home + "] for " + time + " seconds. Permission not granted.", plugin);
        }
    }

    public static void uninviteDefaultHome(MultiHomePlugin plugin, Player player, String target) {
        if (PermissionUtil.has(player, "multihome.defaulthome.uninvite")) {
            if (plugin.getInviteManager().getInvite(player.getName(), "", target) != null) {
                plugin.getInviteManager().removeInvite(player.getName(), "", target);

                Settings.sendMessageUninviteOwnerHome(player, target, "");

                Player targetPlayer = MiscUtil.getExactPlayer(target, plugin);
                if (targetPlayer != null) {
                    Settings.sendMessageUninviteTargetHome(targetPlayer, player.getName(), "[Default]");
                }

                MessageUtil.logInfo("Player " + player.getName() + " removed invite for " + target + " to their default home.", plugin);
            } else {
                Settings.sendMessageNoDefaultHome(player);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to remove invite for " + target + " to their default home. Permission not granted.", plugin);
        }
    }

    public static void uninviteNamedHome(MultiHomePlugin plugin, Player player, String target, String home) {
        if (PermissionUtil.has(player, "multihome.namedhome.uninvite")) {
            if (plugin.getInviteManager().getInvite(player.getName(), home, target) != null) {
                plugin.getInviteManager().removeInvite(player.getName(), home, target);
                Settings.sendMessageUninviteOwnerHome(player, target, home);

                Player targetPlayer = MiscUtil.getExactPlayer(target, plugin);
                if (targetPlayer != null) {
                    Settings.sendMessageUninviteTargetHome(targetPlayer, player.getName(), home);
                }

                MessageUtil.logInfo("Player " + player.getName() + " removed invite for " + target + " to their home location [" + home + "].", plugin);
            } else {
                Settings.sendMessageNoHome(player, home);
            }
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to remove invite for " + target + " to their home location [" + home + "]. Permission not granted.", plugin);
        }
    }

    public static void listInvitesToMe(MultiHomePlugin plugin, Player player) {
        if (PermissionUtil.has(player, "multihome.listinvites.tome")) {
            ArrayList<InviteEntry> invites = plugin.getInviteManager().listPlayerInvitesToMe(player.getName());

            Settings.sendMessageInviteListToMe(player, player.getName(), MiscUtil.compileInviteListForMe(player.getName(), invites));
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to list invitations open to them. Permission not granted.", plugin);
        }
    }

    public static void listInvitesToOthers(MultiHomePlugin plugin, Player player) {
        if (PermissionUtil.has(player, "multihome.listinvites.toothers")) {
            ArrayList<InviteEntry> invites = plugin.getInviteManager().listPlayerInvitesToOthers(player.getName());

            Settings.sendMessageInviteListToOthers(player, player.getName(), MiscUtil.compileInviteListForOthers(invites));
        } else {
            MessageUtil.logInfo("Player " + player.getName() + " tried to list invitations they've given. Permission not granted.", plugin);
        }
    }
}
