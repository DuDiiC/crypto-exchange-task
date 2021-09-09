package gamedia.recruitment.task.md.currencies.client;

import gamedia.recruitment.task.md.currencies.ClientRestRequest;
import gamedia.recruitment.task.md.currencies.ClientUriBuilder;
import gamedia.recruitment.task.md.currencies.CurrencyCalculator;
import gamedia.recruitment.task.md.currencies.CurrencyService;
import gamedia.recruitment.task.md.currencies.dtos.ExchangeForCurrency;
import gamedia.recruitment.task.md.currencies.dtos.ExchangeRequest;
import gamedia.recruitment.task.md.currencies.dtos.QuotesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
class CoinRankingCurrencyService implements CurrencyService {

    private final CurrencyCalculator currencyCalculator;
    private final ClientUriBuilder uriBuilder;
    private final ClientRestRequest<Response> clientRestRequest;

    @Override
    public QuotesResponse getQuotesForCurrency(String sourceCoin, List<String> destCoins) {

        final String uri = uriBuilder.buildUri(prepareArray(sourceCoin, destCoins)).orElseThrow();
        final Response response = clientRestRequest.getForObject(uri, Response.class).orElseThrow();

        if (response.data() == null) {
            return new QuotesResponse(sourceCoin, Collections.emptyMap());
        }
        Map<String, Coin> coins = prepareCoinsMapFromClientApiResponse(response);

        BigDecimal sourceCoinPrice = coins.get(sourceCoin).price();
        coins.remove(sourceCoin);

        Map<String, BigDecimal> quotes = calculateQuotes(sourceCoinPrice, destCoins, coins);
        return new QuotesResponse(sourceCoin, quotes);
    }

    @Override
    public Map<String, Object> getExchangesForCurrency(ExchangeRequest exchangeRequest) {
        final String uri = uriBuilder.buildUri(
                prepareArray(exchangeRequest.sourceCoin(), exchangeRequest.destCoins())
        ).orElseThrow();

        final Response response = clientRestRequest.getForObject(uri, Response.class).orElseThrow();

        Map<String, Coin> coins = prepareCoinsMapFromClientApiResponse(response);

        BigDecimal sourceCoinPrice = coins.get(exchangeRequest.sourceCoin()).price();
        coins.remove(exchangeRequest.sourceCoin());

        Map<String, BigDecimal> quotes = calculateQuotes(sourceCoinPrice, exchangeRequest.destCoins(), coins);
        return calculateExchanges(exchangeRequest, sourceCoinPrice, coins, quotes);
    }

    Map<String, Coin> prepareCoinsMapFromClientApiResponse(Response response) {
        return response.data().coins().stream()
                .collect(Collectors.toMap(
                        Coin::symbol,
                        Function.identity(),
                        // removing duplicates - take coin higher in ranking
                        (coin1, coin2) -> coin1.rank() < coin2.rank() ? coin1 : coin2
                ));
    }

    Map<String, BigDecimal> calculateQuotes(BigDecimal sourceCoinPrice, List<String> destCoins, Map<String, Coin> coins) {
        Map<String, BigDecimal> quotes = new HashMap<>();
        if (!destCoins.isEmpty()) {
            destCoins.forEach(destCoin ->
                    quotes.put(destCoin, currencyCalculator.calculateRate(
                            sourceCoinPrice,
                            coins.get(destCoin).price(),
                            10
                    ).orElse(BigDecimal.ZERO))
            );
        } else {
            coins.forEach((symbol, coin) ->
                    quotes.put(symbol, currencyCalculator.calculateRate(
                            sourceCoinPrice,
                            coin.price(),
                            10
                    ).orElse(BigDecimal.ZERO))
            );
        }
        return quotes;
    }

    Map<String, Object> calculateExchanges(ExchangeRequest exchangeRequest, BigDecimal sourceCoinPrice, Map<String, Coin> coins, Map<String, BigDecimal> quotes) {

        Map<String, Object> exchanges = new HashMap<>();

        coins.forEach((symbol, coin) -> {

            BigDecimal exchange = currencyCalculator.calculateExchange(
                    sourceCoinPrice,
                    exchangeRequest.amount(),
                    quotes.get(symbol),
                    4
            ).orElse(BigDecimal.ZERO).setScale(10, RoundingMode.HALF_UP);

            BigDecimal fee = currencyCalculator.calculateFee(
                    sourceCoinPrice,
                    new BigDecimal("0.01"),
                    exchangeRequest.amount(),
                    10
            ).orElse(BigDecimal.ZERO);

            exchanges.put(symbol, new ExchangeForCurrency(
                    quotes.get(symbol),
                    exchangeRequest.amount(),
                    exchange.add(fee),
                    fee
            ));
        });

        return exchanges;
    }

    private String[] prepareArray(String param, List<String> otherParams) {
        return Stream.concat(Arrays.stream(Collections.singletonList(param).toArray()), otherParams.stream()).toArray(String[]::new);
    }
}
