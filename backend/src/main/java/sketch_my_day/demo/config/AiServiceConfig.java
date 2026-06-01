package sketch_my_day.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class AiServiceConfig {

    @Bean // Spring 이 관리하는 객체를 등록한다
    public WebClient aiWebClient(
            @Value("${ai.service.base-url}") String baseUrl
    ) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException(
                    "ai.service.base-url is empty. Set AI_SERVICE_BASE_URL (e.g. http://127.0.0.1:8000)."
            );
        }

        try {
            URI uri = new URI(baseUrl);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalStateException(
                        "ai.service.base-url must be an absolute URL (e.g. http://127.0.0.1:8000). Current value: " + baseUrl
                );
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid ai.service.base-url: " + baseUrl, e);
        }

        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
