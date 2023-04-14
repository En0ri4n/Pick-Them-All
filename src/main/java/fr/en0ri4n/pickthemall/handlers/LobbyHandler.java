package fr.en0ri4n.pickthemall.handlers;

import fr.en0ri4n.pickthemall.core.GameCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class LobbyHandler implements Listener
{
    @EventHandler
    public void onPickUp(EntityPickupItemEvent event)
    {
        GameCore.cancelLobbyEvent(event);
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event)
    {
        GameCore.cancelLobbyEvent(event);
    }

    @EventHandler
    public void onBlockDestroyed(BlockBreakEvent event)
    {
        GameCore.cancelLobbyEvent(event);
    }

    @EventHandler
    public void onPlayer(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player)
            GameCore.cancelLobbyEvent(event);
    }

    @EventHandler
    public void onPlayerStat(FoodLevelChangeEvent event)
    {
        GameCore.cancelLobbyEvent(event);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        GameCore.cancelLobbyEvent(event);
    }
}
