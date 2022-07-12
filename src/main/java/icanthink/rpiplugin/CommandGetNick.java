package icanthink.rpiplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGetNick implements CommandExecutor {

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
        if (player_data == null || player_data.get("nick") == null) {
            player.sendMessage("No nick name set!");
            return true;
        }
        player.sendMessage("Current nickname is " + player_data.get("nick"));
        return true;
    }
}