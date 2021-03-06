package com.ratelut.apiserver.resources;

import com.google.common.base.Preconditions;
import com.ratelut.apiserver.common.CurrencyCode;
import com.ratelut.apiserver.common.CurrencyPair;
import com.ratelut.apiserver.spreads.LatestExchangeRates;
import com.ratelut.apiserver.spreads.SpreadsAggregator;
import com.ratelut.apiserver.storage.StorageException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates snapshots of current exchange rate spreads.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
@Path("/api/snapshot")
public class Snapshot {
    @Inject SpreadsAggregator aggregator;

    @GET
    @Path("/{base}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rateSnapshot(@PathParam("base") String baseCurrency) throws StorageException {
        Preconditions.checkNotNull(aggregator);

        Map<CurrencyPair, LatestExchangeRates> spreads = aggregator.calculateCurrentSpreads(
                CurrencyCode.valueOf(baseCurrency));

        // Convert all LatestExchangeRates objects to RateInfo.
        Map<CurrencyPair, RateInfo> converted = spreads.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> RateInfo.from(e.getValue())));
        return Response.ok(converted).build();
    }
}
