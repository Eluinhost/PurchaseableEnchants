package gg.uhc.purchaseableenchants.listeners;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraftingListener implements Listener {

    protected final Set<Material> disabled;
    protected final boolean disableRepairEnchants;

    /**
     * Disables crafting of all provided materials
     *
     * @param disabled the materials to disable
     * @param disableRepairEnchants remove enchants on repairs
     */
    public CraftingListener(List<Material> disabled, boolean disableRepairEnchants) {
        this.disableRepairEnchants = disableRepairEnchants;
        this.disabled = Sets.newHashSet(disabled);
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        // if it's in the disabled set then set the craft result to AIR
        if (disabled.contains(event.getRecipe().getResult().getType())) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }

        if (disableRepairEnchants && event.isRepair()) {
            ItemStack result = event.getRecipe().getResult();
            Map<Enchantment, Integer> enchs = result.getEnchantments();

            for (Enchantment ench : enchs.keySet()) {
                result.removeEnchantment(ench);
            }
        }
    }
}
