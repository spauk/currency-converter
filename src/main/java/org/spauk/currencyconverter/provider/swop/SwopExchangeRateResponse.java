package org.spauk.currencyconverter.provider.swop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SwopExchangeRateResponse {

    private Double quote;
    public SwopExchangeRateResponse() {}

    public SwopExchangeRateResponse(Double quote) {
        this.quote = quote;
    }
    public Double getQuote() {
        return quote;
    }

    public void setQuote(Double quote) {
        this.quote = quote;
    }
}
