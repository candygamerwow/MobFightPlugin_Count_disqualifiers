package org.mobfightplugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MobFightListener implements Listener{
    private final MobFightPlugin plugin;
    private int playerDeaths = 0;
    private final String worldName = "mob_fighting";
    private final String logFolderName = "MobFightPlugin_log";
    private final String logFileName = "banned_mob_fight_players.log";

    public MobFightListener(MobFightPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler

    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Check if the event occurred in the mob_fighting world
        if (player.getWorld().getName().equalsIgnoreCase(worldName)) {
            playerDeaths++;

            if (playerDeaths <= 10) {
                Component kickMessage = Component.text("You have been disqualified", NamedTextColor.RED);
                player.kick(kickMessage);
                plugin.getServer().getBanList(BanList.Type.NAME).addBan(player.getName(), "Disqualified", null, null);

                // Log the banned player's name to the log file
                logBannedPlayer(player.getName());

                // Send a message to the console
                plugin.getLogger().info(player.getName() + " was disqualified from the event (" + playerDeaths + "/10)");

                // Send a message to all players on the server
                Component disqualifiedMessage = Component.text(player.getName() + " was disqualified from the event (" + playerDeaths + "/10)", NamedTextColor.RED);
                Bukkit.broadcast(disqualifiedMessage);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        // Check if the event occurred in the mob_fighting world
        if (player.getWorld().getName().equalsIgnoreCase(worldName)) {
            if (event.getRightClicked() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    private void logBannedPlayer(String playerName) {
        File logFolder = new File(plugin.getDataFolder().getParentFile(), logFolderName);
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }

        Path logFilePath = Paths.get(logFolder.getPath(), logFileName);
        try {
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
                Files.writeString(logFilePath, "The banned players (remove ban at the end of the event):\n", StandardOpenOption.APPEND);
            }

            Files.writeString(logFilePath, playerName + "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to log banned player: " + playerName);
            e.printStackTrace();
        }
    }
}
