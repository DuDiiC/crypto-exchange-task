package gamedia.recruitment.task.md.currencies.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ExchangeForCurrency(
        @JsonProperty("rate") BigDecimal rate,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("result") BigDecimal result,
        @JsonProperty("fee") BigDecimal fee
) {
}
