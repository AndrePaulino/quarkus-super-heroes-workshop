package io.apaulino.superheroes.fight.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/villains")
@RegisterRestClient(configKey = "villain-host")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public interface VillainProxy {

    @GET
    @Path("/random")
    Villain findRandomVillain();
}
