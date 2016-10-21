package com.ratelut.apiserver.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Helper class to parse Revolut repsonses into {@link ExchangeRate} objects.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class RevolutJsonParser {
    private static final ObjectMapper OBJECT_MAPPER;
    private static final JsonFactory JSON_FACTORY;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        JSON_FACTORY = new JsonFactory(OBJECT_MAPPER);
    }

    public static ExchangeRate parseExchangeRate(String contents) throws IOException {
        RevolutResponseObject read = JSON_FACTORY.createParser(contents)
                .readValueAs(RevolutResponseObject.class);
        return read.toExchangeRate();
    }

    // Example response: {"from":"EUR","to":"LKR","rate":156.7522,"timestamp":1477054543000}
    private static class RevolutResponseObject {
        @JsonProperty("from")
        String currencyFrom;
        @JsonProperty("to")
        String currencyTo;
        @JsonProperty("rate")
        BigDecimal rate = BigDecimal.ZERO;
        @JsonProperty("timestamp")
        Long timestampInMillis;

        ExchangeRate toExchangeRate() {
            Preconditions.checkState(timestampInMillis > 0);
            Preconditions.checkState(rate.compareTo(BigDecimal.ZERO) > 0);
            ExchangeRate exchangeRate = ExchangeRate.of(ExchangeRateProvider.REVOLUT,
                    CurrencyPair.of(CurrencyCode.valueOf(currencyFrom),
                            CurrencyCode.valueOf(currencyTo)),
                    Instant.ofEpochMilli(timestampInMillis),
                    rate);
            return exchangeRate;
        }
    }
}
