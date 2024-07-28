package org.spauk.currencyconverter.service;

import org.spauk.currencyconverter.provider.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SupportedCurrenciesServiceTest {

    @InjectMocks
    private SupportedCurrenciesService supportedCurrenciesService;

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnTrueWhenCurrencyIsValid() {
        when(exchangeRateProvider.getSupportedCurrencies()).thenReturn(Mono.just(Arrays.asList("EUR", "USD")));
        Mono<Boolean> isValid = supportedCurrenciesService.isValidCurrency("EUR");
        StepVerifier.create(isValid)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void shouldReturnFalseWhenCurrencyIsInvalid() {
        when(exchangeRateProvider.getSupportedCurrencies()).thenReturn(Mono.just(Arrays.asList("EUR", "USD")));
        Mono<Boolean> isValid = supportedCurrenciesService.isValidCurrency("KKK");
        StepVerifier.create(isValid)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void shouldReturnFalseWhenThereAreNoSupportedCurrencies() {
        when(exchangeRateProvider.getSupportedCurrencies()).thenReturn(Mono.just(Collections.emptyList()));
        Mono<Boolean> isValid = supportedCurrenciesService.isValidCurrency("EUR");
        StepVerifier.create(isValid)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void shouldThrowExceptionWhenFetchingSupportedCurrenciesFails() {
        when(exchangeRateProvider.getSupportedCurrencies()).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> supportedCurrenciesService.getCurrencies());
    }

    @Test
    public void shouldThrowExceptionWhenFetchingSupportedCurrenciesFailsDuringValidation() {
        when(exchangeRateProvider.getSupportedCurrencies()).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> supportedCurrenciesService.isValidCurrency("EUR"));
    }

    @Test
    public void shouldReturnSupportedCurrencies() {
        when(exchangeRateProvider.getSupportedCurrencies()).thenReturn(Mono.just(Arrays.asList("EUR", "USD")));
        StepVerifier.create(supportedCurrenciesService.getCurrencies())
                .expectNext(Arrays.asList("EUR", "USD"))
                .verifyComplete();
    }
}
