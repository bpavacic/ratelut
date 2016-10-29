package com.ratelut.apiserver.spreads;

import com.ratelut.apiserver.common.ExchangeRate;
import com.ratelut.apiserver.common.ExchangeRateProvider;

import java.math.BigDecimal;

/**
 * Holds information about most recent currency pair exchange rates.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class LatestExchangeRates {
    public ExchangeRate revolutExchangeRate = null;
    public ExchangeRate revolutInverseExchangeRate = null;
    public ExchangeRate bloombergExchangeRate = null;

    public void feedRate(ExchangeRate rate) {
        if (rate.getProvider() == ExchangeRateProvider.REVOLUT) {
            if (revolutExchangeRate == null || revolutExchangeRate.getTimestamp().isBefore(
                    rate.getTimestamp())) {
                revolutExchangeRate = rate;
            }
        } else if (rate.getProvider() == ExchangeRateProvider.BLOOMBERG) {
            if (bloombergExchangeRate == null || bloombergExchangeRate.getTimestamp().isBefore(
                    rate.getTimestamp())) {
                bloombergExchangeRate = rate;
            }
        } else {
            throw new IllegalArgumentException("Unsupported rate provider in " + rate);
        }
    }

    public void feedInverseRate(ExchangeRate rate) {
        if (rate.getProvider() == ExchangeRateProvider.REVOLUT) {
            if (revolutInverseExchangeRate == null || revolutInverseExchangeRate.getTimestamp()
                    .isBefore(rate.getTimestamp())) {
                revolutInverseExchangeRate = rate.invert();
            }
        } else {
            // Ignore reverse Bloomberg rates.
        }
    }
}
