package gg.uhc.purchaseableenchants.configuration;

import com.google.common.collect.Lists;
import gg.uhc.purchaseableenchants.enchants.EnchantmentType;
import gg.uhc.purchaseableenchants.offers.EnchantmentOffers;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.Set;

public class EnchantmentOffersReader extends ConfigurationReader<EnchantmentOffers> {

    protected final EnchantmentTypeReader enchantmentTypeReader;

    public EnchantmentOffersReader(EnchantmentTypeReader enchantmentTypeReader) {
        this.enchantmentTypeReader = enchantmentTypeReader;
    }

    @Override
    protected EnchantmentOffers read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection enchants = section.getConfigurationSection(key);

        Set<String> keys = enchants.getKeys(false);
        List<EnchantmentType> types = Lists.newArrayList();
        for (String ench : keys) {
            types.add(enchantmentTypeReader.readFromSection(enchants, ench));
        }

        return new EnchantmentOffers(types);
    }
}
