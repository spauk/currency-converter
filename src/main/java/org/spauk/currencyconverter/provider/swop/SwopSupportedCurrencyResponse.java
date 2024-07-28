package org.spauk.currencyconverter.provider.swop;

public class SwopSupportedCurrencyResponse {

    private String code;

    private Boolean active;

    public SwopSupportedCurrencyResponse() {}

    public SwopSupportedCurrencyResponse(String code, Boolean active) {
        this.code = code;
        this.active = active;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
