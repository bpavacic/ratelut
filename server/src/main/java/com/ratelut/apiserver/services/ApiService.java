package com.ratelut.apiserver.services;

import com.google.common.collect.ImmutableMap;
import com.ratelut.apiserver.updater.UpdateRatesJob;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;


/**
 * Dummy REST service implementation.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
@Path("/")
public class ApiService {
    @GET
    @Path("sum/{num1}/{num2}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sum(@PathParam("num1") double num1, @PathParam("num2") double num2) {
        System.out.println("Serving HTTP request");

        Map<String, Double> output = ImmutableMap.of(
                "num1", num1,
                "num2", num2,
                "sum", num1 + num2,
                "counter", (double) UpdateRatesJob.counter);
        return Response.ok(output).build();
    }
}