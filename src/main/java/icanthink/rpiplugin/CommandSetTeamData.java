package icanthink.rpiplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandSetTeamData implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 3) {
            return false;
        }
        Player player = (Player) sender;
        try {
            ChatColor.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Incorrect minecraft color!");
            return true;
        }
        RpiPlugin.data.teams.put(args[0], new HashMap<>());
        RpiPlugin.data.teams.get(args[0]).put("name", args[1]);
        RpiPlugin.data.teams.get(args[0]).put("color", args[2]);
        player.sendMessage("Updated Team Data!");
        RpiPlugin.data.saveData("save_data.dat");
        return true;
    }
}