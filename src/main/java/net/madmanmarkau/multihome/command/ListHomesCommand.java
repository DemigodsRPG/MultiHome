package net.madmanmarkau.multihome.command;

import com.censoredsoftware.library.command.type.BaseCommand;
import com.censoredsoftware.library.command.type.CommandResult;
import net.madmanmarkau.multihome.MultiHomePlugin;
import net.madmanmarkau.multihome.Settings;
import net.madmanmarkau.multihome.data.HomeEntry;
import net.madmanmarkau.multihome.util.MessageUtil;
import net.madmanmarkau.multihome.util.MiscUtil;
import net.madmanmarkau.multihome.util.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListHomesCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command cmd, String[] args) {
        if ("listhomes".equals(cmd.getName())) {
            if (sender instanceof ConsoleCommandSender) {
                if (args.length == 1) {
                    List<HomeEntry> homes = MultiHomePlugin.getInst().getHomeManager().listUserHomes(args[0]);
                    Settings.sendMessageOthersHomeList(sender, args[0], MiscUtil.compileHomeList(homes));
                    return CommandResult.SUCCESS;
                }
            } else {
                Player player = (Player) sender;
                if (args.length == 0) {
                    return listHomes(player);
                } else if (args.length == 1) {
                    return listPlayerHomes(player, args[0]);
                }
            }
        }
        return CommandResult.INVALID_SYNTAX;
    }

    public CommandResult listHomes(Player player) {
        if (PermissionUtil.has(player, "multihome.namedhome.list")) {
            List<HomeEntry> homes = MultiHomePlugin.getInst().getHomeManager().listUserHomes(player);
            Settings.sendMessageHomeList(player, MiscUtil.compileHomeList(homes));
            return CommandResult.SUCCESS;
        }
        MessageUtil.logInfo("Player " + player.getName() + " tried to list home locations. Permission not granted.");
        return CommandResult.NO_PERMISSIONS;
    }

    public CommandResult listPlayerHomes(Player player, String owner) {
        if (PermissionUtil.has(player, "multihome.othershome.list")) {
            List<HomeEntry> homes = MultiHomePlugin.getInst().getHomeManager().listUserHomes(owner);
            Settings.sendMessageOthersHomeList(player, owner, MiscUtil.compileHomeList(homes));
            return CommandResult.SUCCESS;
        }
        MessageUtil.logInfo("Player " + player.getName() + " tried to list " + owner + "'s home locations. Permission not granted.");
        return CommandResult.NO_PERMISSIONS;
    }
}
