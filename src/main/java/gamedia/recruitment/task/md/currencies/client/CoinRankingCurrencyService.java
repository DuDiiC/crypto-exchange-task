package gamedia.recruitment.task.md.currencies.client;

import gamedia.recruitment.task.md.currencies.ClientConfig;
import gamedia.recruitment.task.md.currencies.CurrencyCalculator;
import gamedia.recruitment.task.md.currencies.CurrencyService;
import gamedia.recruitment.task.md.currencies.dtos.ExchangeForCurrency;
import gamedia.recruitment.task.md.currencies.dtos.ExchangeRequest;
import gamedia.recruitment.task.md.currencies.dtos.QuotesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class CoinRankingCurrencyService implements CurrencyService {

    private final RestTemplate restTemplate;
    private final CurrencyCalculator currencyCalculator;
    private final ClientConfig clientConfig;

    @Override
    public QuotesResponse getQuotesForCurrency(String sourceCoin, List<String> destCoins) {

        String uri = buildUri(sourceCoin, destCoins);
        Response response = restTemplate.getForObject(uri, Response.class, Map.of("x-access-token", clientConfig.getApiKey()));
        if (response == null || response.data() == null) {
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

        String uri = buildUri(exchangeRequest.sourceCoin(), exchangeRequest.destCoins());
        Response response = restTemplate.getForObject(uri, Response.class, Map.of("x-access-token", clientConfig.getApiKey()));
        if (response == null || response.data() == null) {
            return Collections.emptyMap();
        }
        Map<String, Coin> coins = prepareCoinsMapFromClientApiResponse(response);

        BigDecimal sourceCoinPrice = coins.get(exchangeRequest.sourceCoin()).price();
        coins.remove(exchangeRequest.sourceCoin());

        Map<String, BigDecimal> quotes = calculateQuotes(sourceCoinPrice, exchangeRequest.destCoins(), coins);
        return calculateExchanges(exchangeRequest, sourceCoinPrice, coins, quotes);
    }

    String buildUri(String currency, List<String> filters) {

        String uri = clientConfig.getApiBaseUrl();

        if (!filters.isEmpty()) {
            StringBuilder sb = new StringBuilder(uri).append("?symbols[]=").append(currency);
            filters.forEach(filter -> sb.append("&symbols[]=").append(filter));
            uri = sb.toString();
        }

        return uri;
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
}
