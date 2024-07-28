package org.spauk.currencyconverter;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@org.springframework.context.annotation.Configuration
@EnableCaching
@EnableScheduling
public class Configuration {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("exchangeRates", "supportedCurrencies");
    }

}
