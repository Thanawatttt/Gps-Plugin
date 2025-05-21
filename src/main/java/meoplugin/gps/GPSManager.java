package meoplugin.gps;

import com.google.gson.*;
import meoplugin.gps.models.GPSRoute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import java.io.*;
import java.util.*;

public class GPSManager {
    private final GPSPlugin plugin;
    private final Map<String, GPSRoute> routes;
    private final Map<UUID, NavigationSession> activeSessions;
    private final File dataFile;

    public GPSManager(GPSPlugin plugin) {
        this.plugin = plugin;
        this.routes = new HashMap<>();
        this.activeSessions = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "gps_data.json");
        loadData();
    }

    public void createRoute(String name) {
        routes.put(name, new GPSRoute(name));
        saveData();
    }

    public void addPoint(String routeName, Location location) {
        GPSRoute route = routes.get(routeName);
        if (route != null) {
            route.addPoint(location);
            saveData();
        }
    }

    public void removeRoute(String name) {
        routes.remove(name);
        saveData();
    }

    public void removePoint(String routeName, int index) {
        GPSRoute route = routes.get(routeName);
        if (route != null) {
            route.removePoint(index);
            saveData();
        }
    }

    public List<String> getRouteNames() {
        return new ArrayList<>(routes.keySet());
    }

    public void startNavigation(String routeName, Player player) {
        GPSRoute route = routes.get(routeName);
        if (route != null && !route.getPoints().isEmpty()) {
            stopNavigation(player);
            NavigationSession session = new NavigationSession(player, route);
            activeSessions.put(player.getUniqueId(), session);
            session.start();
        }
    }

    public void stopNavigation(Player player) {
        NavigationSession session = activeSessions.remove(player.getUniqueId());
        if (session != null) {
            session.stop();
        }
    }

    public void saveAllData() {
        saveData();
    }

    // Stop GPS if player is in world not in any point of route they're navigating
    public boolean isPlayerInValidWorld(Player player) {
        NavigationSession session = activeSessions.get(player.getUniqueId());
        if (session == null) return true;
        String currentWorld = player.getWorld().getName();
        for (Location point : session.route.getPoints()) {
            if (point.getWorld().getName().equals(currentWorld)) return true;
        }
        return false;
    }

    private void saveData() {
        try (Writer writer = new FileWriter(dataFile)) {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, GPSRoute> entry : routes.entrySet()) {
                JsonArray arr = new JsonArray();
                for (Location loc : entry.getValue().getPoints()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("world", loc.getWorld().getName());
                    o.addProperty("x", loc.getX());
                    o.addProperty("y", loc.getY());
                    o.addProperty("z", loc.getZ());
                    arr.add(o);
                }
                json.add(entry.getKey(), arr);
            }
            new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        if (!dataFile.exists()) return;
        try (Reader reader = new FileReader(dataFile)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (String key : json.keySet()) {
                JsonArray arr = json.getAsJsonArray(key);
                GPSRoute route = new GPSRoute(key);
                for (JsonElement el : arr) {
                    JsonObject o = el.getAsJsonObject();
                    String world = o.get("world").getAsString();
                    double x = o.get("x").getAsDouble();
                    double y = o.get("y").getAsDouble();
                    double z = o.get("z").getAsDouble();
                    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                    route.addPoint(loc);
                }
                routes.put(key, route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== INNER CLASS ===================

    public class NavigationSession {
        private final Player player;
        private final GPSRoute route;
        private int currentPoint;
        private BukkitTask task;
        private final HologramHelper directionHolo; // ข้อความนำทาง
        private final HologramHelper goalHolo;      // เป้าหมาย

        public NavigationSession(Player player, GPSRoute route) {
            this.player = player;
            this.route = route;
            this.currentPoint = 0;
            this.directionHolo = new HologramHelper(plugin);
            this.goalHolo = new HologramHelper(plugin);
        }

        public void start() {
            updateHolograms();

            task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (currentPoint >= route.getPoints().size()) {
                    stop();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aGPS: ถึงจุดหมายแล้ว!"));
                    return;
                }

                Location target = route.getPoints().get(currentPoint);
                Location playerLoc = player.getLocation();
                double distance = playerLoc.distance(target);

                // ข้อความนำทาง (ตรงไป/เลี้ยวซ้าย/เลี้ยวขวา/กลับหลังหัน)
                String directionText = getDirectionText(playerLoc, target);
                // ลอยข้อความข้างหน้าผู้เล่น (เห็นใน first person)
                Location holoPos = getFrontLocation(playerLoc, 2.5, 1.5);
                directionHolo.show(player, holoPos, directionText);

                // อัปเดต hologram เป้าหมาย (แสดงระยะห่าง)
                Location goal = route.getPoints().get(currentPoint);
                goalHolo.show(player, goal.clone().add(0, 2.5, 0),
                        "§a§lจุดที่ " + (currentPoint + 1) + "\n§7ระยะ " + (int) player.getLocation().distance(goal) + " m");

                player.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        new TextComponent("§bGPS: อีก §e" + (int) distance + "§b m ถึงจุดที่ " + (currentPoint + 1) + "/" + route.getPoints().size())
                );

                if (distance <= 3) {
                    currentPoint++;
                    updateHolograms();
                }
            }, 0L, 2L);
        }

        private void updateHolograms() {
            if (currentPoint < route.getPoints().size()) {
                Location goal = route.getPoints().get(currentPoint);
                goalHolo.show(player, goal.clone().add(0, 2.5, 0),
                        "§a§lจุดที่ " + (currentPoint + 1) + "\n§7ระยะ " + (int) player.getLocation().distance(goal) + " m");
                // ข้อความจะถูกอัปเดตใน loop อยู่แล้ว
            } else {
                goalHolo.remove();
                directionHolo.remove();
            }
        }

        public void stop() {
            if (task != null) task.cancel();
            directionHolo.remove();
            goalHolo.remove();
        }

        // คำนวณทิศทางข้อความ (ตรงไป/เลี้ยวซ้าย/เลี้ยวขวา/กลับหลังหัน)
        private String getDirectionText(Location playerLoc, Location targetLoc) {
            float playerYaw = playerLoc.getYaw();
            double dx = targetLoc.getX() - playerLoc.getX();
            double dz = targetLoc.getZ() - playerLoc.getZ();
            double angleToTarget = Math.toDegrees(Math.atan2(-dx, dz));
            double diff = angleToTarget - playerYaw;
            // Normalize angle to [-180, 180]
            while (diff > 180) diff -= 360;
            while (diff < -180) diff += 360;

            if (Math.abs(diff) < 30) {
                return "§eตรงไป";
            } else if (diff > 30 && diff < 150) {
                return "§eเลี้ยวขวา";
            } else if (diff < -30 && diff > -150) {
                return "§eเลี้ยวซ้าย";
            } else {
                return "§eกลับหลังหัน";
            }
        }

        // คำนวณตำแหน่งด้านหน้าผู้เล่น (สำหรับแสดง hologram เห็นใน first person)
        private Location getFrontLocation(Location playerLoc, double distance, double up) {
            float yaw = playerLoc.getYaw();
            double rad = Math.toRadians(yaw);
            double xOffset = -Math.sin(rad) * distance;
            double zOffset = Math.cos(rad) * distance;
            return playerLoc.clone().add(xOffset, up, zOffset);
        }
    }
}
