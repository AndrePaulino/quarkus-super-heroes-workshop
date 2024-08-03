package io.apaulino.superheroes.fight;

import io.apaulino.superheroes.fight.client.Hero;
import io.apaulino.superheroes.fight.client.Villain;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FightResourceTest {

    private static final String DEFAULT_WINNER_NAME = "Super Baguette";
    private static final String DEFAULT_WINNER_PICTURE = "super_baguette.png";
    private static final int DEFAULT_WINNER_LEVEL = 42;
    private static final String DEFAULT_WINNER_POWERS = "Eats baguette in less than a second";
    private static final String DEFAULT_LOSER_NAME = "Super Chocolatine";
    private static final String DEFAULT_LOSER_PICTURE = "super_chocolatine.png";
    private static final int DEFAULT_LOSER_LEVEL = 6;
    private static final String DEFAULT_LOSER_POWERS = "Transforms chocolatine into pain au chocolat";

    private static final int NB_FIGHTS = 3;
    private static String fightId;

    @Test
    void shouldPingOpenAPI() {
        given()
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .get("/openapi")
            .then()
            .statusCode(StatusCode.OK);
    }

    @Test
    void shouldNotGetUnknownFight() {
        given()
            .pathParam("id", 1)
            .when()
            .get("/api/fights/{id}")
            .then()
            .statusCode(StatusCode.NO_CONTENT);
    }

    @Test
    void shouldNotAddInvalidItem() {
        Fighters fighters = new Fighters();
        fighters.hero = null;
        fighters.villain = null;

        given()
            .body(fighters)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/fights")
            .then()
            .statusCode(StatusCode.BAD_REQUEST);
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Fight> fights = get("/api/fights")
            .then()
            .statusCode(StatusCode.OK)
            .extract()
            .body()
            .as(getFightTypeRef());
        assertEquals(NB_FIGHTS, fights.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Hero hero = new Hero();
        hero.name = DEFAULT_WINNER_NAME;
        hero.picture = DEFAULT_WINNER_PICTURE;
        hero.level = DEFAULT_WINNER_LEVEL;
        hero.powers = DEFAULT_WINNER_POWERS;
        Villain villain = new Villain();
        villain.name = DEFAULT_LOSER_NAME;
        villain.picture = DEFAULT_LOSER_PICTURE;
        villain.level = DEFAULT_LOSER_LEVEL;
        villain.powers = DEFAULT_LOSER_POWERS;
        Fighters fighters = new Fighters();
        fighters.hero = hero;
        fighters.villain = villain;

        fightId = given()
            .body(fighters)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/fights")
            .then()
            .statusCode(StatusCode.OK)
            .body(containsString("winner"), containsString("loser"))
            .extract()
            .body()
            .jsonPath()
            .getString("id");

        assertNotNull(fightId);

        given()
            .pathParam("id", fightId)
            .when().get("/api/fights/{id}")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .body("winnerName", is(DEFAULT_WINNER_NAME))
            .body("winnerPicture", is(DEFAULT_WINNER_PICTURE))
            .body("winnerLevel", is(DEFAULT_WINNER_LEVEL))
            .body("winnerPowers", is(DEFAULT_WINNER_POWERS))
            .body("loserName", is(DEFAULT_LOSER_NAME))
            .body("loserPicture", is(DEFAULT_LOSER_PICTURE))
            .body("loserLevel", is(DEFAULT_LOSER_LEVEL))
            .body("loserPowers", is(DEFAULT_LOSER_POWERS))
            .body("fightDate", is(notNullValue()));

        List<Fight> fights = get("/api/fights")
            .then()
            .statusCode(StatusCode.OK)
            .extract()
            .body()
            .as(getFightTypeRef());
        assertEquals(NB_FIGHTS + 1, fights.size());
    }

    @Test
    void testHelloEndpoint() {
        given()
            .header(ACCEPT, TEXT_PLAIN)
            .when()
            .get("/api/fights/hello")
            .then()
            .statusCode(StatusCode.OK)
            .body(is("Hello REST Fights"));
    }

    private TypeRef<List<Fight>> getFightTypeRef() {
        return new TypeRef<>() {
        };
    }

}
