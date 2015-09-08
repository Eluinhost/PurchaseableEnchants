package gg.uhc.purchaseableenchants.offers;

import org.bukkit.inventory.ItemStack;

public class Offer {

    protected final ItemStack offer;
    protected final ItemStack original;
    protected final ItemStack cost;
    protected final int index;

    /**
     * An offer for an upgrade
     *
     * @param offer the offered item
     * @param original the original stack
     * @param cost the cost to get the offered item
     * @param index the inventory index of the original item
     */
    public Offer(ItemStack offer, ItemStack original, ItemStack cost, int index) {
        this.offer = offer;
        this.original = original;
        this.cost = cost;
        this.index = index;
    }

    /**
     * @return the original item stack
     */
    public ItemStack getOriginal() {
        return original;
    }

    /**
     * @return the item to offer the player
     */
    public ItemStack getOffer() {
        return offer;
    }

    /**
     * @return how much the upgrade costs
     */
    public ItemStack getCost() {
        return cost;
    }

    /**
     * @return the inventory index of the original item
     */
    public int getOriginalItemIndex() {
        return index;
    }
}
