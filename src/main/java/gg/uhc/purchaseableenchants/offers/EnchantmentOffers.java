package gg.uhc.purchaseableenchants.offers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import gg.uhc.purchaseableenchants.enchants.EnchantmentType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnchantmentOffers {

    protected final List<EnchantmentType> enchantmentTypes;

    public EnchantmentOffers(List<EnchantmentType> enchantmentTypes) {
        this.enchantmentTypes = enchantmentTypes;

//        ItemStack tier1 = new ItemStack(Material.GOLD_INGOT, 2);
//        ItemStack tier2 = new ItemStack(Material.GOLD_INGOT, 8);
//        ItemStack tier3 = new ItemStack(Material.GOLDEN_APPLE, 2);
//        ItemStack tier4 = new ItemStack(Material.GOLDEN_APPLE, 8);
//
//        enchantmentTypes = Lists.newArrayList();
//
//        enchantmentTypes.add(new EnchantmentType(Enchantment.DAMAGE_ALL, tier1, tier2, tier3, tier4));
//        enchantmentTypes.add(new EnchantmentType(Enchantment.ARROW_DAMAGE, tier1, tier2, tier3, tier4));
//        enchantmentTypes.add(new EnchantmentType(Enchantment.PROTECTION_ENVIRONMENTAL, tier1, tier2, tier3, tier4));
//        enchantmentTypes.add(new EnchantmentType(Enchantment.ARROW_FIRE, tier3, tier4));
//        enchantmentTypes.add(new EnchantmentType(Enchantment.FIRE_ASPECT, tier3, tier4));
//        enchantmentTypes.add(new EnchantmentType(Enchantment.ARROW_INFINITE, tier4));
    }

    /**
     * @param items the items to check
     * @return List of offers for the given item stacks with their index set to the position in the array
     */
    public List<Offer> availableOffersForStacks(ItemStack[] items) {
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
}
