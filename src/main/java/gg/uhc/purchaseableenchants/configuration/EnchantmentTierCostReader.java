package gg.uhc.purchaseableenchants.configuration;

import com.google.common.collect.Maps;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class EnchantmentTierCostReader extends ConfigurationReader<ItemStack[]> {

    protected static final String INVALID_TIER_NUMBER = "Invalid tier number '%d' at key `%s`";
    protected static final String INVALID_TIER_COST = "Invalid tier cost '%s' for tier %d at key `%s`";

    protected final Map<String, ItemStack> costs;

    public EnchantmentTierCostReader(Map<String, ItemStack> costs) {
        this.costs = costs;
    }

    @Override
    protected ItemStack[] read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection tiersSection = section.getConfigurationSection(key);

        Set<String> keys = tiersSection.getKeys(false);

        Map<Integer, ItemStack> tiers = Maps.newHashMap();
        int max = 0;
        for (String k : keys) {
            int tier;
            try {
                tier = Integer.parseInt(k);

                // don't allow < 1 as levels
                if (tier < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw up(INVALID_TIER_NUMBER, k, key);
            }

            if (!tiersSection.isString(k)) throw up(WRONG_TYPE, key + "." + tier, "String", tiersSection.get(k));

            String tierCost = tiersSection.getString(k);
            ItemStack cost = costs.get(tierCost);

            if (null == cost) throw up(INVALID_TIER_COST, tierCost, tier, key);

            max = Math.max(max, tier);
            tiers.put(tier, cost);
        }

        // build indexed array from the map
        ItemStack[] f = new ItemStack[max];
        for (Map.Entry<Integer, ItemStack> entry : tiers.entrySet()) {
            // level 1 enchant is index 0
            f[entry.getKey() - 1] = entry.getValue();
        }

        return f;
    }
}
