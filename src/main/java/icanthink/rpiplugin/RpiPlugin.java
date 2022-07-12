package icanthink.rpiplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.haoshoku.nick.api.NickAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public final class RpiPlugin extends JavaPlugin {
    static Data data;
    static Plugin plugin;
    static String wolf_key = "";
    @Override
    public void onEnable() {
        File myObj = new File("wolf_key");
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);
            wolf_key = myReader.nextLine();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        myReader.close();
        plugin = this;
        getServer().getPluginManager().registerEvents(new PluginListener(), this);
        data = Data.loadData("save_data.dat");
        if (data == null) {
            data = new Data();
        }
        this.getCommand("nick").setExecutor(new CommandNick());
        this.getCommand("getnick").setExecutor(new CommandGetNick());
        this.getCommand("setteamdata").setExecutor(new CommandSetTeamData());
        this.getCommand("setteam").setExecutor(new CommandSetTeam());
        this.getCommand("setteamoffline").setExecutor(new CommandSetTeamOffline());
        this.getCommand("chat").setExecutor(new CommandChat());
        this.getCommand("calculator").setExecutor(new CommandCalculator());
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : players) {
            update_name_tag(player);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    static void set_nick(Player player, String prefix, String name, String color) {
        NickAPI.nick(player, org.bukkit.ChatColor.valueOf(color) + name);
        NickAPI.refreshPlayer(player);
    }

    static void update_name_tag(Player player) {
        HashMap<String, String> player_data = data.player_map.get(player.getUniqueId());
        if (player_data == null) {
            return;
        }
        String nick = player.getName();
        if (player_data.get("nick") != null) {
            nick = player_data.get("nick");
        }
        String prefix = "";
        String color = "White";
        System.out.println("Team:" + player_data.get("team"));
        if (player_data.get("team") != null) {
            HashMap<String, String> team = data.teams.get(player_data.get("team"));
            if (team != null) {
                prefix = team.get("name");
                color = team.get("color");
            }
        }
        RpiPlugin.set_nick(player, prefix, nick, color.toUpperCase());
    }

    static void update_name_tag(OfflinePlayer player) {
        HashMap<String, String> player_data = data.player_map.get(player.getUniqueId());
        if (player_data == null) {
            return;
        }
        String nick = player.getName();
        if (player_data.get("nick") != null) {
            nick = player_data.get("nick");
        }
        String prefix = "";
        String color = "White";
        System.out.println("Team:" + player_data.get("team"));
        if (player_data.get("team") != null) {
            HashMap<String, String> team = data.teams.get(player_data.get("team"));
            if (team != null) {
                prefix = team.get("name");
                color = team.get("color");
            }
        }
    }

    static String get(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return null;
        }

        return response.body();
    }

    static void safe_lightening_strike(Player player) {
        boolean villager = false;
        Entity[] entities = player.getWorld().getNearbyEntities(player.getLocation(), 8, 8, 8).toArray(new Entity[0]);
        for (int i = 0; i < entities.length; i++) {
            if (entities[i].getType() == EntityType.VILLAGER) {
                villager = true;
                break;
            }
        }
        if (villager) {
            player.setFireTicks(20 * 5);
            player.sendMessage(ChatColor.DARK_RED + "Villagers detected! Setting fire instead of summoning thor.");
        }
        else {
            player.getWorld().strikeLightning(player.getLocation());
        }
    }

}
