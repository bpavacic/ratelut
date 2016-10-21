package com.ratelut.apiserver.common;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Contains Revolut-related utility methods.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class RevolutUtils {
    private static final ImmutableList<CurrencyPair> ALL_REVOLUT_CURRENCY_PAIRS =
            populateCurrencyPairs();
    private static final String REVOLUT_QUOTE_URL_FORMAT =
            "https://revolut.com/api/quote/internal/%s%s";
    /**
     * Returns a list of all currency pairs we should monitor.
     *
     * The first currency is always one of the base currencies (EUR, GBP, USD), and the second is a
     * spending currency
     *
     */
    public static List<CurrencyPair> getAllCurrencyPairs() {
        return ALL_REVOLUT_CURRENCY_PAIRS;
    }

    private static ImmutableList<CurrencyPair> populateCurrencyPairs() {
        ImmutableList.Builder<CurrencyPair> builder = ImmutableList.builder();
        for (CurrencyCode currency1: CurrencyCode.getAllBaseCurrencies()) {
            for (CurrencyCode currency2 : CurrencyCode.getAllCurrencies()) {
                if (!currency2.equals(currency1)) {
                    builder.add(CurrencyPair.of(currency1, currency2));
                }
            }
        }
        return builder.build();
    }

    public static Optional<ExchangeRate> fetchExchangeRate(CurrencyPair pair) {
        try {
            String contents = Utils.loadUrl(
                    String.format(REVOLUT_QUOTE_URL_FORMAT, pair.getFirst(), pair.getSecond()));
            ExchangeRate rate = RevolutJsonParser.parseExchangeRate(contents);
            return Optional.of(rate);
        } catch (IOException | InternalException e) {
            System.out.println("Error fetching Revolut rate for " + pair);
            return Optional.empty();
        }
    }
}
