package gamedia.recruitment.task.md.currencies.client;

import gamedia.recruitment.task.md.currencies.ClientConfig;
import gamedia.recruitment.task.md.currencies.ClientUriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class CoinRankingClientUriBuilder implements ClientUriBuilder {

    private final ClientConfig clientConfig;

    @Override
    public Optional<String> buildUri(String... parameters) {
        if (!inputIsValid(parameters)) {
            return Optional.empty();
        }
        StringBuilder uriBuilder = new StringBuilder(clientConfig.getApiBaseUrl());
        if (parameters.length > 1) { // filters too
            uriBuilder.append("?symbols[]=").append(parameters[0]);
            for (int i = 1; i < parameters.length; i++) {
                uriBuilder.append("&symbols[]=").append(parameters[i]);
            }
        }
        return Optional.of(uriBuilder.toString());
    }

    private boolean inputIsValid(String... parameters) {
        return parameters.length > 0;
    }
}
