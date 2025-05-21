package meoplugin.gps.models;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class GPSRoute {
    private String name;
    private List<Location> points;

    public GPSRoute(String name) {
        this.name = name;
        this.points = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<Location> getPoints() { return points; }
    public void addPoint(Location location) { points.add(location); }
    public void removePoint(int index) {
        if (index >= 0 && index < points.size()) points.remove(index);
    }
}