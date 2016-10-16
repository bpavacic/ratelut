package com.ratelut.apiserver.storage;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.ratelut.apiserver.common.CurrencyPair;
import com.ratelut.apiserver.common.ExchangeRate;
import com.ratelut.apiserver.common.ExchangeRateProvider;
import org.joda.time.Interval;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Storage implementation keeping all data in memory in memory.
 *
 * Once the server is shut down, all data is lost. Good for testing purposes.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
@Singleton
public class InMemoryStorage implements Storage {
    // provider -> currency pair -> exchange rates
    private final Map<ExchangeRateProvider, Map<CurrencyPair, SortedSet<ExchangeRate>>> map =
            new HashMap<>();

    @Override
    public synchronized void saveExchangeRate(ExchangeRateProvider exchangeRateProvider,
            ExchangeRate exchangeRate) {
        getOrCreateSetFor(exchangeRateProvider, exchangeRate.getCurrencyPair())
                .add(exchangeRate);
    }

    @Nullable
    @Override
    public synchronized ExchangeRate getLatestExchangeRate(
            ExchangeRateProvider exchangeRateProvider, CurrencyPair currencyPair) {
        SortedSet<ExchangeRate> set = getOrCreateSetFor(exchangeRateProvider, currencyPair);
        if (set.isEmpty()) {
            return null;
        } else {
            return set.last();
        }
    }

    @Override
    public synchronized Iterable<ExchangeRate> getExchangeRates(
            ExchangeRateProvider exchangeRateProvider,
            CurrencyPair currencyPair, Interval timeInterval) {
        return Iterables.filter(getOrCreateSetFor(exchangeRateProvider, currencyPair),
                input -> {
                    return timeInterval.contains(input.getTimestamp());
                });
    }

    private SortedSet<ExchangeRate> getOrCreateSetFor(ExchangeRateProvider exchangeRateProvider,
            CurrencyPair currencyPair) {
        if (!map.containsKey(exchangeRateProvider)) {
            map.put(exchangeRateProvider, new HashMap<>());
        }
        final Map<CurrencyPair, SortedSet<ExchangeRate>> exchangeRateProviderMap =
                Preconditions.checkNotNull(map.get(exchangeRateProvider));
        if (!exchangeRateProviderMap.containsKey(currencyPair)) {
            exchangeRateProviderMap.put(currencyPair, new TreeSet<>(
                    (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp())));
        }
        return Preconditions.checkNotNull(exchangeRateProviderMap.get(currencyPair));
    }
}
