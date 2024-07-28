package org.spauk.currencyconverter.provider.swop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spauk.currencyconverter.provider.ExchangeRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SwopExchangeRateProvider implements ExchangeRateProvider {
    @Autowired
    private WebClient webClient;

    private Logger logger = LoggerFactory.getLogger(SwopExchangeRateProvider.class);

    @Cacheable("exchangeRates")
    public Mono<Double> getExchangeRate(String sourceCurrency, String targetCurrency) {
        logger.info("Fetching exchange rates from:{} to:{}", sourceCurrency, targetCurrency);
        return webClient.get()
                .uri("/rates/{sourceCurrency}/{targetCurrency}", sourceCurrency, targetCurrency)
                .retrieve()
                .bodyToMono(SwopExchangeRateResponse.class)
                .onErrorMap(Throwable.class, t -> new RuntimeException(t.getMessage()))
                .map(SwopExchangeRateResponse::getQuote);
    }

    @Cacheable("supportedCurrencies")
    public Mono<List<String>> getSupportedCurrencies() {
        logger.info("Fetching supported currencies");
        return webClient.get()
                .uri("/currencies")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SwopSupportedCurrencyResponse>>() {})
                .onErrorMap(Throwable.class, t -> new RuntimeException(t.getMessage()))
                .flatMapMany(Flux::fromIterable)
                .filter(SwopSupportedCurrencyResponse::getActive)
                .map(SwopSupportedCurrencyResponse::getCode)
                .collectList();
    }

    @CacheEvict(value = "exchangeRates", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.rates-cache-ttl}")
    public void evictExchangeRatesCache() {
        logger.info("Evicting rates cache");
    }

    @CacheEvict(value = "supportedCurrencies", allEntries = true)
    @Scheduled(fixedRateString = "${caching.spring.currencies-cache-ttl}")
    public void evictSupportedCurrenciesCache() {
        logger.info("Evicting currencies cache");
    }
}
