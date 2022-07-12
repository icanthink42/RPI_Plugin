package icanthink.rpiplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.cert.TrustAnchor;
import java.util.HashMap;

public class CommandChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 0) {
            return false;
        }
        Player player = (Player) sender;
        HashMap<String, String> player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
        if (player_data == null) {
            RpiPlugin.data.player_map.put(player.getUniqueId(), new HashMap<>());
            player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
        }
        if (player_data.get("team") == null) {
            player.sendMessage("you may not toggle your chat mode if you are not in a team!");
            return true;
        }
        player_data.putIfAbsent("chatmode", "normal");
        if (player_data.get("chatmode").equals("normal")) {
            player_data.put("chatmode", "team");
        } else {
            player_data.put("chatmode", "normal");
        }
        RpiPlugin.data.saveData("save_data.dat");
        player.sendMessage("Updated chat mode to " + player_data.get("chatmode"));
        return true;
    }
}