package fr.en0ri4n.pickthemall.runnables;

import fr.en0ri4n.pickthemall.config.PluginConfig;
import fr.en0ri4n.pickthemall.core.GameCore;
import fr.en0ri4n.pickthemall.utils.TaskHelper;
import org.bukkit.Bukkit;

public class GameRunnable extends BaseRunnable
{
    private GameRunnable(int gameDuration)
    {
        setCounter(gameDuration);
    }

    @Override
    public void run()
    {
        if(getCounter() <= 0)
        {
            TaskHelper.cancelTask(getTaskId());
            GameCore.getInstance().endGame();
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> player.setLevel(getCounter()));

        decreaseCounter();
    }

    public static void start()
    {
        // Set Game State
        GameCore.getInstance().setState(GameCore.GameState.IN_GAME);

        GameRunnable gameRunnable = new GameRunnable(60 * PluginConfig.getInstance().getGameDuration());
        gameRunnable.setTaskId(TaskHelper.startRepeatingTask(gameRunnable, 20L));
    }
}
