package org.spauk.currencyconverter.service;

import org.spauk.currencyconverter.provider.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class CurrencyConversionService {

    @Autowired
    private ExchangeRateProvider exchangeRateProvider;

    public Mono<BigDecimal> convertCurrency(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        return exchangeRateProvider
                .getExchangeRate(sourceCurrency, targetCurrency)
                .map(exchangeRate -> BigDecimal.valueOf(exchangeRate * amount.doubleValue()));
    }
}
