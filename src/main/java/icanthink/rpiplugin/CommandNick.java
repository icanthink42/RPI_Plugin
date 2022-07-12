package icanthink.rpiplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandNick implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        Player player = (Player) sender;
        HashMap<String, String> player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
        if (player_data == null) {
            RpiPlugin.data.player_map.put(player.getUniqueId(), new HashMap<>());
            player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
        }
        player_data.put("nick", args[0]);
        RpiPlugin.data.saveData("save_data.dat");
        player.sendMessage("Set name to " + args[0]);
        RpiPlugin.update_name_tag(player);
        return true;
    }
}