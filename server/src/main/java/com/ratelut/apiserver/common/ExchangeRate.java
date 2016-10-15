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
    private final Instant when;
    private final BigDecimal rate;

    public static ExchangeRate of(CurrencyPair currencyPair, Instant when, BigDecimal rate) {
        return new ExchangeRate(currencyPair, when, rate);
    }

    public ExchangeRate invert() {
        return ExchangeRate.of(currencyPair.invert(), when,
                BigDecimal.ONE.divide(rate, BigDecimal.ROUND_HALF_UP));
    }

    private ExchangeRate(CurrencyPair currencyPair, Instant when, BigDecimal rate) {
        this.currencyPair = currencyPair;
        this.when = when;
        this.rate = rate;
    }
}
