package org.spauk.currencyconverter.controller;

import org.spauk.currencyconverter.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class)
@AutoConfigureWebTestClient
public class CurrencyConversionControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldReturnConversions() {
        webTestClient.get()
                .uri("/conversions?amount=100&from=EUR&to=USD")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(not(""));
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidSourceCurrency() {
        webTestClient.get()
                .uri("/conversions?amount=100&from=INVALID&to=EUR")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(String.class)
                .isEqualTo("Invalid source currency: INVALID");
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidTargetCurrency() {
        webTestClient.get()
                .uri("/conversions?amount=100&from=USD&to=INVALID")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(String.class)
                .isEqualTo("Invalid target currency: INVALID");
    }

    @Test
    public void shouldReturnBadRequstWhenAmountIsNegative() {
        webTestClient.get()
                .uri("/conversions?amount=-100&from=USD&to=EUR")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnListOfSupportedCurrencies() {
        webTestClient.get()
                .uri("/currencies")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(String.class)
                .value(list -> {
                    assert !list.isEmpty();
                });
    }
}