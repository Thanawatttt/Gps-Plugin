package meoplugin.gps;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.entity.Player;

public class GPSListener implements Listener {
    private final GPSPlugin plugin;
    public GPSListener(GPSPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getGPSManager().stopNavigation(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getGPSManager().isPlayerInValidWorld(player)) {
            plugin.getGPSManager().stopNavigation(player);
        }
    }
}