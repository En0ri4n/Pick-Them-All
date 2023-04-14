package fr.en0ri4n.pickthemall.config;

import fr.en0ri4n.pickthemall.PickThemAll;
import fr.en0ri4n.pickthemall.utils.Randomizer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class PluginConfig
{
    private static final PluginConfig INSTANCE = new PluginConfig();

    // Config Name and File
    private static final String CONFIG_NAME = "config.yml";

    // Config
    private boolean isBungeeServer;
    private String serverName;
    private int minimumPlayers;
    private boolean autoStart;

    private List<Integer> gameDurationRange;
    private Integer gameDuration;

    private PluginConfig() {}

    public void load()
    {
        PickThemAll.getInstance().saveResource(CONFIG_NAME, false);

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(PickThemAll.getInstance().getDataFolder(), CONFIG_NAME));

        isBungeeServer = config.getBoolean("bungee_server");
        serverName = config.getString("fallback_server");
        minimumPlayers = config.getInt("minimum_players");
        autoStart = config.getBoolean("auto_start");

        gameDurationRange = config.getIntegerList("game_duration");

        gameDuration = Randomizer.randomRange(gameDurationRange);

        PickThemAll.getInstance().getLogger().info("Config loaded!" + gameDuration + " " + gameDurationRange);
    }

    public int getMinimumPlayers()
    {
        return minimumPlayers;
    }

    public static PluginConfig getInstance()
    {
        return INSTANCE;
    }

    public boolean isBungeeServer()
    {
        return isBungeeServer;
    }

    public String getFallbackServerName()
    {
        return serverName;
    }

    public boolean isAutoStart()
    {
        return autoStart;
    }

    public List<Integer> getGameDurationRange()
    {
        return gameDurationRange;
    }

    public int getGameDuration()
    {
        return gameDuration;
    }
}
