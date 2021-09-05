package gamedia.recruitment.task.md.currencies.client;

import gamedia.recruitment.task.md.currencies.ClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "coin-ranking.api")
@Configuration
@RequiredArgsConstructor
class CoinRankingClientConfig implements ClientConfig {

    @Value("${coin-ranking.api.base-url}")
    private String apiBaseUrl;

    @Value("${coin-ranking.api.key}")
    private String apiKey;

    @Override
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }
}
