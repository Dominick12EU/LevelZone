package me.dominick.levelzone;

import com.alonsoaliaga.alonsolevels.api.AlonsoLevelsAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Set;

public class MinLevelFlag extends Handler {
    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<MinLevelFlag> {
        @Override
        public MinLevelFlag create(Session session) {
            return new MinLevelFlag(session);
        }
    }

    public MinLevelFlag(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        int levelRequired = toSet.queryAllValues(player, WGAlonsoLevels.MIN_LEVEL).stream().max(Integer::compareTo).orElse(-1);
        boolean allowed = levelRequired <= AlonsoLevelsAPI.getLevel(player.getUniqueId());

        WGAlonsoLevels plugin = WGAlonsoLevels.getPlugin(WGAlonsoLevels.class);
        FileConfiguration config = plugin.getConfig();
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        if (!getSession().getManager().hasBypass(player, (World) to.getExtent()) && !allowed && moveType.isCancellable()) {
            String message = config.getString("message");
            if (message != null && !message.isEmpty()) {
                player.printRaw(CommandUtils.replaceColorMacros(message.replace("<level>", String.valueOf(levelRequired))));
            }

            String title = config.getString("title");
            String subtitle = config.getString("subtitle");
            if (title == null) title = "";
            if (subtitle == null) subtitle = "";
            if (!title.isEmpty() || !subtitle.isEmpty()) {
                bukkitPlayer.sendTitle(CommandUtils.replaceColorMacros(title.replace("<level>", String.valueOf(levelRequired))), CommandUtils.replaceColorMacros(subtitle.replace("<level>", String.valueOf(levelRequired))), 0, 20 * 2, 10);
            }
            WGAlonsoLevels.debug(String.format("Not allowed: from %.02f,%.02f,%.02f to %.02f,%.02f,%.02f", from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ()));

            String actionbar = config.getString("actionbar");
            if (actionbar != null && !actionbar.isEmpty()) {
                bukkitPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(CommandUtils.replaceColorMacros(actionbar.replace("<level>", String.valueOf(levelRequired)))));
            }

            int fromX = from.getBlockX();
            int fromY = from.getBlockY();
            int fromZ = from.getBlockZ();
            int changeX = to.getBlockX() - fromX;
            int changeY = to.getBlockY() - fromY;
            int changeZ = to.getBlockZ() - fromZ;
            if (Math.max(changeX, Math.max(changeY, changeZ)) > 1) {
                return false;
            }
            boolean restrictedX = toSet.getRegions().stream().allMatch(region -> region.contains(fromX + changeX, fromY, fromZ));
            boolean restrictedY = toSet.getRegions().stream().allMatch(region -> region.contains(fromX, fromY + changeY, fromZ));
            boolean restrictedZ = toSet.getRegions().stream().allMatch(region -> region.contains(fromX, fromY, fromZ + changeZ));
            boolean restrictedXZ = !restrictedX && !restrictedZ && toSet.getRegions().stream().allMatch(region -> region.contains(fromX + changeX, fromY, fromZ + changeZ));

            if (restrictedY) {
                bukkitPlayer.teleport(BukkitAdapter.adapt(from));
                return false;
            }

            Vector knockback = new Vector(restrictedX ? Math.signum(changeX) : 0, -0.5, restrictedZ ? Math.signum(changeZ) : 0)
                    .normalize()
                    .multiply(-1);
            if (restrictedXZ) {
                knockback = new Vector(Math.signum(changeX), -0.75, Math.signum(changeZ))
                        .normalize()
                        .multiply(-1);
            }
            if (bukkitPlayer.isFlying()) {
                knockback.setY(0);
            }
            bukkitPlayer.setVelocity(knockback);
            String soundBlock = config.getString("sound-block");
            if (soundBlock != null) {
                bukkitPlayer.playSound(bukkitPlayer.getLocation(), soundBlock, SoundCategory.MASTER, 1f, 1f);
            }
        } else if (levelRequired > 0) {
            int levelRequiredBeforeEntering = toSet.getRegions()
                    .stream()
                    .filter(region -> !entered.contains(region))
                    .map(region -> region.getFlag(WGAlonsoLevels.MIN_LEVEL))
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(0);
            if (levelRequiredBeforeEntering <= 0) {
                String soundEnter = config.getString("sound-enter");
                if (soundEnter != null) {
                    bukkitPlayer.playSound(bukkitPlayer.getLocation(), soundEnter, SoundCategory.MASTER, 1f, 1f);
                }
            }
        }
        return true;
    }
}