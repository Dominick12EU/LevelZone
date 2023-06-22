package me.dominick.levelzone;

import me.dominick.levelzone.commands.CommandsLevelZ;
import me.dominick.levelzone.metrics.bStats;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import me.dominick.levelzone.utils.ConfigManager;
import me.dominick.levelzone.utils.UpdaterChecker;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WGAlonsoLevels extends JavaPlugin {

    private static WGAlonsoLevels instance;
    private ConfigManager configManager;
    public static IntegerFlag MIN_LEVEL;
    public static IntegerFlag MIN_CLASS_LEVEL;

    public WGAlonsoLevels() {
        instance = this;
    }

    public static WGAlonsoLevels getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            if (instance.getServer().getPluginManager().getPlugin("AlonsoLevels") != null) {
                IntegerFlag ml = new IntegerFlag("min-level");
                registry.register(ml);
                MIN_LEVEL = ml;
            }
            if (instance.getServer().getPluginManager().getPlugin("MMOCore") != null) {
                IntegerFlag mcl = new IntegerFlag("min-class-level");
                registry.register(mcl);
                MIN_CLASS_LEVEL = mcl;
            }
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        new bStats(this, 17364);

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();

        if (instance.getServer().getPluginManager().getPlugin("AlonsoLevels") != null) {
            sessionManager.registerHandler(MinLevelFlag.FACTORY, null);
            getLogger().info("AlonsoLevels Support Enabled!");
        }
        if (instance.getServer().getPluginManager().getPlugin("MMOCore") != null) {
            sessionManager.registerHandler(MinClassLevelFlag.FACTORY, null);
            getLogger().info("MMOCore Support Enabled!");
        }

        CommandManager commandManager = new CommandManager(this, true);
        commandManager.register(new CommandsLevelZ(this));

        getLogger().info("----------------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("");
        getLogger().info("----------------------------------------");
        new UpdaterChecker(this, 106294).getLatestVersion(version -> {
            if(this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("Plugin is up to date.");
            } else {
                getLogger().warning("********************************************************************************");
                getLogger().warning("* There is a new version of LevelZone available!");
                getLogger().warning("*   Your version: " + getDescription().getVersion());
                getLogger().warning("*");
                getLogger().warning("* Please update to the newest version.");
                getLogger().warning("*");
                getLogger().warning("* Download:");
                getLogger().warning("*    https://www.spigotmc.org/resources/106294/");
                getLogger().warning("********************************************************************************");
            }
        });
    }

    public void onReload() {
        reloadConfig();
    }

    /* package-private*/ static void debug(String log) {
        WGAlonsoLevels plugin = getPlugin(WGAlonsoLevels.class);
        if(plugin.getConfig().getBoolean("debug")) {
            plugin.getLogger().info("DEBUG: " + log);
        }
    }
}
