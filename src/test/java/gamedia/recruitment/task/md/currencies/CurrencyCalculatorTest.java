package gamedia.recruitment.task.md.currencies;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyCalculatorTest {

    private final CurrencyCalculator currencyCalculator = new CurrencyCalculator();

    @Nested
    class CalculateRateTest {

        @ParameterizedTest
        @CsvSource({
                "1,1,4,1.0000", "1,1,2,1.00", "15,60,2,4.00",
                "60,15,2,0.25", "60,15,1,0.3", "1,0,2,0.00"
        })
        void whenStandardInput_thenReturnCorrectRate(
                BigDecimal sourceCoinUsdPrice, BigDecimal destCoinUsdPrice,
                Integer scale, BigDecimal result
        ) {
            BigDecimal calculated = currencyCalculator.calculateRate(
                    sourceCoinUsdPrice, destCoinUsdPrice, scale
            ).orElseThrow();
            assertEquals(result, calculated);
        }

        @ParameterizedTest
        @MethodSource("anyNullArgument")
        void whenNullArguments_thenReturnEmptyOptional(BigDecimal sourceCoinUsdPrice, BigDecimal destCoinUsdPrice) {
            assertTrue(currencyCalculator.calculateRate(sourceCoinUsdPrice, destCoinUsdPrice, 1).isEmpty());
        }

        private static Stream<Arguments> anyNullArgument() {
            return Stream.of(
                    Arguments.of(null, null),
                    Arguments.of(null, BigDecimal.ONE),
                    Arguments.of(BigDecimal.ONE, null)
            );
        }

        @Test
        void whenTryDivideByZero_thenReturnEmptyOptional() {
            assertTrue(currencyCalculator.calculateRate(BigDecimal.ZERO, BigDecimal.ONE, 1).isEmpty());
        }
    }

    @Nested
    class CalculateExchangeTest {

        @ParameterizedTest
        @CsvSource({
                "1,1,1,2,1.00", "2,4,4,2,32.00",
                "1.00,0,0,4,0.0000", "1.23,4.56,7.89,4,44.2534"
        })
        void whenStandardInput_thenReturnCorrectExchange(
                BigDecimal sourceCoinUsdPrice, BigDecimal amount, BigDecimal quoteToDestCoin,
                int scale, BigDecimal expected
        ) {
            BigDecimal calculated = currencyCalculator.calculateExchange(
                    sourceCoinUsdPrice, amount, quoteToDestCoin, scale
            ).orElseThrow();
            assertEquals(expected, calculated);
        }

        @ParameterizedTest
        @MethodSource("anyNullArgument")
        void whenNullArguments_thenReturnEmptyOptional(
                BigDecimal sourceCoinUsdPrice, BigDecimal amount, BigDecimal quoteToDestCoin
        ) {
            assertTrue(currencyCalculator.calculateExchange(sourceCoinUsdPrice, amount, quoteToDestCoin, 1).isEmpty());
        }

        private static Stream<Arguments> anyNullArgument() {
            return Stream.of(
                    Arguments.of(null, null, null),
                    Arguments.of(null, null, BigDecimal.ONE),
                    Arguments.of(null, BigDecimal.ONE, null),
                    Arguments.of(BigDecimal.ONE, null, null),
                    Arguments.of(null, BigDecimal.ONE, BigDecimal.ONE),
                    Arguments.of(BigDecimal.ONE, null, BigDecimal.ONE),
                    Arguments.of(BigDecimal.ONE, BigDecimal.ONE, null)
            );
        }
    }

    @Nested
    class CalculateFeeTest {

        @ParameterizedTest
        @CsvSource({
                "1,1,1,2,1.00", "2,4,4,2,32.00",
                "1.00,0,0,4,0.0000", "1.23,4.56,7.89,4,44.2534"
        })
        void whenStandardInput_thenReturnCorrectFee(
                BigDecimal sourceCoinUsdPrice, BigDecimal standardFee,
                BigDecimal amount, int scale, BigDecimal expected) {
            BigDecimal calculated = currencyCalculator.calculateFee(
                    sourceCoinUsdPrice, standardFee, amount, scale
            ).orElseThrow();
            assertEquals(expected, calculated);
        }

        @ParameterizedTest
        @MethodSource("anyNullArgument")
        void whenNullArguments_thenReturnEmptyOptional(
                BigDecimal sourceCoinUsdPrice, BigDecimal standardFee, BigDecimal amount
        ) {
            assertTrue(currencyCalculator.calculateFee(sourceCoinUsdPrice, standardFee, amount, 1).isEmpty());
        }

        private static Stream<Arguments> anyNullArgument() {
            return Stream.of(
                    Arguments.of(null, null, null),
                    Arguments.of(null, null, BigDecimal.ONE),
                    Arguments.of(null, BigDecimal.ONE, null),
                    Arguments.of(BigDecimal.ONE, null, null),
                    Arguments.of(null, BigDecimal.ONE, BigDecimal.ONE),
                    Arguments.of(BigDecimal.ONE, null, BigDecimal.ONE),
                    Arguments.of(BigDecimal.ONE, BigDecimal.ONE, null)
            );
        }
    }
}
