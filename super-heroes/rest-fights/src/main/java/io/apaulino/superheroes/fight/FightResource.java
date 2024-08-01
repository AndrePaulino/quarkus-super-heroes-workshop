package io.apaulino.superheroes.fight;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
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

import java.util.List;
import java.util.Optional;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/fights")
@Tag(name = "Fights")
@AllArgsConstructor
@Produces(APPLICATION_JSON + ";charset=UTF-8")
@Consumes(APPLICATION_JSON + ";charset=UTF-8")
public class FightResource {

    @Inject
    Logger logger;
    @Inject
    FightService service;

    @GET
    @Operation(summary = "Return a list of all fights registered")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Fight.class, type = SchemaType.ARRAY)))
    public RestResponse<List<Fight>> getAllFights() {
        List<Fight> fights = service.findAllFights();
        logger.debugf("Total number of fights: %d", fights.size());
        return RestResponse.ok(fights);
    }

    @GET
    @Path("/randomFighters")
    @Operation(summary = "Return random fighters")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Fighters.class, required = true)))
    public RestResponse<Fighters> getRandomFighters() {
        Fighters fighters = service.findRandomFighters();
        logger.debugf("Found random fighters: %s", fighters);
        return RestResponse.ok(fighters);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Return specific fight for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Fight.class)))
    @APIResponse(responseCode = "204", description = "The fight is not found for a given identifier")
    public RestResponse<Fight> getFight(@RestPath final Long id) {
        return Optional.ofNullable(service.findFightById(id))
            .map(fight -> {
                logger.debugf("Found fight: %s", fight);
                return RestResponse.ok(fight);
            })
            .orElseGet(() -> {
                logger.debugf("No fight found with id: %d", id);
                return RestResponse.noContent();
            });
    }

    @POST
    @Operation(summary = "Registers a valid hero")
    @APIResponse(responseCode = "201", description = "The registered fight", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Fight.class)))
    public Fight fight(@Valid Fighters fighters, @Context UriInfo uriInfo) {
        return service.saveFight(fighters);
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Hello world endpoint")
    public String hello() {
        return "Hello REST Fights";
    }
}
