package gg.uhc.purchaseableenchants.configuration.parsers;

import com.google.common.base.Optional;
import org.bukkit.Material;

public class MaterialParser implements Parser<Material> {
    @Override
    public Optional<Material> parse(String raw) {
        return Optional.fromNullable(Material.getMaterial(raw));
    }
}
