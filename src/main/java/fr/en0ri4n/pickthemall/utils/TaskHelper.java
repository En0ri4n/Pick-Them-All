package fr.en0ri4n.pickthemall.utils;

import fr.en0ri4n.pickthemall.PickThemAll;
import fr.en0ri4n.pickthemall.runnables.BaseRunnable;
import org.bukkit.Bukkit;

public class TaskHelper
{
    public static int startRepeatingTask(Runnable task, long period)
    {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(PickThemAll.getInstance(), task, 0L, period);
    }

    public static void cancelTask(int taskId)
    {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public static BaseRunnable getRunnable(int taskId)
    {
        return (BaseRunnable) Bukkit.getScheduler().getPendingTasks().stream().filter(task -> task.getTaskId() == taskId).findFirst().orElse(null);
    }
}
