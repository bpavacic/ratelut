package com.ratelut.apiserver.resources;

import org.codehaus.jackson.annotate.JsonProperty;
import com.ratelut.apiserver.spreads.LatestExchangeRates;

import java.math.BigDecimal;

/**
 * Data structure used to output currency pair exchange rate information.
 *
 * TODO(bobo): Replace with a proto?
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class RateInfo {
    @JsonProperty("revolut_rate")
    public final BigDecimal revolutRate;

    @JsonProperty("revolut_rate_ts")
    public final Long revolutRateTimestamp;

    @JsonProperty("revolut_inverse_rate")
    public final BigDecimal revolutInverseRate;

    @JsonProperty("revolut_inverse_rate_ts")
    public final Long revolutInverseRateTimestamp;

    @JsonProperty("bloomberg_rate")
    public final BigDecimal bloombergRate;

    @JsonProperty("bloomberg_rate_ts")
    public final Long bloombergRateTimestamp;

    @JsonProperty("implied_revolut_spread_percentage")
    public final BigDecimal impliedRevolutSpreadPercentage;

    private RateInfo(LatestExchangeRates latestExchangeRates) {
        if (latestExchangeRates.revolutExchangeRate != null) {
            this.revolutRate = latestExchangeRates.revolutExchangeRate.getExchangeRate();
            this.revolutRateTimestamp = latestExchangeRates.revolutExchangeRate.getTimestamp()
                    .toEpochMilli();
        } else {
            this.revolutRate = null;
            this.revolutRateTimestamp = null;
        }

        if (latestExchangeRates.revolutInverseExchangeRate != null) {
            this.revolutInverseRate =
                    latestExchangeRates.revolutInverseExchangeRate.getExchangeRate();
            this.revolutInverseRateTimestamp =
                    latestExchangeRates.revolutInverseExchangeRate.getTimestamp().toEpochMilli();
        } else {
            this.revolutInverseRate = null;
            this.revolutInverseRateTimestamp = null;
        }

        if (latestExchangeRates.bloombergExchangeRate != null) {
            this.bloombergRate = latestExchangeRates.bloombergExchangeRate.getExchangeRate();
            this.bloombergRateTimestamp = latestExchangeRates.bloombergExchangeRate.getTimestamp()
                    .toEpochMilli();
        } else {
            this.bloombergRate = null;
            this.bloombergRateTimestamp = null;
        }

        if (revolutRate != null && revolutInverseRate != null) {
            impliedRevolutSpreadPercentage = revolutInverseRate
                    .divide(revolutRate, 8, BigDecimal.ROUND_HALF_UP)
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100));
        } else {
            impliedRevolutSpreadPercentage = null;
        }
    }

    public static RateInfo from(LatestExchangeRates latestExchangeRates) {
        return new RateInfo(latestExchangeRates);
    }
}
