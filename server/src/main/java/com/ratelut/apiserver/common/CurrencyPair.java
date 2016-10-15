package com.ratelut.apiserver.common;

import java.util.Objects;

/**
 * Defines immutable currency pair.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class CurrencyPair {
    private final CurrencyCode first;
    private final CurrencyCode second;

    public static CurrencyPair of(CurrencyCode first, CurrencyCode second) {
        return new CurrencyPair(first, second);
    }

    public CurrencyCode getFirst() {
        return first;
    }

    public CurrencyCode getSecond() {
        return second;
    }

    private CurrencyPair(CurrencyCode first, CurrencyCode second) {
        this.first = first;
        this.second = second;
    }

    public CurrencyPair invert() {
        return CurrencyPair.of(getSecond(), getFirst());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyPair that = (CurrencyPair) o;

        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
