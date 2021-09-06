package gamedia.recruitment.task.md.currencies;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class CurrencyCalculator {

    public Optional<BigDecimal> calculateRate(BigDecimal sourceCoinUsdPrice, BigDecimal destCoinUsdPrice, int scale) {
        if (sourceCoinUsdPrice == null || destCoinUsdPrice == null || sourceCoinUsdPrice.doubleValue() == 0.0) {
            return Optional.empty();
        }
        return Optional.of(destCoinUsdPrice.divide(sourceCoinUsdPrice, scale, RoundingMode.HALF_UP));
    }

    public Optional<BigDecimal> calculateExchange(BigDecimal sourceCoinUsdPrice, BigDecimal amount, BigDecimal quoteToDestCoin, int scale) {
        if (sourceCoinUsdPrice == null || amount == null || quoteToDestCoin == null) {
            return Optional.empty();
        }
        return Optional.of(sourceCoinUsdPrice.multiply(amount).multiply(quoteToDestCoin).setScale(scale, RoundingMode.HALF_UP));
    }

    public Optional<BigDecimal> calculateFee(BigDecimal sourceCoinUsdPrice, BigDecimal standardFee, BigDecimal amount, int scale) {
        if (sourceCoinUsdPrice == null || standardFee == null || amount == null) {
            return Optional.empty();
        }
        return Optional.of(sourceCoinUsdPrice.multiply(standardFee).multiply(amount).setScale(scale, RoundingMode.HALF_UP));
    }
}
