package org.spauk.currencyconverter.provider.swop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

public class SwopExchangeRateProviderTest {
    @InjectMocks
    private SwopExchangeRateProvider swopCurrenciesProvider;
    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;

    @Mock
    private WebClient.ResponseSpec responseMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
    }

    @Test
    public void shouldReturnExchangeRates() {
        when(requestHeadersUriMock.uri("/rates/{sourceCurrency}/{targetCurrency}", "EUR", "USD")).thenReturn(requestHeadersMock);
        when(responseMock.bodyToMono(SwopExchangeRateResponse.class)).thenReturn(Mono.just(new SwopExchangeRateResponse(1.23)));

        Mono<Double> rateMono = swopCurrenciesProvider.getExchangeRate("EUR", "USD");

        StepVerifier.create(rateMono)
                .expectNext(1.23)
                .verifyComplete();
    }

    @Test
    public void shouldThrowExceptionWhenGettingExchangeRateFails() {
        WebClientResponseException clientException = WebClientResponseException.create(
                400, "Bad Request", null, null, null);

        when(requestHeadersUriMock.uri("/rates/{sourceCurrency}/{targetCurrency}", "EUR", "USD")).thenReturn(requestHeadersMock);
        when(responseMock.bodyToMono(SwopExchangeRateResponse.class)).thenReturn(Mono.error(clientException));

        Mono<Double> rateMono = swopCurrenciesProvider.getExchangeRate("EUR", "USD");

        StepVerifier.create(rateMono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void shouldReturnSupportedCurrencies() {
        List<SwopSupportedCurrencyResponse> currencies = Arrays.asList(new SwopSupportedCurrencyResponse("EUR", true), new SwopSupportedCurrencyResponse("USD", true), new SwopSupportedCurrencyResponse("KKK", false));
        when(requestHeadersUriMock.uri("/currencies")).thenReturn(requestHeadersMock);
        when(responseMock.bodyToMono(new ParameterizedTypeReference<List<SwopSupportedCurrencyResponse>>() {})).thenReturn(Mono.just(currencies));

        Mono<List<String>> activeCurrencies = swopCurrenciesProvider.getSupportedCurrencies();

        StepVerifier.create(activeCurrencies)
                .expectNext(Arrays.asList("EUR", "USD"))
                .verifyComplete();
    }

    @Test
    public void shouldThrowExceptionWhenGettingSupportedCurrenciesFails() {
        WebClientResponseException exception = WebClientResponseException.create(
                500, "Internal Server Error", null, null, null);

        when(requestHeadersUriMock.uri("/currencies")).thenReturn(requestHeadersMock);
        when(responseMock.bodyToMono(new ParameterizedTypeReference<List<SwopSupportedCurrencyResponse>>() {})).thenReturn(Mono.error(exception));

        Mono<List<String>> activeCurrencies = swopCurrenciesProvider.getSupportedCurrencies();

        StepVerifier.create(activeCurrencies)
                .expectError(RuntimeException.class)
                .verify();
    }

}
