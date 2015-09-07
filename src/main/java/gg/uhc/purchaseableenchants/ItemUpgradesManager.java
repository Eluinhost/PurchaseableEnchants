package gg.uhc.purchaseableenchants;

import com.google.common.collect.*;
import gg.uhc.purchaseableenchants.offers.EnchantmentOffers;
import gg.uhc.purchaseableenchants.offers.Offer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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

    protected static final int COLUMNS = 9;
    protected static final int MAX_OFFERS_PER_INVENTORY = 18;
    protected static final String PURCHASE = ChatColor.GREEN + "Click to purchase upgrade";
    protected static final String CANNOT_AFFORD = ChatColor.RED + "You cannot afford this upgrade";
    protected static final String INV_NAME = ChatColor.RED + "Enchantment Tool";

    protected final Map<UUID, Offer[]> playerPurchaseIndexes = Maps.newHashMap();
    protected final EnchantmentOffers enchantmentOffers;

    protected final ItemStack emptySlot;
    protected final ItemStack noItems;

    public ItemUpgradesManager(EnchantmentOffers enchantmentOffers) {
        this.enchantmentOffers = enchantmentOffers;
        emptySlot = null;

        noItems = new ItemStack(Material.BARRIER, 1);
        setItemMeta(noItems, ChatColor.RED + "No Items", "You have no items in your", "inventory that can be enchanted");
    }

    protected void setItemMeta(ItemStack stack, String name, String... lore) {
        ItemMeta current = stack.getItemMeta();
        current.setDisplayName(name);

        if (lore.length > 0) {
            current.setLore(Arrays.asList(lore));
        }

        stack.setItemMeta(current);
    }

    public void showUpgrades(Player player) {
        // get all of the offers for their entire inventory
        List<Offer> offers = enchantmentOffers.availableOffersForStacks(player.getInventory().getContents());

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

    protected void closeOpen(Player player) {
        playerPurchaseIndexes.remove(player.getUniqueId());
        player.closeInventory();
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        playerPurchaseIndexes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        playerPurchaseIndexes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(INV_NAME)) return;
        event.setCancelled(true);

        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) return;

        Player player = (Player) entity;

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

        // click on a non-trigger slot
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
