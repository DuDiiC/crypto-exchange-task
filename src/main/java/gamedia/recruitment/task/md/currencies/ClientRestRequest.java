package gamedia.recruitment.task.md.currencies;

import java.util.Optional;

public interface ClientRestRequest<T> {

    Optional<T> getForObject(String uri, Class<? extends T> clazz);
}
