package me.dominick.levelzone;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WGAlonsoLevels extends JavaPlugin {
    public static IntegerFlag MIN_LEVEL;

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            IntegerFlag flag = new IntegerFlag("min-level");
            registry.register(flag);
            MIN_LEVEL = flag;
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(MinLevelFlag.FACTORY, null);
    }
}
