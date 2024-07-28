package org.spauk.currencyconverter.controller;

import org.spauk.currencyconverter.exception.InvalidCurrencyCodeException;
import org.spauk.currencyconverter.service.CurrencyConversionService;
import org.spauk.currencyconverter.service.SupportedCurrenciesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CurrencyConversionControllerTest {

    @InjectMocks
    private CurrencyConversionController currencyConversionController;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @Mock
    private SupportedCurrenciesService supportedCurrenciesService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnConversions() {
        when(supportedCurrenciesService.isValidCurrency("EUR")).thenReturn(Mono.just(true));
        when(supportedCurrenciesService.isValidCurrency("USD")).thenReturn(Mono.just(true));
        when(currencyConversionService.convertCurrency(BigDecimal.valueOf(100), "EUR", "USD")).thenReturn(Mono.just(BigDecimal.valueOf(108.56)));

        Mono<BigDecimal> conversion = currencyConversionController.getConversions(BigDecimal.valueOf(100), "EUR", "USD");

        StepVerifier.create(conversion)
                .expectNext(BigDecimal.valueOf(108.56))
                .verifyComplete();
    }

    @Test
    public void shouldThrowExceptionWhenSourceCurrencyIsInvalid() {
        when(supportedCurrenciesService.isValidCurrency("INVALID")).thenReturn(Mono.just(false));
        when(supportedCurrenciesService.isValidCurrency("USD")).thenReturn(Mono.just(true));

        StepVerifier.create(currencyConversionController.getConversions(BigDecimal.valueOf(100), "INVALID", "USD"))
                .expectErrorMatches(throwable -> throwable instanceof InvalidCurrencyCodeException
                        && throwable.getMessage().contains("INVALID"))
                .verify();
    }

    @Test
    public void shouldThrowExceptionWhenTargetCurrencyIsInvalid() {
        when(supportedCurrenciesService.isValidCurrency("EUR")).thenReturn(Mono.just(true));
        when(supportedCurrenciesService.isValidCurrency("INVALID")).thenReturn(Mono.just(false));

        StepVerifier.create(currencyConversionController.getConversions(BigDecimal.valueOf(100), "EUR", "INVALID"))
                .expectErrorMatches(throwable -> throwable instanceof InvalidCurrencyCodeException
                        && throwable.getMessage().contains("INVALID"))
                .verify();
    }

    @Test
    public void shouldReturnExceptionMessageWhenHandlingBadRequestErrors() {
        InvalidCurrencyCodeException exception = new InvalidCurrencyCodeException("message");
        String response = currencyConversionController.handleBadRequest(exception);
        assertEquals("message", response);
    }

    @Test
    public void shouldReturnExceptionMessageWhenHandlingGeneralErrors() {
        RuntimeException exception = new RuntimeException("message");
        String response = currencyConversionController.handleInternalServerError(exception);
        assertEquals("message", response);
    }

    @Test
    public void shouldThrowExceptionWhenCurrencyConversionFails() {
        when(supportedCurrenciesService.isValidCurrency("EUR")).thenReturn(Mono.just(true));
        when(supportedCurrenciesService.isValidCurrency("USD")).thenReturn(Mono.just(true));
        when(currencyConversionService.convertCurrency(BigDecimal.valueOf(100), "EUR", "USD")).thenThrow(new RuntimeException());

        StepVerifier.create(currencyConversionController.getConversions(BigDecimal.valueOf(100), "EUR", "USD"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException)
                .verify();
    }

    @Test
    public void shouldReturnSupportedCurrencies() {
        when(supportedCurrenciesService.getCurrencies()).thenReturn(Mono.just(Arrays.asList("EUR", "USD")));
        StepVerifier.create(currencyConversionController.getSupportedCurrencies())
                .expectNext(Arrays.asList("EUR", "USD"))
                .verifyComplete();
    }

    @Test
    public void shouldThrowExceptionWhenGettingSupportedCurrenciesFails() {
        when(supportedCurrenciesService.getCurrencies()).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> currencyConversionController.getSupportedCurrencies());
    }

}
