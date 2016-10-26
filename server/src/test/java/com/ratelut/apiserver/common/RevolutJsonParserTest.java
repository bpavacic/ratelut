package com.ratelut.apiserver.common;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.*;

/**
 * Unit test for {@link RevolutRateFetcher}.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class RevolutJsonParserTest {
    @Test
    public void parseInput() throws IOException {
        ExchangeRate parsed = RevolutRateFetcher.parseExchangeRate(
                "{\"from\":\"EUR\",\"to\":\"LKR\",\"rate\":156.7522,\"timestamp\":1477054543000}");
        assertEquals(
                ExchangeRate.of(ExchangeRateProvider.REVOLUT,
                        CurrencyPair.of(CurrencyCode.EUR, CurrencyCode.LKR),
                        Instant.ofEpochMilli(1477054543000L),
                        BigDecimal.valueOf(156.7522)),
                parsed);
    }

    @Test
    public void invalidInput() {
        try {
            RevolutRateFetcher.parseExchangeRate(
                    "{\"from\":\"EUR\",\"to\":\"LKR\",\"timestamp\":1477054543000}");
            throw new IllegalStateException("Should have thrown");
        } catch (Throwable expected) {
        }
    }
}