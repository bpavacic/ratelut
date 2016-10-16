package com.ratelut.apiserver.common;

import org.joda.time.Instant;

import java.math.BigDecimal;

/**
 * Holds exchange rate for a pair of currencies at a specific time.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class ExchangeRate {
    private final CurrencyPair currencyPair;
    private final Instant timestamp;
    private final BigDecimal rate;

    public static ExchangeRate of(CurrencyPair currencyPair, Instant when, BigDecimal rate) {
        return new ExchangeRate(currencyPair, when, rate);
    }

    public ExchangeRate invert() {
        return ExchangeRate.of(currencyPair.invert(), timestamp,
                BigDecimal.ONE.divide(rate, BigDecimal.ROUND_HALF_UP));
    }

    private ExchangeRate(CurrencyPair currencyPair, Instant timestamp, BigDecimal rate) {
        this.currencyPair = currencyPair;
        this.timestamp = timestamp;
        this.rate = rate;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
