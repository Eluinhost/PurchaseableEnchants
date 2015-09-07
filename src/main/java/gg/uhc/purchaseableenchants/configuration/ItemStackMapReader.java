package gg.uhc.purchaseableenchants.configuration;

import com.google.common.collect.Maps;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class ItemStackMapReader extends ConfigurationReader<Map<String,ItemStack>> {

    protected final ItemStackReader itemStackReader;

    public ItemStackMapReader(ItemStackReader itemStackReader) {
        this.itemStackReader = itemStackReader;
    }

    @Override
    protected Map<String, ItemStack> read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection mapSection = section.getConfigurationSection(key);

        Set<String> keys = mapSection.getKeys(false);
        Map<String, ItemStack> stacks = Maps.newHashMap();

        for (String k : keys) {
            stacks.put(k, itemStackReader.readFromSection(mapSection, k));
        }

        return stacks;
    }
}
