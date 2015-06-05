package ${package}.resources;

import java.lang.management.ManagementFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Path ("/")
@Controller
public class HealthCheck
{
    private static final Logger log = LoggerFactory.getLogger(HealthCheck.class);

    @GET
    @Path ("healthcheck")
    @Produces (MediaType.APPLICATION_JSON)
    public Response healthcheck()
    {
        JsonObject root = new JsonObject();
        root.addProperty("service", "Easy Micros");
        root.addProperty("version", getClass().getPackage().getImplementationVersion());
        root.addProperty("uptime", ManagementFactory.getRuntimeMXBean().getUptime());

        return Response.ok().entity(new Gson().toJson(root)).build();
    }

    @GET
    public Response index()
    {
        return Response.ok("Hello Easy Micros!!!").build();
    }
}
