package gg.uhc.purchaseableenchants.offers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Cost {

    protected final Material material;
    protected final int amount;

    public Cost(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getStack() {
        return new ItemStack(material, amount);
    }
}
