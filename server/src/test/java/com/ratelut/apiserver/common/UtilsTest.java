package com.ratelut.apiserver.common;

import org.junit.Test;

import java.time.ZoneId;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Utils}.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class UtilsTest {
    @Test
    public void convertLocalToUTC() throws Exception {
        long retrievedTimestamp = 1477445400000L;
        long convertedToUtc = Utils.convertLocalToUTC(ZoneId.of("America/New_York"),
                retrievedTimestamp, true);
        // Expect 4 hours difference between America/New_York and UTC on October 26, 2016.
        assertEquals(4 * 3600 * 1000L, convertedToUtc - retrievedTimestamp);
    }
}
