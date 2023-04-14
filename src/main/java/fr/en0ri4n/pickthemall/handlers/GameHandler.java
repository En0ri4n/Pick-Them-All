package fr.en0ri4n.pickthemall.handlers;

import fr.en0ri4n.pickthemall.core.GameCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class GameHandler implements Listener
{
    @EventHandler
    public void onPickUp(EntityPickupItemEvent event)
    {
        if(event.getEntity() instanceof Player player && GameCore.isGame())
        {
            GameCore.getInstance().addItem(player, event.getItem().getItemStack());
        }
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event)
    {
        if(GameCore.isGame())
            GameCore.getInstance().addItem((Player) event.getWhoClicked(), event.getRecipe().getResult());
    }

    @EventHandler
    public void onPlayerSmelt(FurnaceExtractEvent event)
    {
        if(GameCore.isGame())
            GameCore.getInstance().addItem(event.getPlayer(), new ItemStack(event.getItemType()));
    }

    @EventHandler
    public void onEvent(FurnaceStartSmeltEvent event)
    {
        event.setTotalCookTime(60);
    }
}
