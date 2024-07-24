package io.apaulino.superheroes.villain;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import lombok.AllArgsConstructor;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@AllArgsConstructor
@Path("/api/villains")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VillainResource {

    @Inject
    Logger logger;
    @Inject
    VillainService service;


    @GET
    public RestResponse<List<Villain>> getAllVillains() {
        List<Villain> villains = service.listAllVillains();
        logger.debug("Total number of villains: " + villains.size());
        return RestResponse.ok(villains);
    }

    @GET
    @Path("/{id}")
    public RestResponse<Villain> getVillain(@PathParam("id") Long id) {
        Villain villain = service.findVillainById(id);
        if (villain == null) {
            logger.debug("No villain found with id: " + id);
            return RestResponse.noContent();
        }

        return RestResponse.ok(villain);
    }

    @GET
    @Path("/random")
    public RestResponse<Villain> getRandomVillain() {
        Villain villain = service.findRandomVillain();
        logger.debug("Found random villain: " + villain);
        return RestResponse.ok(villain);
    }

    @POST
    public RestResponse<Villain> createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
        villain = service.saveVillain(villain);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.id));
        logger.debug("New villain created with URI " + builder.build().toString());
        return RestResponse.created(builder.build());
    }

    @PUT
    public RestResponse<Villain> updateVillain(@Valid Villain villain) {
        villain = service.updateVillain(villain);
        logger.debug("Villain updated with new valued " + villain);
        return RestResponse.ok(villain);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<Void> removeVillain(@RestPath Long id) {
        service.removeVillain(id);
        logger.debug("Villain deleted with " + id);
        return RestResponse.noContent();
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello REST Villains";
    }
}
