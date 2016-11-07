package com.ratelut.apiserver.updater;

import com.google.common.collect.Iterables;
import com.ratelut.apiserver.common.*;
import com.ratelut.apiserver.storage.Storage;
import com.ratelut.apiserver.storage.StorageException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Checks if any of exchange rates need to be updated.
 *
 * Currently contains dummy implementation.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class UpdateRatesJob implements Runnable {
    private static final Duration ONE_WEEK = Duration.ofDays(7);
    private static final Duration REVOLUT_RATE_TTL = Duration.ofMinutes(10);

    @Inject Storage storage;

    @Override
    public void run() {
        List<CurrencyPair> allPairs = RevolutUtils.getAllRevolutCurrencyPairs();
        for (CurrencyPair pair : allPairs) {
            updateExchangeRatesIfNecessary(pair);
        }
    }

    private void updateExchangeRatesIfNecessary(CurrencyPair pair) {
        try {
            // Get lastest exchange rate we have stored.
            ExchangeRate oldRate = getLatestStoredRevolutExchangeRate(pair);
            if (oldRate == null || Duration.between(oldRate.getTimestamp(), Instant.now())
                    .compareTo(REVOLUT_RATE_TTL) > 0) {
                // Needs an update.
                Optional<ExchangeRate> newRate = RevolutRateFetcher.fetchExchangeRate(pair);
                if (newRate.isPresent()) {
                    storage.saveExchangeRate(newRate.get());
                }
                Optional<ExchangeRate> inverseRate = RevolutRateFetcher.fetchExchangeRate(
                        pair.invert());
                if (inverseRate.isPresent()) {
                    storage.saveExchangeRate(inverseRate.get());
                }
                Optional<ExchangeRate> bloombergRage = BloombergRateFetcher.fetchExchangeRate(pair);
                if (bloombergRage.isPresent()) {
                    storage.saveExchangeRate(bloombergRage.get());
                }
            }
        } catch (Throwable e) {
            // Swallow errors.
            System.out.println(String.format("Unable to update exchange rates for %s: %s",
                    pair, e.getMessage()));
        }
    }

    @Nullable
    private ExchangeRate getLatestStoredRevolutExchangeRate(CurrencyPair pair)
            throws StorageException {
        Instant now = Instant.now();
        Iterable<ExchangeRate> rates = storage.getExchangeRates(
                new Interval(now.minus(ONE_WEEK), now),
                Optional.of(ExchangeRateProvider.REVOLUT),
                Optional.of(pair));
        return Iterables.getLast(rates, null);
    }
}
