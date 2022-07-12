package icanthink.rpiplugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import xyz.haoshoku.nick.api.NickAPI;

import java.awt.image.ColorConvertOp;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PluginListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RpiPlugin.update_name_tag(event.getPlayer());
        HashMap<String, String> player_data = RpiPlugin.data.player_map.get(event.getPlayer().getUniqueId());
        if (player_data != null && player_data.get("nick") != null) {
            event.setJoinMessage(ChatColor.YELLOW + player_data.get("nick") + " joined the game");
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        String name = event.getPlayer().getName();
        String prefix = "";
        ChatColor chatColor = ChatColor.WHITE;
        String chatMode = "normal";
        String team_id = "none";
        HashMap<String, String> player_data = RpiPlugin.data.player_map.get(event.getPlayer().getUniqueId());
        if (player_data != null) {
            if (player_data.get("answer") != null && !player_data.get("answer").equals("noquestion")) {
                boolean answer_attempt = true;
                int cole_answer = 0;
                String fixed_message = event.getMessage().replace("−","-");
                try {
                    cole_answer = Math.round(Float.parseFloat(fixed_message));
                } catch (NumberFormatException e) {
                    answer_attempt = false;
                }
                if (answer_attempt) {
                    if (event.getMessage().contains("−")) {
                        Bukkit.getScheduler().runTask(RpiPlugin.plugin, () -> event.getPlayer().getWorld().strikeLightning(event.getPlayer().getLocation()));
                        Bukkit.broadcastMessage(NickAPI.getName(event.getPlayer()) + ChatColor.RED + " just tried to cheat a math question using Desmos!");
                        player_data.put("answer", "noquestion");
                        return;
                    }
                    long answer;
                    String str_answer = player_data.get("answer");
                    if (str_answer.contains("x")) {
                        String p1 = str_answer.split("×")[1];
                        String p2 = str_answer.split("\\^")[1];
                        try {
                            answer = Math.round(Float.parseFloat(p1) * Math.pow(10, Float.parseFloat(p2)));
                        } catch (NumberFormatException e) {
                            player_data.put("answer", "noquestion");
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "Ah shit! Looks like Jack sucks at writing code that works. Something went wrong while parsing the answer.");
                            return;
                        }
                    } else {
                        try {
                            answer = Math.round(Float.parseFloat(str_answer));
                        } catch (NumberFormatException e) {
                            player_data.put("answer", "noquestion");
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "Ah shit! Looks like Jack sucks at writing code that works. Something went wrong while parsing the answer.");
                            return;
                        }
                    }


                    if (answer == cole_answer) {
                        player_data.putIfAbsent("correct_math", "0");
                        int count = Integer.parseInt(player_data.get("correct_math"));
                        player_data.put("correct_math", String.valueOf(count + 1));
                        Bukkit.broadcastMessage(NickAPI.getName(event.getPlayer()) + ChatColor.GREEN + " answered a math problem correctly! They have answered correctly " + player_data.get("correct_math") + " time(s)");

                    } else {
                        player_data.putIfAbsent("incorrect_math", "0");
                        int count = Integer.parseInt(player_data.get("incorrect_math"));
                        player_data.put("incorrect_math", String.valueOf(count + 1));
                        Bukkit.getScheduler().runTask(RpiPlugin.plugin, () -> event.getPlayer().getWorld().strikeLightning(event.getPlayer().getLocation()));
                        Bukkit.broadcastMessage(NickAPI.getName(event.getPlayer()) + ChatColor.RED + " answered a math problem incorrectly! They have answered incorrectly " + player_data.get("incorrect_math") + " time(s)");
                    }
                    player_data.put("answer", "noquestion");
                    RpiPlugin.data.saveData("save_data.dat");
                    return;
                }
            }
            if (player_data.get("nick") != null) {
                name = player_data.get("nick");
            }
            if (player_data.get("team") != null) {
                team_id = player_data.get("team");
                HashMap<String, String> team = RpiPlugin.data.teams.get(player_data.get("team"));
                if (team != null) {
                    prefix = team.get("name");
                    chatColor = ChatColor.valueOf(team.get("color").toUpperCase());
                }
            }
            if (player_data.get("chatmode") != null) {
                chatMode = player_data.get("chatmode");
            }
        }
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (chatMode.equals("normal")) {
            if (prefix.equals("")) {
                Bukkit.broadcastMessage(ChatColor.BOLD + "Unaligned " + ChatColor.RESET + name + ": " + event.getMessage());
            }
            else {
                Bukkit.broadcastMessage(chatColor + "" + ChatColor.BOLD + prefix + ChatColor.RESET + chatColor + " " + name + ": " + event.getMessage());
            }
        }
        else if (chatMode.equals("team")) {
            for (Player player : players) {
                HashMap<String, String> i_player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
                if (i_player_data != null && i_player_data.get("team") != null && i_player_data.get("team").equals(team_id)) {
                    player.sendMessage(chatColor + "" + ChatColor.BOLD + "[Team Chat] " + prefix + ChatColor.RESET + chatColor + " " + name + ": " + event.getMessage());
                }
            }
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() != EntityType.CREEPER) {
            return;
        }
        event.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_HUGE,event.getEntity().getLocation(),1);
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        event.setCancelled(true);
    }

    static ItemStack get_head(Player player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setDisplayName(player.getDisplayName() + "'s Head");
        ArrayList<String> lore = new ArrayList<>();
        skullMeta.setLore(lore);
        skullMeta.setOwnerProfile(player.getPlayerProfile());
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null && event.getEntity().getKiller().getType() != EntityType.PLAYER) {
            return;
        }
        ItemStack head = get_head(event.getEntity());
        event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), head);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        HashMap<String, String> player_data = RpiPlugin.data.player_map.get(event.getPlayer().getUniqueId());
        if (player_data != null && player_data.get("nick") != null) {
            event.setQuitMessage(ChatColor.YELLOW + player_data.get("nick") + " left the game");
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        Random rand = new Random();
        String question = MathQuestionGenerator.generate_math_problem();

        Player player = event.getPlayer();
        if (!((event.getBlock().getType() == Material.COAL_ORE && rand.nextInt(10) == 0) || event.getBlock().getType() == Material.COAL_BLOCK)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(RpiPlugin.plugin, () -> {
            player.sendMessage(ChatColor.YELLOW + question);
            HashMap<String, String> player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
            if (player_data == null) {
                RpiPlugin.data.player_map.put(player.getUniqueId(), new HashMap<>());
                player_data = RpiPlugin.data.player_map.get(player.getUniqueId());
            }

            player_data.put("answer", MathQuestionGenerator.solve_equation(question, true));
            RpiPlugin.data.saveData("save_data.dat");
        });

    }

    @EventHandler
    public void BlockRightClickEvent(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            float yaw = 0;
            BlockFace blockFace;
            if (block.getType().toString().endsWith("_STAIRS")) {
                var stair = (Stairs) block.getBlockData();
                if (stair.getHalf() == Bisected.Half.TOP) {
                    return;
                }
                blockFace = stair.getFacing();

                if (blockFace == BlockFace.NORTH) {
                    yaw = 0;
                } else if (blockFace == BlockFace.EAST) {
                    yaw = 90;
                } else if (blockFace == BlockFace.SOUTH) {
                    yaw = 180;
                } else if (blockFace == BlockFace.WEST) {
                    yaw = 270;
                }
            } else if (block.getType().toString().endsWith("_SLAB")) {
                var slab = (Slab) block.getBlockData();
                if (slab.getType() != Slab.Type.BOTTOM) {
                    return;
                }
                yaw = event.getPlayer().getLocation().getYaw();
            } else {
                return;
            }
            Location location = block.getLocation().add(new Vector(0.5, -0.4, 0.5));
            var pig = (LivingEntity) block.getWorld().spawnEntity(location, EntityType.PIG);
            pig.setRotation(yaw, 0);
            pig.setAI(false);
            pig.setInvulnerable(true);
            pig.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(1000000, 255));
            pig.addPassenger(event.getPlayer());
        }
    }

    @EventHandler
    public void OnPlayerDismount(EntityDismountEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER && event.getDismounted().getType() == EntityType.PIG) {
            Pig pig = (Pig) event.getDismounted();
            if (!pig.hasAI()) {
                pig.remove();
                event.getEntity().teleport(event.getEntity().getLocation().add(new Vector(0, 1, 0)));
            }
        }
    }
}
