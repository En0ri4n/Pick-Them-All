package fr.en0ri4n.pickthemall;

import fr.en0ri4n.pickthemall.config.PluginConfig;
import fr.en0ri4n.pickthemall.core.GameCore;
import fr.en0ri4n.pickthemall.handlers.GameHandler;
import fr.en0ri4n.pickthemall.handlers.LobbyHandler;
import fr.en0ri4n.pickthemall.handlers.PlayerHandler;
import fr.en0ri4n.pickthemall.runnables.LobbyRunnable;
import fr.en0ri4n.pickthemall.utils.TaskHelper;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static fr.en0ri4n.pickthemall.utils.Colors.*;

@SuppressWarnings("unused")
public class PickThemAll extends JavaPlugin
{
    public static final String ID = "pickthemall";

    private static PickThemAll INSTANCE;

    private final List<String> worlds = Arrays.asList("world", "world_nether", "world_the_end");

    @Override
    public void onLoad()
    {
        if(Bukkit.getOnlinePlayers().size() > 0)
            return;

        getLogger().info("Deleting Worlds...");
        worlds.forEach(world ->
        {
            try
            {
                getLogger().info(String.format("Deleting World '%s'", world));
                Bukkit.unloadWorld(world, false);
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), world));
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        getLogger().info("Done!");
    }

    @Override
    public void onEnable()
    {
        INSTANCE = this;

        PluginConfig.getInstance().load();

        registerListeners();

        registerCommands();

        GameCore.getInstance().load();

        LobbyRunnable.start();
    }

    private void registerCommands()
    {
        registerCommand("start", (sender, command, label, args) ->
        {
            if(!sender.isOp()) return true;

            TaskHelper.cancelTask(LobbyRunnable.getId());
            GameCore.getInstance().startGame();
            return true;
        });

        registerCommand("top", (sender, command, label, args) ->
        {
            if(!(sender instanceof Player player))
                return true;

            Location location = player.getLocation();

            for(int y = 256; y > 1; y--)
            {
                Location newLoc = location.clone();
                newLoc.setY(y);

                if(newLoc.clone().subtract(0D, 1D, 0D).getBlock().getType() != Material.AIR)
                {
                    player.teleport(newLoc);
                    player.sendMessage(green("Ding! Your floor, sir"));
                    break;
                }
            }

            return true;
        });
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        if(PluginConfig.getInstance().isBungeeServer())
            Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        GameCore.getInstance().unload();
    }

    private void registerListeners()
    {
        register(new LobbyHandler());
        register(new GameHandler());
        register(new PlayerHandler());

        if(PluginConfig.getInstance().isBungeeServer())
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void registerCommand(String command, CommandExecutor executor)
    {
        Objects.requireNonNull(getCommand(command)).setExecutor(executor);
    }

    private void register(Listener listener)
    {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public static NamespacedKey getKey(String path)
    {
        return NamespacedKey.fromString(path, INSTANCE);
    }

    public static PickThemAll getInstance()
    {
        return INSTANCE;
    }
}
