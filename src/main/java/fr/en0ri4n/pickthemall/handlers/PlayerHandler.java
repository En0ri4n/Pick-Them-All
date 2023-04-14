package fr.en0ri4n.pickthemall.handlers;

import fr.en0ri4n.pickthemall.PickThemAll;
import fr.en0ri4n.pickthemall.core.GameCore;
import fr.en0ri4n.pickthemall.utils.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import static fr.en0ri4n.pickthemall.utils.Colors.*;

public class PlayerHandler implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if(GameCore.isGame() && GameCore.isPlayer(player))
        {
            event.setJoinMessage(gold(player.getName()) + yellow(" has rejoined !"));
            return;
        }

        if(GameCore.isGame())
        {
            player.setGameMode(GameMode.SPECTATOR);
            event.setJoinMessage(gray(italic(player.getName() + " wants to spectate the game...")));
            ScoreboardManager.getInstance().registerPlayer(player);
            return;
        }

        GameCore.getInstance().addPlayer(player);
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if(GameCore.isGame())
        {
            GameCore.broadcast(gold(event.getPlayer().getName()) + yellow(" has left !"));
            event.setQuitMessage(null);
            return;
        }

        GameCore.getInstance().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Bukkit.getScheduler().runTaskLater(PickThemAll.getInstance(), () -> GameCore.getInstance().giveEffects(event.getPlayer(), true), 40L);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        event.setFormat(gold("%s") + reset(gray(" ▶ ")) + white("%s"));
    }
}
