package gg.uhc.purchaseableenchants.configuration;

import com.google.common.base.Optional;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

public class ItemStackReader extends ConfigurationReader<ItemStack> {

    protected final MaterialReader materialReader;
    protected final PositiveIntegerReader integerReader;

    public ItemStackReader(MaterialReader materialReader, PositiveIntegerReader integerReader) {
        this.materialReader = materialReader;
        this.integerReader = integerReader;
    }

    @Override
    protected ItemStack read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection itemStackSection = section.getConfigurationSection(key);

        Material material = materialReader.readFromSection(itemStackSection, "material");

        int amount = integerReader.readFromSection(itemStackSection, "amount");

        Optional<Integer> data = integerReader.readFromSectionOptional(itemStackSection, "data");

        return new ItemStack(material, amount, data.or(0).shortValue());
    }
}
