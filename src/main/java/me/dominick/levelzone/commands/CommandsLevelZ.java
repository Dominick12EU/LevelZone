package me.dominick.levelzone.commands;

import com.alonsoaliaga.alonsolevels.api.AlonsoLevelsAPI;
import me.dominick.levelzone.WGAlonsoLevels;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

@Command("levelzone")
@Alias("levelz")
public class CommandsLevelZ extends CommandBase {

    private final WGAlonsoLevels plugin;

    public CommandsLevelZ(WGAlonsoLevels plugin) {
        this.plugin = plugin;
    }

    public static String msg(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Default
    public void defaultCommand(CommandSender sender) {
        final String version = plugin.getDescription().getVersion();
        if (!sender.hasPermission("levelzone.admin")) {
            sender.sendMessage(msg("&7The server is running &aLevelZone &7v" + version + " By Dominick12"));
            return;
        }

        sender.sendMessage("");
        sender.sendMessage(msg("&a&lLevelZone &7v" + version));
        sender.sendMessage(msg("  &7By Dominick12"));
        sender.sendMessage("");
        sender.sendMessage(msg("&a&lCommands"));
        sender.sendMessage(msg("  &f/levelzone reload &7- &fReload Plugin"));
        sender.sendMessage(msg("  &f/levelzone check &7- &fCheck Plugin"));
        sender.sendMessage(msg("  &f/levelzone stats &7- &fCheck Your Levels"));
        sender.sendMessage("");
    }

    @SubCommand("reload")
    @Permission("levelzone.admin")
    public void reloadSubCommand(CommandSender sender) {
        plugin.onReload();
        sender.sendMessage(msg("&a&lLevelZone &8> &fThe configuration has been reloaded"));
    }

    @SubCommand("check")
    @Permission("levelzone.admin")
    public void statusSubCommand(CommandSender sender) {
        final PluginManager pm = getServer().getPluginManager();
        final String version = plugin.getDescription().getVersion();
        sender.sendMessage(msg("&a&lAddons Check:"));
        sender.sendMessage(msg("  &7AlonsoLevels: " + (pm.isPluginEnabled("AlonsoLevels") ? "&a\u2714" : "&c\u2718")));
        sender.sendMessage(msg("  &7MMOCore: " + (pm.isPluginEnabled("MMOCore") ? "&a\u2714" : "&c\u2718")));
        sender.sendMessage("");
        sender.sendMessage(msg("&a&lInfo:"));
        sender.sendMessage(msg("  &7Author: &fDominick12"));
        sender.sendMessage(msg("  &7Version: &fv" + version));
    }

    @SubCommand("stats")
    @Permission("levelzone.stats")
    public void statsSubCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("&cYou must be a player to use this command."));
            return;
        }

        Player player = (Player) sender;
        int alevels = -1;
        int mmolevels = -1;

        if (Bukkit.getPluginManager().isPluginEnabled("AlonsoLevels")) {
            alevels = AlonsoLevelsAPI.getLevel(player.getUniqueId());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("mmocore")) {
            mmolevels = PlayerData.get(player.getUniqueId()).getLevel();
        }

        FileConfiguration config = plugin.getConfig();
        List<String> stats = config.getStringList("stats");

        for (String level : stats) {
            if (alevels != -1) {
                level = level.replace("<alonso-level>", String.valueOf(alevels));
            } else {
                level = level.replace("<alonso-level>", "N/A");
            }

            if (mmolevels != -1) {
                level = level.replace("<mmo-level>", String.valueOf(mmolevels));
            } else {
                level = level.replace("<mmo-level>", "N/A");
            }

            sender.sendMessage(msg(level));
        }
    }
}
