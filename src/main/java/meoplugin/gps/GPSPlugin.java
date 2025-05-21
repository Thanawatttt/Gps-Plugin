package meoplugin.gps;

import org.bukkit.plugin.java.JavaPlugin;

public class GPSPlugin extends JavaPlugin {
    private GPSManager gpsManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        this.gpsManager = new GPSManager(this);
        getCommand("gps").setExecutor(new GPSCommand(this));
        getCommand("gps").setTabCompleter(new GPSTabCompleter(this));
        getServer().getPluginManager().registerEvents(new GPSListener(this), this);
    }
    @Override
    public void onDisable() {
        if (gpsManager != null) gpsManager.saveAllData();
    }
    public GPSManager getGPSManager() { return gpsManager; }
}