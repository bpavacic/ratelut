package com.ratelut.apiserver.common;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/**
 * Helper class to fetch exchange rates from Revolut servers and parse them into
 * {@link ExchangeRate} objects.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class RevolutRateFetcher {
    private static final String REVOLUT_QUOTE_URL_FORMAT =
            "https://revolut.com/api/quote/internal/%s%s";

    private static final ObjectMapper OBJECT_MAPPER;
    private static final JsonFactory JSON_FACTORY;
    
    static {
        OBJECT_MAPPER = new ObjectMapper();
        JSON_FACTORY = new JsonFactory(OBJECT_MAPPER);
    }

    public static Optional<ExchangeRate> fetchExchangeRate(CurrencyPair pair) {
        try {
            String url = String.format(REVOLUT_QUOTE_URL_FORMAT, pair.getFirst(), pair.getSecond());
            String contents = Utils.loadUrl(url);
            ExchangeRate rate = RevolutRateFetcher.parseExchangeRate(contents);
            return Optional.of(rate);
        } catch (IOException | InternalException e) {
            System.out.println("Error fetching Revolut rate for " + pair + ", " + e.getMessage());
            return Optional.empty();
        }
    }

    static ExchangeRate parseExchangeRate(String contents) throws IOException {
        RevolutResponseObject read = JSON_FACTORY.createJsonParser(contents)
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
