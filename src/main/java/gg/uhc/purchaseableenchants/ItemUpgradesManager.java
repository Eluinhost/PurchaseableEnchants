package gg.uhc.purchaseableenchants;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.uhc.purchaseableenchants.enchants.EnchantmentType;
import gg.uhc.purchaseableenchants.offers.Offer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemUpgradesManager implements Listener {

    // how many columns in an inventory
    protected static final int COLUMNS = 9;

    // how many offers fit in an inventory
    // max inventory size is 9*6 and an offer is 3 tall so we can fit 18
    protected static final int MAX_OFFERS_PER_INVENTORY = 18;

    // item names
    protected static final String PURCHASE = ChatColor.GREEN + "Click to purchase upgrade";
    protected static final String CANNOT_AFFORD = ChatColor.RED + "You cannot afford this upgrade";

    // inventory name used for identification
    protected static final String INV_NAME = ChatColor.RED + "Enchantment Tool";

    // map of player -> active offers (indexed based on inventory click index)
    protected final Map<UUID, Offer[]> playerPurchaseIndexes = Maps.newHashMap();

    // list of enchantment types used to generate offers
    protected final List<EnchantmentType> enchantmentTypes;

    protected final ItemStack emptySlot;
    protected final ItemStack noItems;

    public ItemUpgradesManager(List<EnchantmentType> enchantmentTypes) {
        this.enchantmentTypes = enchantmentTypes;
        emptySlot = null;

        noItems = new ItemStack(Material.BARRIER, 1);
        setItemMeta(noItems, ChatColor.RED + "No Items", "You have no items in your", "inventory that can be enchanted");
    }

    /**
     * Set the name and lore on the given stack
     *
     * @param stack the stack to set
     * @param name the name of the item
     * @param lore optional lore lines
     */
    protected void setItemMeta(ItemStack stack, String name, String... lore) {
        ItemMeta current = stack.getItemMeta();
        current.setDisplayName(name);

        if (lore.length > 0) {
            current.setLore(Arrays.asList(lore));
        }

        stack.setItemMeta(current);
    }

    /**
     * Converts the itemstack array to a List of offers for the items
     *
     * @param inventory the inventory to get offers for
     * @return list of offers
     */
    public List<Offer> getOffersForInventory(Inventory inventory) {
        ItemStack[] items = inventory.getContents();

        List<Offer> offers = Lists.newArrayList();

        for (int i = 0; i < items.length; i++) {
            ItemStack current = items[i];

            for (EnchantmentType type : enchantmentTypes) {
                if (!type.canEnchant(current)) continue;

                Optional<ItemStack> cost = type.costToUpgrade(current);

                if (!cost.isPresent()) continue;

                ItemStack offer = current.clone();
                type.unsafeEnchantIncrement(offer);

                offers.add(new Offer(offer, current, cost.get(), i));
            }
        }

        return offers;
    }

    /**
     * Shows a list of upgrades to the player to choose from
     *
     * @param player the player to run for
     */
    public void showUpgrades(Player player) {
        // get all of the offers for their entire inventory
        List<Offer> offers = getOffersForInventory(player.getInventory());

        // send empty inventory if there are no offers
        if (offers.size() == 0) {
            // create an inventory 1 row tall
            Inventory custom = Bukkit.createInventory(null, COLUMNS, INV_NAME);

            // make the row contain the no items item in the middle
            ItemStack[] empty = new ItemStack[COLUMNS];
            empty[empty.length / 2] = noItems;
            custom.setContents(empty);

            // open the inventory
            player.openInventory(custom);

            // remove any triggers (shouldnt exist but whatever)
            playerPurchaseIndexes.remove(player.getUniqueId());
            return;
        }

        // create a limited version of the original to fit into an inventory
        if (offers.size() > MAX_OFFERS_PER_INVENTORY) {
            // shuffle them so different ones show up each time
            Collections.shuffle(offers);

            offers = offers.subList(0, MAX_OFFERS_PER_INVENTORY);
        }

        // 3 slots per offer
        int requiredSlots = numberOfSlotsRequired(offers.size()) * 3;

        // array used to populate the inventory
        ItemStack[] customContents = new ItemStack[requiredSlots];
        Arrays.fill(customContents, emptySlot);

        // array that is 1-1 with the inventory indexes
        // if the index has an offer, when it gets clicked
        // it will purchase that offer
        Offer[] offerPurchaseTriggers = new Offer[requiredSlots];

        // split into offer rows
        List<List<Offer>> offerRows = Lists.partition(offers, COLUMNS);

        for (int i = 0; i < offerRows.size(); i++) {
            // 3 rows per offer
            int originalRowOffset = i * COLUMNS * 3;
            int offerRowOffset = originalRowOffset + COLUMNS;
            int costRowOffset = offerRowOffset + COLUMNS;

            List<Offer> offerRow = offerRows.get(i);

            for (int j = 0; j < offerRow.size(); j++) {
                Offer offer = offerRow.get(j);

                // top item
                customContents[j + originalRowOffset] = offer.getOriginal();
                // middle item
                customContents[j + offerRowOffset] = offer.getOffer();

                // handle cost item
                ItemStack costItem = offer.getCost().clone();

                // set item name based on if they can afford it or not
                boolean canAfford = player.getInventory().containsAtLeast(costItem, costItem.getAmount());
                setItemMeta(costItem, canAfford ? PURCHASE : CANNOT_AFFORD);

                // only add trigger if they can afford it
                if (canAfford) offerPurchaseTriggers[j + costRowOffset] = offer;

                // set item
                customContents[j + costRowOffset] = costItem;
            }
        }

        // create the inventory
        Inventory custom = Bukkit.createInventory(null, requiredSlots, INV_NAME);
        custom.setContents(customContents);

        // replace previous offers trigger map
        playerPurchaseIndexes.put(player.getUniqueId(), offerPurchaseTriggers);
        player.openInventory(custom);
    }

    /**
     * How many slots are required to hold the number of items, multiples of COLUMNS and at least 1*COLUMNS
     *
     * @param number the number to round
     */
    protected int numberOfSlotsRequired(int number) {
        if (number == 0) return COLUMNS;

        return COLUMNS * ((number + COLUMNS - 1) / COLUMNS);
    }

    /**
     * Closes the player's inventory and removes any triggers
     *
     * @param player the player to run for
     */
    public void closeOpen(Player player) {
        playerPurchaseIndexes.remove(player.getUniqueId());
        player.closeInventory();
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        // remove triggers
        playerPurchaseIndexes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        // remove triggers
        playerPurchaseIndexes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        // only run for our inventory
        if (!event.getInventory().getTitle().equals(INV_NAME)) return;

        // cancel all clicks
        event.setCancelled(true);

        // only run for players
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) return;

        Player player = (Player) entity;

        // grab player's triggers
        Offer[] purchaseTriggers = playerPurchaseIndexes.get(player.getUniqueId());

        // none saved, shouldn't happen
        if (purchaseTriggers == null) {
            closeOpen(player);
            return;
        }

        int indexClicked = event.getRawSlot();

        // clicked outside inventory
        if (indexClicked >= purchaseTriggers.length) return;

        // grab trigger
        Offer chosen = purchaseTriggers[indexClicked];

        // click on a non-triggered slot
        if (chosen == null) return;

        ItemStack cost = chosen.getCost();

        // check they have enough
        // shouldn't happen but cancel anyway
        if (!player.getInventory().containsAtLeast(cost, cost.getAmount())) {
            closeOpen(player);
            return;
        }

        // remove cost
        player.getInventory().removeItem(cost);
        // upgrade item
        player.getInventory().setItem(chosen.getOriginalItemIndex(), chosen.getOffer());

        // we're done here
        closeOpen(player);
    }
}
