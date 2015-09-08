package gg.uhc.purchaseableenchants.enchants;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantmentType {

    protected final Enchantment enchantment;
    protected final ItemStack[] costs;

    /**
     * Create a new enchantment type
     *
     * @param enchantment the enchantment to apply
     * @param costs the costs per tier, null = not available
     */
    public EnchantmentType(Enchantment enchantment, ItemStack... costs) {
        Preconditions.checkNotNull(enchantment);

        this.enchantment = enchantment;
        this.costs = costs;
    }

    /**
     * Checks ths cost to upgrade the given stack.
     * If return value is not present, we cannot upgrade the given stack with this enchantment.
     *
     * @param stack the stack to check
     * @return the cost if possible
     */
    public Optional<ItemStack> costToUpgrade(ItemStack stack) {
        int currentLevel = stack.getEnchantmentLevel(enchantment);

        // out of range or null cost, cannot do
        if (!canEnchantAtLevel(currentLevel + 1)) return Optional.absent();

        return Optional.of(costs[currentLevel]);
    }

    /**
     * Check if the level given has an associated cost
     *
     * @param level the level to check
     * @return true if there is a cost, false otherwise
     */
    public boolean canEnchantAtLevel(int level) {
        int index = level - 1;

        return index >= 0 && index < costs.length && costs[index] != null;
    }

    /**
     * Increments this enchant on the given stack.
     * If the item doesn't have this enchant it will be set to level 1
     *
     * @param stack the stack to increment
     */
    public void unsafeEnchantIncrement(ItemStack stack) {
        Preconditions.checkNotNull(stack);

        stack.addUnsafeEnchantment(enchantment, stack.getEnchantmentLevel(enchantment) + 1);
    }

    /**
     * Checks if the item can have this enchant and whether it is upgradable via this object
     *
     * @param stack the stack to check
     * @return true if it is enchantable and upgradable
     */
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
