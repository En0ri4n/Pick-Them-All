package fr.en0ri4n.pickthemall.runnables;

import fr.en0ri4n.pickthemall.core.GameCore;
import fr.en0ri4n.pickthemall.utils.TaskHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExitRunnable extends BaseRunnable
{
    private final List<UUID> players;

    private ExitRunnable()
    {
        this.players = Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
        setCounter(players.size() - 1);
    }

    @Override
    public void run()
    {
        if(isCounter(-1))
        {
            TaskHelper.cancelTask(getTaskId());
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
            return;
        }

        Player player = Bukkit.getPlayer(players.get(getCounter()));

        if(player != null) GameCore.sendToLobby(player);

        decreaseCounter();
    }

    public static void start()
    {
        ExitRunnable runnable = new ExitRunnable();
        runnable.setTaskId(TaskHelper.startRepeatingTask(runnable, 3L));
    }
}
