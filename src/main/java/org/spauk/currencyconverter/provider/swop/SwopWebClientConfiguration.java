package org.spauk.currencyconverter.provider.swop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SwopWebClientConfiguration {

    @Value("${exchange-rate-provider.swop.base-url}")
    private String url;

    @Value("${exchange-rate-provider.swop.api-key}")
    private String apiKey;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(url)
                .defaultHeader("Authorization", "ApiKey " + apiKey)
                .build();
    }
}
