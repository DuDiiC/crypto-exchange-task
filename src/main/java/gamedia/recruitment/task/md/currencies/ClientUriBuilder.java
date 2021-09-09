package gamedia.recruitment.task.md.currencies;

import java.util.Optional;

public interface ClientUriBuilder {

    Optional<String> buildUri(String... parameters);
}
