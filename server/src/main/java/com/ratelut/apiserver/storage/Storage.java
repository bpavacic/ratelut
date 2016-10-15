package com.ratelut.apiserver.storage;

import com.ratelut.apiserver.common.CurrencyPair;
import com.ratelut.apiserver.common.ExchangeRate;
import com.ratelut.apiserver.common.ExchangeRateProvider;
import org.joda.time.Interval;

import javax.annotation.Nullable;

/**
 * Defines storage interface.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public interface Storage {
    void saveExchangeRate(ExchangeRateProvider exchangeRateProvider, ExchangeRate exchangeRate);
    @Nullable ExchangeRate getLatestExchangeRate(ExchangeRateProvider exchangeRateProvider,
            CurrencyPair currencyPair);
    Iterable<ExchangeRate> getExchangeRates(ExchangeRateProvider exchangeRateProvider,
            CurrencyPair currencyPair, Interval timeInterval);
}