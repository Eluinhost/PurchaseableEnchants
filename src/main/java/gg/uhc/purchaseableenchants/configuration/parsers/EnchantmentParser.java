package gg.uhc.purchaseableenchants.configuration.parsers;

import com.google.common.base.Optional;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentParser implements Parser<Enchantment> {
    public Optional<Enchantment> parse(String enchantment) throws IllegalArgumentException {
        return Optional.fromNullable(Enchantment.getByName(enchantment));
    }
}
