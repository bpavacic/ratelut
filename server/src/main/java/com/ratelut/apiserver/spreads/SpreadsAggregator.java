package com.ratelut.apiserver.spreads;

import com.ratelut.apiserver.common.CurrencyCode;
import com.ratelut.apiserver.common.CurrencyPair;
import com.ratelut.apiserver.common.ExchangeRate;
import com.ratelut.apiserver.common.Interval;
import com.ratelut.apiserver.storage.Storage;
import com.ratelut.apiserver.storage.StorageException;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Retrieves most recent exchange rates from {@link Storage} for specific base currency.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class SpreadsAggregator {
    private static final Duration MAX_AGE = Duration.ofDays(7);
    @Inject Storage storage;

    public Map<CurrencyPair, LatestExchangeRates> calculateCurrentSpreads(CurrencyCode baseCurrency)
            throws StorageException {
        Map<CurrencyPair, LatestExchangeRates> currencyMap = new HashMap<>();
        Instant currentTime = Instant.now();
        Iterable<ExchangeRate> rates = storage.getExchangeRates(
                new Interval(currentTime.minus(MAX_AGE), currentTime),
                Optional.empty(), Optional.empty());
        for (ExchangeRate rate : rates) {
            if (rate.getCurrencyPair().getFirst().equals(baseCurrency)) {
                getOrCreateMapEntry(currencyMap, rate.getCurrencyPair()).feedRate(rate);
            } else if (rate.getCurrencyPair().getSecond().equals(baseCurrency)) {
                getOrCreateMapEntry(currencyMap, rate.getCurrencyPair().invert())
                        .feedInverseRate(rate);
            }
        }
        return currencyMap;
    }

    private LatestExchangeRates getOrCreateMapEntry(Map<CurrencyPair,
            LatestExchangeRates> currencyMap, CurrencyPair currencyPair) {
        if (!currencyMap.containsKey(currencyPair)) {
            currencyMap.put(currencyPair, new LatestExchangeRates());
        }
        return currencyMap.get(currencyPair);
    }
}
