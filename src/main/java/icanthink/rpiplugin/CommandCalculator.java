package icanthink.rpiplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.HashMap;

public class CommandCalculator implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(RpiPlugin.plugin, () -> {
            String question = "";
            for (String arg : args) {
                question += arg + " ";
            }
            question = question.substring(0, question.length() - 1);
            Player player = (Player) sender;
            HashMap<String, String> player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
            if (player_data == null) {
                RpiPlugin.data.player_map.put(player.getUniqueId(), new HashMap<>());
                player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
            }
            String answer = MathQuestionGenerator.solve_equation(question, true);
            if (player_data.get("answer") != null && answer.equals(player_data.get("answer"))) {
                Bukkit.broadcastMessage(NickAPI.getName(player) + ChatColor.RED + " just tried to cheat a math question using the in game calculator!");
                Bukkit.getScheduler().runTask(RpiPlugin.plugin, ()-> Bukkit.getScheduler().runTask(RpiPlugin.plugin, () -> RpiPlugin.safe_lightening_strike(player)));
            }
            else {
                player.sendMessage(ChatColor.GREEN + MathQuestionGenerator.solve_equation(question, false));
            }
        });

        return true;
    }
}