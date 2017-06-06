package com.ratelut.apiserver.common;


import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fetches exchange rates from Bloomberg servers and converts them into {@link ExchangeRate}
 * objects.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class BloombergRateFetcher {
    private static final String BLOOMBERG_URL_FORMAT =
            "http://www.bloomberg.com/markets/chart/data/1D/%s%s:CUR";

    public static Optional<ExchangeRate> fetchExchangeRate(CurrencyPair pair) {
        String url = String.format(BLOOMBERG_URL_FORMAT, pair.getFirst(), pair.getSecond());

        try {
            String contents = Utils.loadUrl(url);
            // Retrieve all exchange rates contained in Bloomberg response.
            List<ExchangeRate> exchangeRates = BloombergJsonObject.readExchangeRatesFrom(
                    pair, contents);
            // Pick only the newest one.
            // TODO(bobo): Think about always returning and importing all received exchange rates.
            Optional<ExchangeRate> result = Optional.empty();
            for (ExchangeRate exchangeRate : exchangeRates) {
                if (!result.isPresent() ||
                        result.get().getTimestamp().isBefore(exchangeRate.getTimestamp())) {
                    result = Optional.of(exchangeRate);
                }
            }
            return result;
        } catch (IOException | InternalException e) {
            System.out.println("Error fetching Bloomberg rate for " + pair + ", " + e.getMessage());
            return Optional.empty();
        }
    }

    private static class PairDeserializer extends JsonDeserializer<Pair> {
        @Override
        public Pair deserialize(
                JsonParser jsonParser,
                DeserializationContext deserializationContext) throws IOException {
            final String[] strings = jsonParser.readValueAs(String[].class);
            return Pair.of(Long.valueOf(strings[0]), new BigDecimal(strings[1]).setScale(4));
        }
    }

    static class BloombergJsonObject {
        private static final ObjectMapper OBJECT_MAPPER;
        private static final JsonFactory JSON_FACTORY;
        /** Bloomberg returns timestamps in Eastern time zone and we need to convert them to UTC. */
        private static final ZoneId BLOOMBERG_ZONE_ID = ZoneId.of("America/New_York");

        static {
            final SimpleModule module = new SimpleModule("BloombergJsonObjectModule",
                    Version.unknownVersion());
            module.addDeserializer(Pair.class, new PairDeserializer());
            OBJECT_MAPPER = new ObjectMapper();
            OBJECT_MAPPER.registerModule(module);
            JSON_FACTORY = new JsonFactory(OBJECT_MAPPER);
        }

        @JsonProperty("precision")
        public Integer precision;
        @JsonProperty("prev_close")
        public Float prevClose;

        @JsonProperty("data_values")
        public List<Pair<Long, BigDecimal>> dataValues;

        // Ignored fields
        @JsonProperty("exch_open_time")
        public Long exchangeOpenTime;
        @JsonProperty("exch_close_time")
        public Long exchangeCloseTime;
        @JsonProperty("show_1D")
        public Boolean show1D;
        @JsonProperty("selectedPeriod")
        public String selectedPeriod;

        public static List<ExchangeRate> readExchangeRatesFrom(CurrencyPair currencyPair,
                String contents) throws IOException {
            BloombergJsonObject deserialized = JSON_FACTORY.createJsonParser(contents).readValueAs(
                    BloombergJsonObject.class);
            List<ExchangeRate> result = new ArrayList<>();
            for (Pair<Long, BigDecimal> entry : deserialized.dataValues) {
                long timeStampMillis = entry.getFirst();
                long utcTimeStampMillis = Utils.convertLocalToUTC(BLOOMBERG_ZONE_ID,
                        timeStampMillis, true /* strict */);
                Instant instant = Instant.ofEpochMilli(utcTimeStampMillis);
                BigDecimal rate = entry.getSecond();
                result.add(ExchangeRate.of(ExchangeRateProvider.BLOOMBERG,
                        currencyPair, instant, rate));
            }
            return result;
        }
    }
}
