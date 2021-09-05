package gamedia.recruitment.task.md.currencies.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record ExchangeRequest(
        @JsonProperty("from") @NotNull String sourceCoin,
        @JsonProperty("to") @NotEmpty List<String> destCoins,
        @JsonProperty("amount") @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount
) {
}
