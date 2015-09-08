package gg.uhc.purchaseableenchants.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class BooleanReader extends ConfigurationReader<Boolean> {

    @Override
    protected Boolean read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isBoolean(key)) throw up(WRONG_TYPE, key, "true/false", section.get(key));

        return section.getBoolean(key);
    }
}
