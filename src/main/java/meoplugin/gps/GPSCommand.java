package meoplugin.gps;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GPSCommand implements CommandExecutor {
    private final GPSPlugin plugin;
    public GPSCommand(GPSPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "create":
                if (!sender.hasPermission("gps.admin")) { sender.sendMessage("§cไม่มีสิทธิ์!"); return true; }
                if (args.length < 2) { sender.sendMessage("§c/gps create <ชื่อ>"); return true; }
                plugin.getGPSManager().createRoute(args[1]);
                sender.sendMessage("§aสร้าง GPS route: " + args[1]);
                break;
            case "add":
                if (!sender.hasPermission("gps.admin")) { sender.sendMessage("§cไม่มีสิทธิ์!"); return true; }
                if (!(sender instanceof Player player)) { sender.sendMessage("§cใช้ในเกมเท่านั้น"); return true; }
                if (args.length < 2) { sender.sendMessage("§c/gps add <ชื่อ>"); return true; }
                plugin.getGPSManager().addPoint(args[1], player.getLocation());
                sender.sendMessage("§aเพิ่มจุดใน " + args[1]);
                break;
            case "remove":
                if (!sender.hasPermission("gps.admin")) { sender.sendMessage("§cไม่มีสิทธิ์!"); return true; }
                if (args.length < 2) { sender.sendMessage("§c/gps remove <ชื่อ>"); return true; }
                plugin.getGPSManager().removeRoute(args[1]);
                sender.sendMessage("§aลบ GPS route: " + args[1]);
                break;
            case "removepoint":
                if (!sender.hasPermission("gps.admin")) { sender.sendMessage("§cไม่มีสิทธิ์!"); return true; }
                if (args.length < 3) { sender.sendMessage("§c/gps removepoint <route> <index>"); return true; }
                try {
                    int index = Integer.parseInt(args[2]);
                    plugin.getGPSManager().removePoint(args[1], index);
                    sender.sendMessage("§aลบจุดที่ " + index + " ใน " + args[1]);
                } catch (NumberFormatException e) { sender.sendMessage("§cIndex ต้องเป็นตัวเลข"); }
                break;
            case "list":
                sender.sendMessage("§aGPS routes:");
                plugin.getGPSManager().getRouteNames().forEach(n -> sender.sendMessage("§6- §f" + n));
                break;
            case "start": {
                if (!sender.hasPermission("gps.admin")) { sender.sendMessage("§cไม่มีสิทธิ์!"); return true; }
                if (args.length < 2) { sender.sendMessage("§c/gps start <route> [player]"); return true; }
                Player target;
                if (args.length > 2) {
                    target = Bukkit.getPlayer(args[2]);
                    if (target == null) { sender.sendMessage("§cไม่พบผู้เล่น: " + args[2]); return true; }
                } else {
                    if (sender instanceof Player player) {
                        target = player;
                    } else {
                        sender.sendMessage("§cสำหรับ console ต้องระบุชื่อผู้เล่น: /gps start <route> <player>");
                        return true;
                    }
                }
                plugin.getGPSManager().startNavigation(args[1], target);
                sender.sendMessage("§aเริ่ม GPS: " + args[1] + " ให้กับ " + target.getName());
                break;
            }
            case "stop":
                if (sender instanceof Player player) {
                    plugin.getGPSManager().stopNavigation(player);
                    sender.sendMessage("§aหยุด GPS แล้ว");
                } else {
                    sender.sendMessage("§cใช้ในเกมเท่านั้น");
                }
                break;
            default: sendHelp(sender); break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6/gps create <ชื่อ> - สร้างเส้นทาง");
        sender.sendMessage("§6/gps add <ชื่อ> - เพิ่มจุด (ในเกมเท่านั้น)");
        sender.sendMessage("§6/gps removepoint <ชื่อ> <index> - ลบจุด");
        sender.sendMessage("§6/gps remove <ชื่อ> - ลบเส้นทาง");
        sender.sendMessage("§6/gps list - ดู GPS routes");
        sender.sendMessage("§6/gps start <ชื่อ> [player] - เริ่มนำทาง (/gps start <ชื่อ> <player> จาก console)");
        sender.sendMessage("§6/gps stop - หยุดนำทาง (ในเกมเท่านั้น)");
    }
}
