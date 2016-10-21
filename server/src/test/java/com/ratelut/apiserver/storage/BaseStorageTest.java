package com.ratelut.apiserver.storage;

import com.google.common.collect.Lists;
import com.ratelut.apiserver.common.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Base class for testing {@link Storage} implementations.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
abstract class BaseStorageTest {
    private static final Instant MONDAY = Instant.parse("2016-10-17T15:00:00Z");
    private static final Instant TUESDAY = MONDAY.plus(Duration.ofDays(1));
    private static final Instant WEDNESDAY = TUESDAY.plus(Duration.ofDays(1));
    private static final Instant THURSDAY = WEDNESDAY.plus(Duration.ofDays(1));
    private static final Instant FRIDAY = THURSDAY.plus(Duration.ofDays(1));

    private static final Optional<ExchangeRateProvider> ALL_PROVIDERS = Optional.empty();
    private static final Optional<CurrencyPair> ALL_CURRENCY_PAIRS = Optional.empty();

    private static final ExchangeRateProvider P1 = ExchangeRateProvider.BLOOMBERG;
    private static final ExchangeRateProvider P2 = ExchangeRateProvider.REVOLUT;

    private static final CurrencyPair EURUSD = CurrencyPair.of(CurrencyCode.EUR, CurrencyCode.USD);
    private static final CurrencyPair EURGBP = CurrencyPair.of(CurrencyCode.EUR, CurrencyCode.GBP);

    private static final ExchangeRate P1_EURUSD_TUE =
            ExchangeRate.of(P1, EURUSD, TUESDAY, BigDecimal.valueOf(3));
    private static final ExchangeRate P1_EURUSD_THU =
            ExchangeRate.of(P1, EURUSD, THURSDAY, BigDecimal.valueOf(3));
    private static final ExchangeRate P1_EURGBP_TUE =
            ExchangeRate.of(P1, EURGBP, TUESDAY, BigDecimal.valueOf(5));
    private static final ExchangeRate P1_EURGBP_THU =
            ExchangeRate.of(P1, EURGBP, THURSDAY, BigDecimal.valueOf(6));

    private static final ExchangeRate P2_EURUSD_TUE =
            ExchangeRate.of(P2, EURUSD, TUESDAY, BigDecimal.valueOf(3));
    private static final ExchangeRate P2_EURGBP_TUE =
            ExchangeRate.of(P2, EURGBP, TUESDAY, BigDecimal.valueOf(5));

    private Storage storage;

    @Before
    public void setUpStorage() {
        storage = createStorageForTest();
    }

    protected abstract Storage createStorageForTest();

    @Test
    public void simpleStoreAndGet() {
        saveExchangeRates(P1_EURUSD_TUE);
        assertStorageReturns(
                new Interval(MONDAY, FRIDAY), ALL_PROVIDERS, ALL_CURRENCY_PAIRS,
                P1_EURUSD_TUE);
        assertStorageReturns(
                new Interval(MONDAY, TUESDAY), ALL_PROVIDERS, ALL_CURRENCY_PAIRS);
        assertStorageReturns(
                new Interval(TUESDAY, FRIDAY), ALL_PROVIDERS, ALL_CURRENCY_PAIRS,
                P1_EURUSD_TUE);
    }

    @Test
    public void testFilters() {
        saveExchangeRates(P1_EURUSD_TUE, P1_EURUSD_THU, P1_EURGBP_TUE, P1_EURGBP_THU, P2_EURGBP_TUE,
                P2_EURUSD_TUE);
        // Filter on time.
        assertStorageReturns(new Interval(MONDAY, WEDNESDAY), ALL_PROVIDERS, ALL_CURRENCY_PAIRS,
                P1_EURGBP_TUE, P1_EURUSD_TUE, P2_EURGBP_TUE, P2_EURUSD_TUE);
        // Filter on provider.
        assertStorageReturns(new Interval(MONDAY, FRIDAY), Optional.of(P1), ALL_CURRENCY_PAIRS,
                P1_EURGBP_TUE, P1_EURUSD_TUE, P1_EURGBP_THU, P1_EURUSD_THU);
        // Filter on currency pair.
        assertStorageReturns(new Interval(MONDAY, FRIDAY), ALL_PROVIDERS, Optional.of(EURGBP),
                P1_EURGBP_TUE, P2_EURGBP_TUE, P1_EURGBP_THU);
        // Filter on everything.
        assertStorageReturns(new Interval(THURSDAY, FRIDAY), Optional.of(P1), Optional.of(EURGBP),
                P1_EURGBP_THU);
    }

    private void saveExchangeRates(ExchangeRate ... rates) {
        for (ExchangeRate rate : rates) {
            storage.saveExchangeRate(rate);
        }
    }

    private void assertStorageReturns(Interval interval,
                                      Optional<ExchangeRateProvider> providers,
                                      Optional<CurrencyPair> currencyPairs,
                                      ExchangeRate ... expected) {
        List<ExchangeRate> returnedAsList = Lists.newArrayList(
                storage.getExchangeRates(interval, providers, currencyPairs));
        List<ExchangeRate> expectedAsList = Arrays.asList(expected);

        // Compare lists ignoring order.
        assertTrue(returnedAsList.containsAll(expectedAsList));
        assertTrue(expectedAsList.containsAll(returnedAsList));

        // Make sure results are ordered by timestamp.
        Instant lastTime = null;
        for (ExchangeRate rate : returnedAsList) {
            assertTrue(lastTime == null || !rate.getTimestamp().isBefore(lastTime));
            lastTime = rate.getTimestamp();
        }
    }
}
