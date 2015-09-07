package gg.uhc.purchaseableenchants.enchants;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantmentType {

    protected final Enchantment enchantment;
    protected final ItemStack[] costs;

    public EnchantmentType(Enchantment enchantment, ItemStack... costs) {
        Preconditions.checkNotNull(enchantment);

        this.enchantment = enchantment;
        this.costs = costs;
    }

    public Optional<ItemStack> costToUpgrade(ItemStack stack) {
        int currentLevel = stack.getEnchantmentLevel(enchantment);

        // out of range or null cost, cannot do
        if (!canEnchantAtLevel(currentLevel + 1)) return Optional.absent();

        return Optional.of(costs[currentLevel]);
    }

    public boolean canEnchantAtLevel(int level) {
        int index = level - 1;

        return index >= 0 && index < costs.length && costs[index] != null;
    }

    public void unsafeEnchantIncrement(ItemStack stack) {
        Preconditions.checkNotNull(stack);

        stack.addUnsafeEnchantment(enchantment, stack.getEnchantmentLevel(enchantment) + 1);
    }

    public boolean canEnchant(ItemStack stack) {
        if (stack == null) return false;

        if (!enchantment.canEnchantItem(stack)) return false;

        // check each enchant on the item
        Map<Enchantment, Integer> current = stack.getEnchantments();
        for (Map.Entry<Enchantment, Integer> e : current.entrySet()) {

            // if it's the same enchantment
            if (e.getKey().equals(enchantment)) {
                // check if we can up a level
                return canEnchantAtLevel(e.getValue() + 1);
            }

            // check for conflicts with existing enchant
            if (e.getKey().conflictsWith(enchantment)) {
                return false;
            }
        }

        return true;
    }
}
