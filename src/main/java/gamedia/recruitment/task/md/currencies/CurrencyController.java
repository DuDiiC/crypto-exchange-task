package gamedia.recruitment.task.md.currencies;

import gamedia.recruitment.task.md.currencies.dtos.ExchangeRequest;
import gamedia.recruitment.task.md.currencies.dtos.QuotesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> calculateExchange(@RequestBody @Valid ExchangeRequest exchangeRequest) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("from", exchangeRequest.sourceCoin());
        Map<String, Object> exchangesForCurrency = service.getExchangesForCurrency(exchangeRequest);
        responseBody.putAll(exchangesForCurrency);
        return responseBody;
    }
}
