package com.prokarma.resources;

import java.io.PrintStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("/filtered")
public class FilteredTextResource {
    
    /* return TEXT representation of the resource referenced */
    @GET
    @Produces(MediaType.TEXT_PLAIN + "; charset=UTF-8")
    @Path("{name}")
    public Response getTextResource(@PathParam("name") final String name) {
        return Response.ok((StreamingOutput) ((output) -> {
            try (final PrintStream ps = new PrintStream(output, false, "UTF-8")) {
                ps.println("GET request on filtered resource named:" + name);
            }
        })).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN + "; charset=UTF-8")
    @Path("{name}")
    public Response echoPostPayloa(final String payload) {
        return Response.ok((StreamingOutput) ((output) -> {
            try (final PrintStream ps = new PrintStream(output, false, "UTF-8")) {
                ps.println(payload);
            }
        })).build();
    }
}
