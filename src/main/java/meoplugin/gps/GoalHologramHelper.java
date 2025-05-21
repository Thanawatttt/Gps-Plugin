package meoplugin.gps;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class GoalHologramHelper {
    private final Plugin plugin;
    private TextDisplay display;

    public GoalHologramHelper(Plugin plugin) {
        this.plugin = plugin;
    }

    public void show(Player player, Location loc, String text) {
        remove();
        World world = loc.getWorld();
        if (world == null) return;
        display = world.spawn(loc.clone().add(0, 2.5, 0), TextDisplay.class, td -> {
            td.setText(text);
            td.setBillboard(Billboard.CENTER);
            td.setSeeThrough(true);
            td.setViewRange(100);
            td.setShadowed(true);
            td.setGlowColorOverride(org.bukkit.Color.LIME);
            td.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new org.joml.Quaternionf(),
                    new Vector3f(2.5f, 2.5f, 2.5f),
                    new org.joml.Quaternionf()
            ));
        });
        for (Player p : world.getPlayers()) {
            if (!p.equals(player)) p.hideEntity(plugin, display);
        }
    }

    public void remove() {
        if (display != null && display.isValid()) display.remove();
        display = null;
    }
}
