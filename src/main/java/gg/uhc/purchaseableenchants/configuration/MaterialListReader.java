package gg.uhc.purchaseableenchants.configuration;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import gg.uhc.purchaseableenchants.configuration.parsers.MaterialParser;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;

public class MaterialListReader extends ConfigurationReader<List<Material>> {

    protected final static String INVALID_MATERIAL = "Invalid material '%s' for key `%s`";

    protected final MaterialParser parser;

    public MaterialListReader(MaterialParser parser) {
        this.parser = parser;
    }

    @Override
    protected List<Material> read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isList(key)) throw up(WRONG_TYPE, key, "list", section.get(key));

        List<String> raw = section.getStringList(key);
        List<Material> materials = Lists.newArrayList();
        for (String string : raw) {
            Optional<Material> mat = parser.parse(string);

            if (!mat.isPresent()) throw up(INVALID_MATERIAL, string, key);

            materials.add(mat.get());
        }

        return materials;
    }
}
