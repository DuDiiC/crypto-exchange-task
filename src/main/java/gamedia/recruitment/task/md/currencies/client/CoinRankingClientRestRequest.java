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
class CoinRankingClientRestRequest implements ClientRestRequest<Response> {

    private final RestTemplate restTemplate;
    private final ClientConfig clientConfig;

    @Override
    public Optional<Response> getForObject(String uri) {
        return Optional.ofNullable(restTemplate.getForObject(uri, Response.class, Map.of("x-access-token", clientConfig.getApiKey())));
    }
}
