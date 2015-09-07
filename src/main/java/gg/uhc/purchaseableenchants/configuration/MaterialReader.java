package gg.uhc.purchaseableenchants.configuration;

import com.google.common.base.Optional;
import gg.uhc.purchaseableenchants.configuration.parsers.MaterialParser;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class MaterialReader extends ConfigurationReader<Material> {

    protected final static String INVALID_MATERIAL = "Invalid material '%s' for key `%s`";

    protected final MaterialParser parser;

    public MaterialReader(MaterialParser parser) {
        this.parser = parser;
    }

    @Override
    protected Material read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isString(key)) throw up(WRONG_TYPE, key, "String", section.get(key));

        String materialName = section.getString(key);

        Optional<Material> material = parser.parse(materialName);

        if (!material.isPresent()) throw up(INVALID_MATERIAL, materialName, key);

        return material.get();
    }
}
