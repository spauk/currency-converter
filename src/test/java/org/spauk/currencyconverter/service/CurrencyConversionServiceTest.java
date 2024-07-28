package org.spauk.currencyconverter.service;

import org.spauk.currencyconverter.provider.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CurrencyConversionServiceTest {

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldConvertCurrency() {
        when(exchangeRateProvider.getExchangeRate("EUR", "USD")).thenReturn(Mono.just(1.3));
        Mono<BigDecimal> result = currencyConversionService.convertCurrency(BigDecimal.valueOf(100.0), "EUR", "USD");

        StepVerifier.create(result)
                .expectNext(BigDecimal.valueOf(130.0))
                .verifyComplete();
    }

    @Test
    public void shouldThrowExceptionWhenExchangeRateProviderFails() {
        when(exchangeRateProvider.getExchangeRate("EUR", "USD")).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> currencyConversionService.convertCurrency(BigDecimal.valueOf(100), "EUR", "USD"));
    }
}
