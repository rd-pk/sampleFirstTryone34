package com.prokarma.resources;

import java.io.PrintStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("/filtered")
public class FilteredHtmlResource {

    /* return HTML representation of the resource referenced */
    @GET
    @Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
    @Path("{name}")
    public Response getResource(@PathParam("name") final String name) {
        return Response.ok((StreamingOutput) ((output) -> {
            try (final PrintStream ps = new PrintStream(output, false, "UTF-8")) {
                ps.print("<html>");
                ps.println("<h1>Hello Dropwizard!</h1>");
                ps.println("<h2>GET request on filtered resource named:" + name + "</h2>");
                ps.println("</html>");
            }
        })).build();
    }
}
