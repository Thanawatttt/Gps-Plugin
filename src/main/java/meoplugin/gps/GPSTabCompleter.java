package meoplugin.gps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GPSTabCompleter implements TabCompleter {
    private final GPSPlugin plugin;
    public GPSTabCompleter(GPSPlugin plugin) { this.plugin = plugin; }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("gps.admin")) ret.addAll(List.of("create","add","remove","removepoint","start"));
            ret.addAll(List.of("list","stop"));
            return ret.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        if (args.length == 2) {
            switch(args[0].toLowerCase()) {
                case "add":
                case "remove":
                case "removepoint":
                case "start":
                    if (sender.hasPermission("gps.admin"))
                        ret.addAll(plugin.getGPSManager().getRouteNames());
                    break;
            }
            return ret.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("start")) {
            if (sender.hasPermission("gps.admin")) {
                for (Player p : plugin.getServer().getOnlinePlayers())
                    ret.add(p.getName());
            }
            return ret.stream().filter(s -> s.startsWith(args[2])).collect(Collectors.toList());
        }
        return ret;
    }
}