package sketch_my_day.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiServiceConfig {

    @Bean // Spring 이 관리하는 객체를 등록한다
    public WebClient aiWebClient(
            @Value("${ai.service.base-url}") String baseUrl
    ) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
