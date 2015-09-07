package gg.uhc.purchaseableenchants.offers;

import org.bukkit.inventory.ItemStack;

public class Offer {

    protected final ItemStack offer;
    protected final ItemStack original;
    protected final ItemStack cost;
    protected final int index;

    public Offer(ItemStack offer, ItemStack original, ItemStack cost, int index) {
        this.offer = offer;
        this.original = original;
        this.cost = cost;
        this.index = index;
    }

    public ItemStack getOriginal() {
        return original;
    }

    public ItemStack getOffer() {
        return offer;
    }

    public ItemStack getCost() {
        return cost;
    }

    public int getOriginalItemIndex() {
        return index;
    }
}
