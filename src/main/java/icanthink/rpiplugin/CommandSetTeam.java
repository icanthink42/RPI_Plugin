package icanthink.rpiplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandSetTeam implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 2) {
            return false;
        }
        Player player = (Player) sender;
        if (!RpiPlugin.data.teams.containsKey(args[1])) {
            player.sendMessage("Team doesn't exist!");
            return true;
        }
        Player player2 = Bukkit.getPlayer(args[0]);
        if (player2 == null) {
            player.sendMessage("Player doesn't exist!");
            return true;
        }
        HashMap<String, String> player_data = RpiPlugin.data.player_map.get(player2.getUniqueId());
        if (player_data == null) {
            RpiPlugin.data.player_map.put(player2.getUniqueId(), new HashMap<>());
            player_data = RpiPlugin.data.player_map.get(player2.getUniqueId());
        }
        player_data.put("team", args[1]);
        RpiPlugin.data.saveData("save_data.dat");
        RpiPlugin.update_name_tag(player2);
        player.sendMessage("Updated Players Team!");
        return true;
    }
}