package org.spauk.currencyconverter.provider.swop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SwopWebClientConfigurationTest {

    @InjectMocks
    private SwopWebClientConfiguration swopWebClientConfiguration;

    @Mock
    private WebClient.Builder webClientBuilderMock;

    @Mock
    private WebClient webClientMock;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilderMock.baseUrl(anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);

        setPrivateField(swopWebClientConfiguration, "url", "urlValue");
        setPrivateField(swopWebClientConfiguration, "apiKey", "apiKeyValue");
    }

    @Test
    public void shouldBuildWebClient() {
        WebClient webClient = swopWebClientConfiguration.webClient(webClientBuilderMock);
        assertNotNull(webClient);
        verify(webClientBuilderMock).build();
    }

    @Test
    public void shouldConfigureWebClient() {
        swopWebClientConfiguration.webClient(webClientBuilderMock);
        verify(webClientBuilderMock).baseUrl("urlValue");
        verify(webClientBuilderMock).defaultHeader("Authorization", "ApiKey apiKeyValue");
    }

    private void setPrivateField(Object object, String fieldName, String value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
