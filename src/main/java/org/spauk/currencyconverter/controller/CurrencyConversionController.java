package org.spauk.currencyconverter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.PositiveOrZero;
import org.spauk.currencyconverter.exception.InvalidCurrencyCodeException;
import org.spauk.currencyconverter.service.CurrencyConversionService;
import org.spauk.currencyconverter.service.SupportedCurrenciesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
@Tag(name = "Currency conversion", description = "Provides endpoints for currency conversion and supported currency codes")
public class CurrencyConversionController {

    private Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);

    @Autowired
    private CurrencyConversionService currencyConversionService;

    @Autowired
    private SupportedCurrenciesService supportedCurrenciesService;

    @GetMapping("/conversions")
    @Operation(summary = "Convert currency", description = "Converts the given money amount from source currency to the target currency")
    public Mono<BigDecimal> getConversions(
            @RequestParam @PositiveOrZero BigDecimal amount,
            @RequestParam(name = "from") String sourceCurrency,
            @RequestParam(name = "to") String targetCurrency) {
        logger.info("Received currency conversion request: amount {}, sourceCurrency {}, targetCurrency {}", amount, sourceCurrency, targetCurrency);

        return Mono.zip(supportedCurrenciesService.isValidCurrency(sourceCurrency),
                        supportedCurrenciesService.isValidCurrency(targetCurrency))
                .flatMap(validationResult -> {
                    boolean isSourceCurrencyValid = validationResult.getT1();
                    boolean isTargetCurrencyValid = validationResult.getT2();
                    if(!isSourceCurrencyValid) {
                        throw new InvalidCurrencyCodeException("Invalid source currency: " + sourceCurrency);
                    }
                    if(!isTargetCurrencyValid) {
                        throw new InvalidCurrencyCodeException("Invalid target currency: " + targetCurrency);
                    }
                    return currencyConversionService.convertCurrency(amount, sourceCurrency, targetCurrency);
                });
    }

    @GetMapping("/currencies")
    @Operation(summary = "Get supported currencies", description = "Returns a list of supported currency codes")
    public Mono<List<String>> getSupportedCurrencies() {
        logger.info("Received get supported currencies request");
        return supportedCurrenciesService.getCurrencies();
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            InvalidCurrencyCodeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception e) {
        logger.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError(Exception e) {
        logger.error(e.getMessage());
        return e.getMessage();
    }
}
