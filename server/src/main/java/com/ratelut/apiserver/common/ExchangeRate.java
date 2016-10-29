package com.ratelut.apiserver.common;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Holds exchange rate for a pair of currencies at a specific time.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class ExchangeRate {
    private final @Nonnull ExchangeRateProvider provider;
    private final @Nonnull CurrencyPair currencyPair;
    private final @Nonnull Instant timestamp;
    private final @Nonnull BigDecimal rate;

    public static ExchangeRate of(ExchangeRateProvider provider, CurrencyPair currencyPair,
            Instant when, BigDecimal rate) {
        return new ExchangeRate(provider, currencyPair, when, rate);
    }

    public ExchangeRate invert() {
        return ExchangeRate.of(provider, currencyPair.invert(), timestamp,
                BigDecimal.ONE.divide(rate, rate.scale(), BigDecimal.ROUND_HALF_UP));
    }

    private ExchangeRate(ExchangeRateProvider provider, CurrencyPair currencyPair,
            Instant timestamp, BigDecimal rate) {
        this.provider = Objects.requireNonNull(provider);
        this.currencyPair = Objects.requireNonNull(currencyPair);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.rate = Objects.requireNonNull(rate);
    }

    public ExchangeRateProvider getProvider() {
        return provider;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BigDecimal getExchangeRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeRate that = (ExchangeRate) o;

        return Objects.equals(provider, that.provider)
                && Objects.equals(currencyPair, that.currencyPair)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, currencyPair, timestamp, rate);
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s %s", timestamp, provider, currencyPair, rate);
    }
}
