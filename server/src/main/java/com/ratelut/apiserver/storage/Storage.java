package com.ratelut.apiserver.storage;

import com.ratelut.apiserver.common.CurrencyPair;
import com.ratelut.apiserver.common.ExchangeRate;
import com.ratelut.apiserver.common.ExchangeRateProvider;
import com.ratelut.apiserver.common.Interval;

import java.util.Optional;

/**
 * Defines storage interface.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public interface Storage {
    /**
     * Stores new exchange rate into the storage.
     */
    void saveExchangeRate(ExchangeRate exchangeRate);

    /**
     * Gets all exchange rates for the specified time interval, currency pair, and exchange rate
     * provider.
     *
     * @param timeInterval time interval
     * @param exchangeRateProvider if set, return results only for this exchange rate provider
     * @param currencyPair if set, return only results for this currency pair
     *
     * @return all currency rates found, sorted by timestamp (ascending). If no exchange
     *         rates are found, returns an empty iterator.
     */
    Iterable<ExchangeRate> getExchangeRates(Interval timeInterval,
                                            Optional<ExchangeRateProvider> exchangeRateProvider,
                                            Optional<CurrencyPair> currencyPair);
}
