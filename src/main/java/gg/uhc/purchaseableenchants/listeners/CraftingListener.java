package gg.uhc.purchaseableenchants.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftingListener implements Listener {

    protected final List<Material> disabled;

    public CraftingListener(List<Material> disabled) {
        this.disabled = disabled;
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (disabled.contains(event.getRecipe().getResult().getType())) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
