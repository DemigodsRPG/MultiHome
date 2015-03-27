package net.madmanmarkau.multihome;

import net.madmanmarkau.multihome.command.ListHomesCommand;
import net.madmanmarkau.multihome.data.*;
import net.madmanmarkau.multihome.util.MessageUtil;
import net.madmanmarkau.multihome.util.PermissionUtil;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MultiHomePlugin extends JavaPlugin {
    private static MultiHomePlugin INST;

    private HomeManager homes;
    private InviteManager invites;
    private WarmUpManager warmups;
    private CoolDownManager cooldowns;

    private String pluginDataPath;

    private MultiHomePlayerListener playerListener = new MultiHomePlayerListener(this);
    private MultiHomeEntityListener entityListener = new MultiHomeEntityListener(this);

    public static MultiHomePlugin getInst() {
        return INST;
    }

    @Override
    public void onEnable() {
        INST = this;

        pluginDataPath = this.getDataFolder().getAbsolutePath() + File.separator;

        File dataPath = new File(pluginDataPath);
        if (!dataPath.exists()) {
            dataPath.mkdirs();
        }

        if (!PermissionUtil.initialize(this)) return;
        Settings.initialize(this);
        Settings.loadSettings();
        MultiHomeEconManager.initialize(this);

        this.homes = new HomeManagerFile(this);
        this.invites = new InviteManagerFile(this);
        this.warmups = new WarmUpManagerFile(this);
        this.cooldowns = new CoolDownManagerFile(this);

        setupCommands();
        registerEvents();

        MessageUtil.logInfo("Version " + this.getDescription().getVersion() + " loaded.");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        warmups.clearWarmups();
        MessageUtil.logInfo("Version " + this.getDescription().getVersion() + " unloaded.");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(entityListener, this);
    }

    private void setupCommands() {
        getCommand("home").setExecutor(commandExecutor);
        getCommand("sethome").setExecutor(commandExecutor);
        getCommand("deletehome").setExecutor(commandExecutor);
        getCommand("listhomes").setExecutor(new ListHomesCommand());
        getCommand("invitehome").setExecutor(commandExecutor);
        getCommand("invitehometimed").setExecutor(commandExecutor);
        getCommand("uninvitehome").setExecutor(commandExecutor);
        getCommand("listinvites").setExecutor(commandExecutor);
        getCommand("listmyinvites").setExecutor(commandExecutor);
    }

    public HomeManager getHomeManager() {
        return this.homes;
    }

    public InviteManager getInviteManager() {
        return this.invites;
    }

    public WarmUpManager getWarmUpManager() {
        return this.warmups;
    }

    public CoolDownManager getCoolDownManager() {
        return this.cooldowns;
    }

    public String getPluginDataPath() {
        return this.pluginDataPath;
    }
}
