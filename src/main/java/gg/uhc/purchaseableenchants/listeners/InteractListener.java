package gg.uhc.purchaseableenchants.listeners;

import com.google.common.collect.Sets;
import gg.uhc.purchaseableenchants.ItemUpgradesManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class InteractListener implements Listener {

    protected final ItemUpgradesManager itemUpgradesManager;
    protected final Set<Material> triggers;

    /**
     * Cancels interact events of the given materials and instead opens the enchantment tool inventory
     *
     * @param triggers the materials to cancel interacts for
     */
    public InteractListener(ItemUpgradesManager itemUpgradesManager, List<Material> triggers) {
        this.itemUpgradesManager = itemUpgradesManager;
        this.triggers = Sets.newHashSet(triggers);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();

        // if the item interacted is to be cancelled
        // cancel the interact and show the inventory
        if (stack != null && triggers.contains(stack.getType())) {
            event.setCancelled(true);
            itemUpgradesManager.showUpgrades(event.getPlayer());
        }
    }

}
