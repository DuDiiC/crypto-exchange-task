package gamedia.recruitment.task.md.currencies.client;

import gamedia.recruitment.task.md.currencies.ClientConfig;
import gamedia.recruitment.task.md.currencies.ClientRestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class CoinRankingClientRestRequest<T> implements ClientRestRequest<T> {

    private final RestTemplate restTemplate;
    private final ClientConfig clientConfig;

    @Override
    public Optional<T> getForObject(String uri, Class<? extends T> clazz) {
        return Optional.ofNullable(restTemplate.getForObject(uri, clazz, Map.of("x-access-token", clientConfig.getApiKey())));
    }
}
