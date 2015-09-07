package gg.uhc.purchaseableenchants.listeners;

import gg.uhc.purchaseableenchants.ItemUpgradesManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InteractListener implements Listener {

    protected final ItemUpgradesManager itemUpgradesManager;
    protected final List<Material> triggers;

    public InteractListener(ItemUpgradesManager itemUpgradesManager, List<Material> triggers) {
        this.itemUpgradesManager = itemUpgradesManager;
        this.triggers = triggers;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();

        if (stack != null && triggers.contains(stack.getType())) {
            event.setCancelled(true);
            itemUpgradesManager.showUpgrades(event.getPlayer());
        }
    }

}
