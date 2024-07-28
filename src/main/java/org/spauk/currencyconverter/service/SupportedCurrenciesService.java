package org.spauk.currencyconverter.service;

import org.spauk.currencyconverter.provider.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SupportedCurrenciesService {

    @Autowired
    private ExchangeRateProvider exchangeRateProvider;

    public Mono<Boolean> isValidCurrency(String currency) {
        return getCurrencies().map(currencies -> currencies.contains(currency));
    }

    public Mono<List<String>> getCurrencies() {
        return exchangeRateProvider.getSupportedCurrencies();
    }
}
