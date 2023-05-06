package me.dominick.levelzone.commands;

import me.dominick.levelzone.WGAlonsoLevels;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Command("levelzone")
@Alias("levelz")
public class Reload extends CommandBase {

    private final WGAlonsoLevels plugin;

    public Reload(WGAlonsoLevels plugin) {
        this.plugin = plugin;
    }

    @Default
    public void defaultCommand(CommandSender sender) {
        final String version = plugin.getDescription().getVersion();
        if (!sender.hasPermission("levelzone.admin")) {
            sender.sendMessage(ChatColor.GRAY + "The server is running " + ChatColor.GREEN + "LevelZone " + ChatColor.GRAY + "v" +version + " By Dominick12");
            return;
        }

        sender.sendMessage(ChatColor.RESET + "");
        sender.sendMessage(ChatColor.GREEN + " " + ChatColor.BOLD + "LevelZone " + ChatColor.GRAY + "v" +version);
        sender.sendMessage(ChatColor.GRAY + "  By Dominick12");
        sender.sendMessage(ChatColor.RESET + "");
        sender.sendMessage(ChatColor.GREEN + " " + ChatColor.BOLD + "Commands");
        sender.sendMessage(ChatColor.WHITE + "  /levelzone reload " + ChatColor.GRAY + "- " + ChatColor.WHITE + "Reload Plugin");
        sender.sendMessage(ChatColor.RESET + "");
    }

    @SubCommand("reload")
    @Permission("levelzone.admin")
    public void reloadSubCommand(CommandSender sender) {
        plugin.onReload();
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LevelZone " + ChatColor.DARK_GRAY + "> " + ChatColor.WHITE + "The configuration has been reloaded");
    }
}
