package io.apaulino.superheroes.villain;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import lombok.AllArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Tag(name = "Villains")
@AllArgsConstructor
@Path("/api/villains")
@Produces(APPLICATION_JSON + ";charset=UTF-8")
@Consumes(APPLICATION_JSON + ";charset=UTF-8")
public class VillainResource {

    @Inject
    Logger logger;
    @Inject
    VillainService service;


    @GET
    @Operation(summary = "Return a list of all villains registered")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class, type = SchemaType.ARRAY)))
    public RestResponse<List<Villain>> getAllVillains() {
        List<Villain> villains = service.listAllVillains();
        logger.debugf("Total number of villains: %d", villains.size());
        return RestResponse.ok(villains);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Return specific villain for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
    @APIResponse(responseCode = "204", description = "The villain is not found for a given identifier")
    public RestResponse<Villain> getVillain(@RestPath final Long id) {
        Villain villain = service.findVillainById(id);
        if (villain == null) {
            logger.debugf("No villain found with id: %d", id);
            return RestResponse.noContent();
        }
        logger.debug("Found villain " + villain);
        return RestResponse.ok(villain);
    }

    @GET
    @Path("/random")
    @Operation(summary = "Return a random villain")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
    public RestResponse<Villain> getRandomVillain() {
        Villain villain = service.findRandomVillain();
        logger.debugf("Found random villain: %s", villain);
        return RestResponse.ok(villain);
    }

    @POST
    @Operation(summary = "Registers a valid villain")
    @APIResponse(responseCode = "201", description = "The URI of the registered villain", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    public RestResponse<Villain> createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
        villain = service.saveVillain(villain);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.id));
        logger.debugf("New villain created with URI %s", builder.build().toString());
        return RestResponse.created(builder.build());
    }

    @PUT
    @Operation(summary = "Updates a villain for a given identifier")
    @APIResponse(responseCode = "200", description = "The updated villain", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
    public RestResponse<Villain> updateVillain(@Valid Villain villain) {
        villain = service.updateVillain(villain);
        logger.debugf("Villain updated to: %s", villain);
        return RestResponse.ok(villain);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Removes a villain for a given identifier")
    @APIResponse(responseCode = "204")
    public RestResponse<Void> removeVillain(@RestPath Long id) {
        service.removeVillain(id);
        logger.debugf("Villain %d deleted", id);
        return RestResponse.noContent();
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Hello world endpoint")
    public String hello() {
        return "Hello REST Villains";
    }
}
