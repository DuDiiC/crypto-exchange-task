package gamedia.recruitment.task.md.currencies.client;

import gamedia.recruitment.task.md.currencies.ClientConfig;
import gamedia.recruitment.task.md.currencies.CurrencyCalculator;
import gamedia.recruitment.task.md.currencies.dtos.ExchangeRequest;
import gamedia.recruitment.task.md.currencies.dtos.QuotesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinRankingCurrencyServiceTest {

    private final Map<String, Coin> coins = Map.of(
            "BTC", new Coin("BTC", BigDecimal.ONE, 1),
            "ETH", new Coin("ETH", BigDecimal.ONE, 2),
            "PLN", new Coin("PLN", BigDecimal.ONE, 3),
            "ETH2", new Coin("ETH", BigDecimal.ONE, 4)
    );
    private final ClientConfig clientConfig = new ClientConfig() {
        @Override
        public String getApiBaseUrl() {
            return "google.com";
        }

        @Override
        public String getApiKey() {
            return "xyz";
        }
    };
    private CoinRankingCurrencyService service;
    @Mock
    private CurrencyCalculator currencyCalculator;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        service = new CoinRankingCurrencyService(restTemplate, currencyCalculator, clientConfig);
    }

    @Nested
    class GetQuotesForCurrencyTest {

        @Test
        void whenGetQuotesForCurrency_thenReturnCorrectResponseRecord() {
            when(currencyCalculator.calculateRate(any(), any(), anyInt()))
                    .thenReturn(Optional.of(BigDecimal.ONE));
            when(restTemplate.getForObject(anyString(), any(), anyMap()))
                    .thenReturn(new Response("success", new ResponseData(List.of(
                            coins.get("BTC"), coins.get("ETH"), coins.get("PLN"))
                    )));
            QuotesResponse quotesResponse = service.getQuotesForCurrency(
                    coins.get("BTC").symbol(), List.of(coins.get("ETH").symbol(), coins.get("PLN").symbol())
            );

            assertAll(
                    () -> assertEquals(2, quotesResponse.rates().size()),
                    () -> assertEquals(coins.get("BTC").symbol(), quotesResponse.sourceCryptoName()),
                    () -> assertNotNull(quotesResponse.rates().get("ETH")),
                    () -> assertNotNull(quotesResponse.rates().get("PLN"))
            );
        }
    }

    @Nested
    class GetExchangeForCurrencyTest {

        @Test
        void whenGetQuotesForCurrency_thenReturnCorrectResponseRecord() {
            when(currencyCalculator.calculateRate(any(), any(), anyInt()))
                    .thenReturn(Optional.of(BigDecimal.ONE));
            when(currencyCalculator.calculateExchange(any(), any(), any(), anyInt()))
                    .thenReturn(Optional.of(BigDecimal.ONE));
            when(currencyCalculator.calculateFee(any(), any(), any(), anyInt()))
                    .thenReturn(Optional.of(BigDecimal.ONE));
            when(restTemplate.getForObject(anyString(), any(), anyMap()))
                    .thenReturn(new Response("success", new ResponseData(List.of(
                            coins.get("BTC"), coins.get("ETH"), coins.get("PLN"))
                    )));

            Map<String, Object> resultMap = service.getExchangesForCurrency(
                    new ExchangeRequest("BTC", List.of("ETH", "PLN"), BigDecimal.ONE)
            );

            assertAll(
                    () -> assertEquals(2, resultMap.size()),
                    () -> assertNotNull(resultMap.get("ETH")),
                    () -> assertNotNull(resultMap.get("PLN"))
            );
        }
    }

    @Nested
    class BuildUriTest {

        @Test
        void whenEmptyFilterList_thenPrepareUriCorrectly() {
            String uri = service.buildUri(coins.get("BTC").symbol(), Collections.emptyList());
            assertEquals(clientConfig.getApiBaseUrl(), uri);
        }

        @Test
        void whenOnlyOneFilter_thenPrepareUriCorrectly() {
            String uri = service.buildUri(
                    coins.get("BTC").symbol(),
                    List.of(coins.get("ETH").symbol())
            );
            assertEquals(clientConfig.getApiBaseUrl() + "?symbols[]=BTC&symbols[]=ETH", uri);
        }

        @Test
        void whenMultipleFilters_thenPrepareUriCorrectly() {
            String uri = service.buildUri(
                    coins.get("BTC").symbol(),
                    List.of(coins.get("ETH").symbol(), coins.get("PLN").symbol())
            );
            assertEquals(clientConfig.getApiBaseUrl() + "?symbols[]=BTC&symbols[]=ETH&symbols[]=PLN", uri);
        }
    }

    @Nested
    class PrepareCoinsMapFromClientApiResponse {

        @Test
        void whenWithoutDuplicates_thenReturnMapWithCoinsFromResponseRecord() {
            Response response = new Response("success", new ResponseData(List.of(
                    coins.get("BTC"), coins.get("ETH"), coins.get("PLN")
            )));

            Map<String, Coin> preparedMap = service.prepareCoinsMapFromClientApiResponse(response);

            assertAll(
                    () -> assertEquals(3, preparedMap.size()),
                    () -> assertEquals(coins.get("BTC"), preparedMap.get(coins.get("BTC").symbol())),
                    () -> assertEquals(coins.get("ETH"), preparedMap.get(coins.get("ETH").symbol())),
                    () -> assertEquals(coins.get("PLN"), preparedMap.get(coins.get("PLN").symbol()))
            );
        }

        @Test
        void whenAnyDuplicate_thenReturnMapWithCoinsFromResponseRecord() {
            Response response = new Response("success", new ResponseData(List.of(
                    coins.get("BTC"), coins.get("ETH"), coins.get("PLN"), coins.get("ETH2")
            )));

            Map<String, Coin> preparedMap = service.prepareCoinsMapFromClientApiResponse(response);

            assertAll(
                    () -> assertEquals(3, preparedMap.size()),
                    () -> assertEquals(coins.get("BTC"), preparedMap.get(coins.get("BTC").symbol())),
                    () -> assertEquals(coins.get("ETH"), preparedMap.get(coins.get("ETH").symbol())),
                    () -> assertEquals(coins.get("PLN"), preparedMap.get(coins.get("PLN").symbol()))
            );
        }
    }
}
