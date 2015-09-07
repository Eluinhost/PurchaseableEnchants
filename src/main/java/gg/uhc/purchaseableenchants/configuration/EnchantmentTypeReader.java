package gg.uhc.purchaseableenchants.configuration;

import com.google.common.base.Optional;
import gg.uhc.purchaseableenchants.configuration.parsers.EnchantmentParser;
import gg.uhc.purchaseableenchants.enchants.EnchantmentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantmentTypeReader extends ConfigurationReader<EnchantmentType> {

    protected final EnchantmentParser parser;
    protected final EnchantmentTierCostReader tierCostReader;

    public EnchantmentTypeReader(EnchantmentParser parser, EnchantmentTierCostReader tierCostReader) {
        this.parser = parser;
        this.tierCostReader = tierCostReader;
    }

    @Override
    protected EnchantmentType read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        Optional<Enchantment> enchantOpt = parser.parse(key);

        if (!enchantOpt.isPresent()) throw up(WRONG_TYPE, key, "enchantment type", key);

        Enchantment enchantment = enchantOpt.get();

        ItemStack[] tiers = tierCostReader.readFromSection(section, key);

        return new EnchantmentType(enchantment, tiers);
    }
}
