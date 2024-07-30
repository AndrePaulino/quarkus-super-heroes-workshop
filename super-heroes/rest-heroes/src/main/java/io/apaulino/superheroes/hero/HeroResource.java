package io.apaulino.superheroes.hero;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
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

@Path("/api/heroes")
@Tag(name = "Heroes")
public class HeroResource {

    @Inject
    Logger logger;

    @GET
    @Path("/random")
    @Operation(summary = "Return a random villain")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, required = true)))
    public Uni<RestResponse<Hero>> getRandomHero() {
        return Hero.findRandom()
            .onItem()
            .ifNotNull()
            .transform(RestResponse::ok)
            .onItem()
            .ifNull()
            .continueWith(RestResponse::notFound);
    }

    @GET
    @Operation(summary = "Return a list of all heroes registered")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, type = SchemaType.ARRAY)))
    public Uni<RestResponse<List<Hero>>> getAllHeroes() {
        return Hero.<Hero>listAll()
            .onItem()
            .ifNotNull()
            .transform(RestResponse::ok);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Return specific hero for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class)))
    @APIResponse(responseCode = "204", description = "The hero is not found for a given identifier")
    public Uni<RestResponse<Hero>> getHero(@RestPath final Long id) {
        return Hero.<Hero>findById(id)
            .onItem()
            .ifNotNull()
            .transform(RestResponse::ok)
            .onItem()
            .ifNull()
            .continueWith(() -> {
                logger.debugf("No hero found with id: %d", id);
                return RestResponse.notFound();
            });
    }

    @POST
    @WithTransaction
    @Operation(summary = "Registers a valid hero")
    @APIResponse(responseCode = "201", description = "The URI of the registered hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    public Uni<RestResponse<URI>> createHero(@Valid Hero hero, @Context UriInfo uriInfo) {
        return hero.<Hero>persist()
            .map(persistedHero -> {
                UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(persistedHero.id));
                logger.debugf("New hero created with URI %s", builder.build().toString());
                return RestResponse.created(builder.build());
            });
    }

    @PUT
    @WithTransaction
    @Operation(summary = "Updates a hero for a given identifier")
    @APIResponse(responseCode = "200", description = "The updated hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class)))
    public Uni<RestResponse<Hero>> updateHero(@Valid Hero hero) {
        return Hero.<Hero>findById(hero.id)
            .onItem()
            .ifNotNull()
            .transform(retrieved -> {
                retrieved.copyFrom(hero);
                logger.debugf("Hero updated to: %s", hero);
                return RestResponse.ok(retrieved);
            });
    }

    @DELETE
    @WithTransaction
    @Path("/{id}")
    @Operation(summary = "Removes a hero for a given identifier")
    @APIResponse(responseCode = "204")
    public Uni<RestResponse<Void>> removeHero(@RestPath final Long id) {
        return Hero.deleteById(id)
            .invoke(() -> logger.debugf("Hero %d deleted", id))
            .replaceWith(RestResponse.noContent());
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Hello world endpoint")
    public String hello() {
        return "Hello REST Heroes";
    }
}
