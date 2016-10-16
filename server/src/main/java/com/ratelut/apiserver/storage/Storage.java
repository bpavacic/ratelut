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
    /** Stores new exchange rate into the storage. */
    void saveExchangeRate(ExchangeRateProvider exchangeRateProvider, ExchangeRate exchangeRate);
    /**
     * Gets the latest exchange rate for given currency pair and exchange rate provider.
     *
     * @return null if there is no data yet.
     */
    @Nullable
    ExchangeRate getLatestExchangeRate(ExchangeRateProvider exchangeRateProvider,
                                       CurrencyPair currencyPair);

    /**
     * Gets all exchange rates for certain currency pair, interval, and exchange rate provider.
     *
     * @return all currency rates for the interval, sorted by timestamp (ascending). If no exchange
     *         rates are found, returns an empty list.
     */
    Iterable<ExchangeRate> getExchangeRates(ExchangeRateProvider exchangeRateProvider,
            CurrencyPair currencyPair, Interval timeInterval);
}
