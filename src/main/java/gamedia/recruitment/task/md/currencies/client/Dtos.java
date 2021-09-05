package gamedia.recruitment.task.md.currencies.client;

import java.math.BigDecimal;
import java.util.List;

record Response(
        String status,
        ResponseData data
) {
}

record ResponseData(
        List<Coin> coins
) {
}

record Coin(
        String symbol,
        BigDecimal price,
        Integer rank
) {
}
