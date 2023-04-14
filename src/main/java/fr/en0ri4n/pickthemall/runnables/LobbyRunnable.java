package fr.en0ri4n.pickthemall.runnables;

import fr.en0ri4n.pickthemall.config.PluginConfig;
import fr.en0ri4n.pickthemall.core.GameCore;
import fr.en0ri4n.pickthemall.utils.TaskHelper;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class LobbyRunnable extends BaseRunnable
{
    private static final LobbyRunnable INSTANCE = new LobbyRunnable();

    private final List<Integer> timeMessages = Arrays.asList(20, 15, 10, 5, 4, 3, 2, 1);

    @Override
    public void run()
    {
        if(GameCore.getInstance().hasEnoughPlayer() && !canCount())
            setCounter(20);

        if(canCount() && PluginConfig.getInstance().isAutoStart())
        {
            if(!GameCore.getInstance().hasEnoughPlayer())
                resetCounter();

            if(isCounter(0))
            {
                TaskHelper.cancelTask(getTaskId());
                GameCore.getInstance().startGame();
                return;
            }

            if(timeMessages.contains(getCounter()))
                GameCore.broadcast(ChatColor.YELLOW + "Party will begin in " + ChatColor.GOLD + getCounter() + ChatColor.YELLOW + " second" + (!isCounter(1) ? "s" : ""));

            decreaseCounter();
        }
    }

    public static int getId()
    {
        return INSTANCE.getTaskId();
    }

    public static void start()
    {
        INSTANCE.setTaskId(TaskHelper.startRepeatingTask(INSTANCE, 20L));
    }
}
