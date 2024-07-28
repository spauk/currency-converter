package org.spauk.currencyconverter.provider.swop;

import org.spauk.currencyconverter.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = Application.class)
public class SwopWebClientConfigurationIntegrationTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private Environment environment;

    @Test
    public void shouldCreateWebClientBean() {
        assertNotNull(webClient);
    }

    @Test
    public void shouldConfigureWebClientBean() {
        String baseUrl = environment.getProperty("exchange-rate-provider.swop.base-url");
        String apiKey = environment.getProperty("exchange-rate-provider.swop.api-key");
        assertNotNull(baseUrl);
        assertFalse(baseUrl.isEmpty());
        assertNotNull(apiKey);
        assertFalse(apiKey.isEmpty());
    }
}
