package meoplugin.gps;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class HologramHelper {
    private final Plugin plugin;
    private TextDisplay display;

    public HologramHelper(Plugin plugin) {
        this.plugin = plugin;
    }

    public void show(Player player, Location loc, String text) {
        if (display == null || !display.isValid()) {
            World world = loc.getWorld();
            if (world == null) return;
            display = world.spawn(loc, TextDisplay.class, td -> {
                td.setBillboard(Billboard.CENTER);
                td.setSeeThrough(true);
                td.setViewRange(100);
                td.setShadowed(true);
                td.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new org.joml.Quaternionf(),
                        new Vector3f(3.5f, 3.5f, 3.5f),
                        new org.joml.Quaternionf()
                ));
            });
            for (Player p : world.getPlayers()) {
                if (!p.equals(player)) p.hideEntity(plugin, display);
            }
        }
        display.setText(text);
        display.teleport(loc);
    }

    public void remove() {
        if (display != null && display.isValid()) display.remove();
        display = null;
    }
}
