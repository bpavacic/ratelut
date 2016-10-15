package com.ratelut.apiserver.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Unit test for {@link CurrencyPair}.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class CurrencyPairTest {

    @Test
    public void invert() {
        CurrencyPair pair = CurrencyPair.of(CurrencyCode.EUR, CurrencyCode.BBD);
        CurrencyPair inverted = pair.invert();
        assertEquals(CurrencyCode.BBD, inverted.getFirst());
        assertEquals(CurrencyCode.EUR, inverted.getSecond());
    }

    @Test
    public void testEquals() {
        CurrencyPair pair = CurrencyPair.of(CurrencyCode.EUR, CurrencyCode.USD);
        CurrencyPair pair2 = CurrencyPair.of(CurrencyCode.USD, CurrencyCode.EUR);

        assertNotEquals(pair, pair2);
        assertEquals(pair2, pair.invert());
        assertEquals(pair, pair2.invert());
        assertEquals(pair, pair.invert().invert());
        assertEquals(pair2, pair2.invert().invert());
    }
}