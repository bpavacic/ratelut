package com.ratelut.apiserver;

import com.google.common.collect.ImmutableMap;

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
public class Server {
    @GET
    @Path("sum/{num1}/{num2}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sum(@PathParam("num1") double num1, @PathParam("num2") double num2) {
        Map<String, Double> output = ImmutableMap.of(
                "num1", num1,
                "num2", num2,
                "sum", num1 + num2);
        return Response.ok(output).build();
    }
}
