package gg.uhc.purchaseableenchants.configuration;

import com.google.common.collect.Lists;
import gg.uhc.purchaseableenchants.enchants.EnchantmentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.Set;

public class EnchantmentTypeListReader extends ConfigurationReader<List<EnchantmentType>> {

    protected final EnchantmentTypeReader enchantmentTypeReader;

    public EnchantmentTypeListReader(EnchantmentTypeReader enchantmentTypeReader) {
        this.enchantmentTypeReader = enchantmentTypeReader;
    }

    @Override
    protected List<EnchantmentType> read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection enchants = section.getConfigurationSection(key);

        Set<String> keys = enchants.getKeys(false);
        List<EnchantmentType> types = Lists.newArrayList();
        for (String ench : keys) {
            types.add(enchantmentTypeReader.readFromSection(enchants, ench));
        }

        return types;
    }
}
