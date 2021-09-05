package gamedia.recruitment.task.md.currencies;

import gamedia.recruitment.task.md.currencies.dtos.ExchangeRequest;
import gamedia.recruitment.task.md.currencies.dtos.QuotesResponse;

import java.util.List;
import java.util.Map;

public interface CurrencyService {

    QuotesResponse getQuotesForCurrency(String sourceCoin, List<String> destCoins);

    Map<String, Object> getExchangesForCurrency(ExchangeRequest exchangeRequest);
}
