package gamedia.recruitment.task.md.currencies.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public record QuotesResponse(
        @JsonProperty("source") String sourceCryptoName,
        @JsonProperty("rates") Map<String, BigDecimal> rates
) {
}
