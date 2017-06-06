package com.ratelut.apiserver.storage;

import com.google.common.collect.Iterables;
import com.ratelut.apiserver.common.CurrencyPair;
import com.ratelut.apiserver.common.ExchangeRate;
import com.ratelut.apiserver.common.ExchangeRateProvider;
import com.ratelut.apiserver.common.Interval;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.*;

/**
 * Storage implementation keeping data in memory.
 *
 * Once the server is shut down, all data is lost. Implemented for testing purposes.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
@Singleton
public class InMemoryStorage implements Storage {
    private static final Comparator<ExchangeRateProvider> PROVIDER_BY_NAME =
            (o1, o2) -> o1.name().compareTo(o2.name());
    private static final Comparator<ExchangeRate> EXCHANGE_RATE_BY_CURRENCY_PAIR =
            (o1, o2) -> o1.getCurrencyPair().toString().compareTo(o2.getCurrencyPair().toString());

    // timestamp -> provider -> currency pair -> exchange rate
    private final SortedMap<Instant, SortedMap<ExchangeRateProvider, SortedSet<ExchangeRate>>>
            ratesMap = new TreeMap<>();

    @Override
    public synchronized void saveExchangeRate(ExchangeRate exchangeRate) {
        if (!ratesMap.containsKey(exchangeRate.getTimestamp())) {
            ratesMap.put(exchangeRate.getTimestamp(), new TreeMap<>(PROVIDER_BY_NAME));
        }
        SortedMap<ExchangeRateProvider, SortedSet<ExchangeRate>> timeEntry =
                ratesMap.get(exchangeRate.getTimestamp());

        if (!timeEntry.containsKey(exchangeRate.getProvider())) {
            timeEntry.put(exchangeRate.getProvider(),
                    new TreeSet<>(EXCHANGE_RATE_BY_CURRENCY_PAIR));
        }
        SortedSet<ExchangeRate> providerSet = timeEntry.get(exchangeRate.getProvider());

        providerSet.add(exchangeRate);
    }

    @Override
    public synchronized Iterable<ExchangeRate> getExchangeRates(
            Interval timeInterval,
            Optional<ExchangeRateProvider> exchangeRateProvider,
            Optional<CurrencyPair> currencyPair) {
        List<ExchangeRate> allRates = new ArrayList<>();
        for (Map.Entry<Instant, SortedMap<ExchangeRateProvider, SortedSet<ExchangeRate>>> entry
                : ratesMap.entrySet()) {
            // Filter by timestamp.
            if (timeInterval.contains(entry.getKey())) {
                for (SortedSet<ExchangeRate> entry2 : entry.getValue().values()) {
                    allRates.addAll(entry2);
                }
            }
        }
        Iterable<ExchangeRate> result = allRates;

        // Filter by provider, if set.
        if (exchangeRateProvider.isPresent()) {
            result = Iterables.filter(result,
                    input -> input.getProvider().equals(exchangeRateProvider.get()));
        }

        // Filter by currency pair, if set.
        if (currencyPair.isPresent()) {
            result = Iterables.filter(result,
                    input -> input.getCurrencyPair().equals(currencyPair.get()));
        }

        return result;
    }
}
