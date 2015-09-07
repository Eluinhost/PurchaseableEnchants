package gg.uhc.purchaseableenchants.configuration.parsers;

import com.google.common.base.Optional;

public interface Parser<T> {
    Optional<T> parse(String raw);
}
