package org.spauk.currencyconverter.provider;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ExchangeRateProvider {

    Mono<Double> getExchangeRate(String sourceCurrency, String targetCurrency);

    Mono<List<String>> getSupportedCurrencies();
}
