package com.ratelut.apiserver.common;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * Defines all supported currencies.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public enum CurrencyCode {
    // Base currencies.
    GBP(true),
    EUR(true),
    USD(true),

    // Spend-only currencies.
    AED, ALL, ARS, AUD, AWG, AZN, BBD, BGN, BND, BOB, BRL, BWP, BYR, BZD, CAD, CHF, CLP, CNY, COP,
    CRC, CZK, DKK, DOP, EGP, HKD, HNL, HRK, HUF, IDR, ILS, INR, ISK, JMD, JPY, KGS, KHR, KRW, KZT,
    LAK, LKR, MAD, MGA, MKD, MMK, MNT, MUR, MVM, MXN, MYR, MZN, NAD, NGN, NIO, NOK, NPR, NZD, OMR,
    PAB, PEN, PHP, PKR, PLN, PYG, QAR, RON, RSD, RUB, SAR, SBD, SCR, SEK, SGD, SOS, SRD, SVC, SYP,
    THB, TRY, TTD, TVD, TWD, UAH, UGX, UYU, UZS, VEF, VND, XCD, YER, ZAR;

    private final boolean isBaseCurrency;

    CurrencyCode() {
        this(false);
    }

    CurrencyCode(boolean isBaseCurrency) {
        this.isBaseCurrency = isBaseCurrency;
    }

    public boolean isBaseCurrency() {
        return isBaseCurrency;
    }

    public static List<CurrencyCode> getAllBaseCurrencies() {
        return Lists.newArrayList(Iterables.filter(getAllCurrencies(),
                input -> input.isBaseCurrency()));
    }

    public static List<CurrencyCode> getAllCurrencies() {
        return Arrays.asList(CurrencyCode.values());
    }
}
