package com.ratelut.apiserver.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;

/**
 * Common utility functions.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class Utils {
    private static final Duration TIMEOUT = Duration.ofMinutes(1);

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
}
