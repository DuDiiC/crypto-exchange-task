package gamedia.recruitment.task.md.currencies;

import gamedia.recruitment.task.md.currencies.dtos.ExchangeRequest;
import gamedia.recruitment.task.md.currencies.dtos.QuotesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
class CurrencyController {

    private final CurrencyService service;

    @GetMapping("/{currency}")
    public QuotesResponse getCurrencyQuote(
            @PathVariable @NotNull String currency,
            @RequestParam(name = "filter[]", required = false, defaultValue = "") List<String> filters
    ) {
        return service.getQuotesForCurrency(currency, filters);
    }

    @PostMapping("/exchange")
    public Map<String, Object> getCalculatedExchange(@RequestBody @Valid ExchangeRequest exchangeRequest) {
        return Stream.of(Map.of("from", exchangeRequest.sourceCoin()), service.getExchangesForCurrency(exchangeRequest))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
