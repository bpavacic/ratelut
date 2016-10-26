package com.ratelut.apiserver.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Common utility functions.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class Utils {
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    /**
     * Fetches resource from Internet.
     *
     * Uses GET method to request a resource and returns its contents as a {@link String}.
     */
    public static String loadUrl(String address) throws InternalException, IOException {
        BufferedReader reader;
        try {
            URL url = new URL(address);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout((int) TIMEOUT.toMillis());
            connection.setReadTimeout((int) TIMEOUT.toMillis());
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (MalformedURLException e) {
            throw new InternalException("Invalid URL: " + address, e);
        }
        try {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch (IOException e) {
            throw new InternalException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new InternalException(e);
            }
        }
    }

    /**
     * Converts a local instant to a standard UTC instant with the same
     * local time. This conversion is used after performing a calculation
     * where the calculation was done using a simple local zone.
     *
     * Note: There is no similar function in Java 8. This code is borrowed from Joda library
     * http://joda-time.sourceforge.net/apidocs/src-html/org/joda/time/DateTimeZone.html#line.972
     *
     * @param instantLocal  the local instant to convert to UTC
     * @param strict  whether the conversion should reject non-existent local times
     * @return the UTC instant with the same local time,
     */
    public static long convertLocalToUTC(ZoneId localZone, long instantLocal, boolean strict) {
        // get the offset at instantLocal (first estimate)
        int offsetLocal = getOffset(localZone, instantLocal);
        // adjust instantLocal using the estimate and recalc the offset
        int offset = getOffset(localZone, instantLocal - offsetLocal);
        // if the offsets differ, we must be near a DST boundary
        if (offsetLocal != offset) {
                // if strict then always check if in DST gap
                // otherwise only check if zone in Western hemisphere (as the
                // value of offset is already correct for Eastern hemisphere)
                if (strict || offsetLocal < 0) {
                        // determine if we are in the DST gap
                        long nextLocal = nextTransition(localZone, instantLocal - offsetLocal);
                        if (nextLocal == (instantLocal - offsetLocal)) {
                                nextLocal = Long.MAX_VALUE;
                            }
                        long nextAdjusted = nextTransition(localZone, instantLocal - offset);
                        if (nextAdjusted == (instantLocal - offset)) {
                                nextAdjusted = Long.MAX_VALUE;
                            }
                        if (nextLocal != nextAdjusted) {
                                // yes we are in the DST gap
                                if (strict) {
                                        // DST gap is not acceptable
                                        throw new IllegalArgumentException(
                                                "Invalid instant " + instantLocal);
                                    } else {
                                        // DST gap is acceptable, but for the Western hemisphere
                                        // the offset is wrong and will result in local times
                                        // before the cutover so use the offsetLocal instead
                                        offset = offsetLocal;
                                    }
                            }
                    }
            }
        // check for overflow
        long instantUTC = instantLocal - offset;
        // If there is a sign change, but the two values have different signs...
        if ((instantLocal ^ instantUTC) < 0 && (instantLocal ^ offset) < 0) {
                throw new ArithmeticException("Subtracting time zone offset caused overflow");
            }
        return instantUTC;
    }

    private static long nextTransition(ZoneId localZone, long instantLocalMillis) {
        return localZone.getRules().nextTransition(
                Instant.ofEpochMilli(instantLocalMillis)).toEpochSecond() * 1000L;
    }

    private static int getOffset(ZoneId localZone, long instantLocalMillis) {
        return localZone.getRules().getOffset(
                Instant.ofEpochMilli(instantLocalMillis)).getTotalSeconds() * 1000;
    }
}
