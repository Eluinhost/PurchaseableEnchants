package gg.uhc.purchaseableenchants.configuration;

import com.google.common.base.Optional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public abstract class ConfigurationReader<T> {

    protected static final String MISSING_KEY = "Required configuration key missing `%s`";
    protected static final String WRONG_TYPE = "Expected key `%s` to be a '%s' but found: %s";

    public final Optional<T> readFromSection(ConfigurationSection section, String key, boolean required) throws InvalidConfigurationException {
        if (!section.contains(key)) {
            if (required) {
                throw up(MISSING_KEY, key);
            } else {
                return Optional.absent();
            }
        }

        return Optional.of(read(section, key));
    }

    public final T readFromSection(ConfigurationSection section, String key) throws InvalidConfigurationException {
        return readFromSection(section, key, true).get();
    }

    public final Optional<T> readFromSectionOptional(ConfigurationSection section, String key) throws InvalidConfigurationException {
        return readFromSection(section, key, false);
    }

    protected abstract T read(ConfigurationSection section, String key) throws InvalidConfigurationException;

    protected final InvalidConfigurationException up(String message, Object... params) throws InvalidConfigurationException {
        return new InvalidConfigurationException(String.format(message, params));
    }
}
