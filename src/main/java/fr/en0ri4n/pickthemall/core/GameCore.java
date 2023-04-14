package fr.en0ri4n.pickthemall.core;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.datafixers.util.Pair;
import fr.en0ri4n.pickthemall.PickThemAll;
import fr.en0ri4n.pickthemall.config.PluginConfig;
import fr.en0ri4n.pickthemall.runnables.ExitRunnable;
import fr.en0ri4n.pickthemall.runnables.GameRunnable;
import fr.en0ri4n.pickthemall.utils.ScoreboardManager;
import fr.en0ri4n.pickthemall.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static fr.en0ri4n.pickthemall.utils.Colors.*;

@SuppressWarnings("ConstantConditions")
public class GameCore
{
    private static final GameCore INSTANCE = new GameCore();

    // Current Game Datas
    private final Map<UUID, List<String>> playerScores;
    private GameState currentGameState;

    private GameCore()
    {
        setState(GameState.LOBBY); // Ensure that the game is in LOBBY state when instancing core
        this.playerScores = new HashMap<>();
    }

    public void load()
    {
        // Setup Game Rules
        Bukkit.getWorlds().forEach(world ->
        {
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.UNIVERSAL_ANGER, true);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        });

        // Add Players to the game
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);

        // Setup Lobby
        setState(GameState.LOBBY);
    }

    public void unload()
    {
        ScoreboardManager.getInstance().unregisterPlayers();
    }

    public void startGame()
    {
        broadcast(green("Starting Game... ") + gray("(" + PluginConfig.getInstance().getGameDuration() + " minutes)"));

        // Give Recipes
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "recipe give @a *");

        Bukkit.getOnlinePlayers().forEach(p ->
        {
            ScoreboardManager.getInstance().registerPlayer(p);
            broadcastObjectiveTo(p);
            p.setGameMode(org.bukkit.GameMode.SURVIVAL);
            p.setLevel(69);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(darkGreen("A game by ") + white("En0ri4n")));
        });

        giveStuff();

        GameRunnable.start();
    }

    public void endGame()
    {
        broadcast(green("Ending Game..."));

        // Set Game State
        setState(GameState.ENDING);

        // Check the winner
        playerScores.entrySet().stream().sorted(Map.Entry.comparingByValue(Utils.LIST_COMPARATOR.reversed())).map(Map.Entry::getKey).findFirst().ifPresent(uuid ->
        {
            Player winner = Bukkit.getPlayer(uuid);

            if(winner != null)
            {
                broadcast(green("The winner is ") + white(winner.getName()) + green("!"));
                broadcast(green("Sending you to the lobby in 10 seconds..."));
                exitPlayers(10);
                Bukkit.getScheduler().runTaskLater(PickThemAll.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart"), 20 * 15);
            }
        });

        // Stop Game
        Bukkit.getOnlinePlayers().forEach(player ->
        {
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setExp(0);
            player.setLevel(0);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.getInventory().setExtraContents(null);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setInvulnerable(true);
            player.setCollidable(false);
            player.setGlowing(true);
            player.setSilent(true);
        });
    }

    private void giveStuff()
    {
        Bukkit.getOnlinePlayers().forEach(player ->
        {
            giveEffects(player, false);

            player.getInventory().clear();
            player.getInventory().addItem(Utils.unbreakable(Material.DIAMOND_SWORD));
            player.getInventory().addItem(Utils.tool(Material.DIAMOND_PICKAXE, 2, 2));
            player.getInventory().addItem(Utils.tool(Material.STONE_AXE, 3, 2));
            player.getInventory().addItem(Utils.unbreakable(Material.GOLDEN_CARROT, 32));
            player.getInventory().setItem(8, Utils.getTutorialBook());

            player.getInventory().setChestplate(Utils.enchant(Material.NETHERITE_CHESTPLATE, Enchantment.BINDING_CURSE, 10));
            player.getInventory().setLeggings(Utils.enchant(Material.LEATHER_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 3));
        });
    }

    public void giveEffects(Player player, boolean isDeath)
    {
        player.addPotionEffect(Utils.effect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(Utils.effect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        player.addPotionEffect(Utils.effect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

        if(isDeath)
        {
            Pair<PotionEffect, String> deathPenalty = Utils.getRandomDeathPenalty();
            send(player, red("Death penalties : ") + deathPenalty.getSecond());
            player.addPotionEffect(deathPenalty.getFirst());
        }
    }

    public void broadcastObjectiveTo(Player player)
    {
        String message = gold("You just have to pick more items than your opponents !");

        send(player, message);
    }

    public boolean hasEnoughPlayer()
    {
        return Bukkit.getOnlinePlayers().size() >= PluginConfig.getInstance().getMinimumPlayers();
    }

    // Handle Player Join and Quit
    public void addPlayer(Player player)
    {
        player.getInventory().clear();
        player.getInventory().addItem(Utils.getTutorialBook());

        playerScores.put(player.getUniqueId(), new ArrayList<>());

        broadcast(yellow(player.getName() + " has joined the party ! ") + gray("(" + Bukkit.getOnlinePlayers().size() + "/" + PluginConfig.getInstance().getMinimumPlayers() + ")"));
    }

    public void removePlayer(Player player)
    {
        playerScores.remove(player.getUniqueId());

        broadcast(gold(player.getName() + " has left the party ! ") + gray("(" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + PluginConfig.getInstance().getMinimumPlayers() + ")"));
    }

    public void updateScoreboard()
    {
        ScoreboardManager.getInstance().updateScoreboard(playerScores);
    }

    /**
     * @param delay in second
     */
    public static void exitPlayers(long delay)
    {
        if(PluginConfig.getInstance().isBungeeServer())
        {
            Bukkit.getScheduler().runTaskLater(PickThemAll.getInstance(), ExitRunnable::start, delay * 20);
        }
        else
        {
            broadcast(yellow("Server will restart in ") + gold("10") + yellow(" seconds"));
            Bukkit.getScheduler().runTaskLater(PickThemAll.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer("Server Restarting")), delay * 20);
        }
    }

    public Map<UUID, List<String>> getScores()
    {
        return this.playerScores;
    }

    // State
    public void setState(GameState currentGameState)
    {
        this.currentGameState = currentGameState;
    }

    public GameState getState()
    {
        return this.currentGameState;
    }

    public boolean isState(GameState state)
    {
        return state == getState();
    }


    public List<String> getPickedItems(Player player)
    {
        if(player == null) return new ArrayList<>();

        return playerScores.get(player.getUniqueId());
    }

    public void addItem(Player player, ItemStack itemStack)
    {
        if(player == null) return;

        if(playerScores.get(player.getUniqueId()).contains(itemStack.getType().name()))
            return;

        List<String> items = playerScores.get(player.getUniqueId());
        items.add(itemStack.getType().name());
        playerScores.put(player.getUniqueId(), items);

        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);

        updateScoreboard();
    }

    // Utils
    public static void send(Player player, String message)
    {
        player.sendMessage(getPrefix() + message);
    }

    private static String getPrefix()
    {
        return darkGreen(bold("Pick")) + green(bold("Them")) + blue(bold("All")) + gray(" >> ");
    }

    public static void broadcast(String message)
    {
        Bukkit.broadcastMessage(getPrefix() + message);
    }

    public static void broadcast(ComponentBuilder builder)
    {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        TextComponent component = new TextComponent(getPrefix());
        component.setBold(true);
        componentBuilder.append(component);
        componentBuilder.append(builder.create());

        Bukkit.spigot().broadcast(componentBuilder.create());
    }

    public static void send(Player player, ComponentBuilder builder)
    {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        TextComponent component = new TextComponent(getPrefix());
        component.setBold(true);
        componentBuilder.append(component);
        componentBuilder.append(builder.create());

        player.spigot().sendMessage(componentBuilder.create());
    }

    // Static Utils
    public static boolean isGame()
    {
        return INSTANCE.isState(GameState.IN_GAME);
    }

    public static boolean isLobby()
    {
        return INSTANCE.isState(GameState.LOBBY);
    }

    public static boolean isPlayer(Player player)
    {
        return getInstance().playerScores.containsKey(player.getUniqueId());
    }

    public static void cancelLobbyEvent(Cancellable cancellable)
    {
        if(isLobby()) cancellable.setCancelled(true);
    }

    public static void sendToLobby(Player player)
    {
        if(!PluginConfig.getInstance().isBungeeServer()) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(PluginConfig.getInstance().getFallbackServerName());

        player.sendPluginMessage(PickThemAll.getInstance(), "BungeeCord", out.toByteArray());
    }

    // Instance
    public static GameCore getInstance()
    {
        return INSTANCE;
    }

    public enum GameState
    {
        LOBBY,
        STARTING,
        IN_GAME,
        ENDING
    }
}
