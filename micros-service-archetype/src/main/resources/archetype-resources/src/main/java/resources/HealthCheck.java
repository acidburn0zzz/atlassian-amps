package ${package}.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Path ("/")
@Controller
public class HealthCheck
{
    private static final Logger log = LoggerFactory.getLogger(HealthCheck.class);

    @GET
    @Path ("healthcheck")
    @Produces (MediaType.TEXT_PLAIN)
    public Response healthcheck()
    {
        return Response.ok().entity("healthcheck").build();
    }
}
