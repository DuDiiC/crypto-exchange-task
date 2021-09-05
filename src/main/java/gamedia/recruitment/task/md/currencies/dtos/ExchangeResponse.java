package gamedia.recruitment.task.md.currencies.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ExchangeResponse(
        @JsonProperty("from") String sourceCurrency,
        @JsonProperty("to") Map<String, ExchangeForCurrency> destinationCurrencies
) {
}
