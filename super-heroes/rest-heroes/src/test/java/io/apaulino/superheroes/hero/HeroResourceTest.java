package io.apaulino.superheroes.hero;

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
import static org.hamcrest.core.Is.is;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HeroResourceTest {

    private static final String DEFAULT_NAME = "Super Baguette";
    private static final String UPDATED_NAME = "Super Baguette (updated)";
    private static final String DEFAULT_OTHER_NAME = "Super Baguette Tradition";
    private static final String UPDATED_OTHER_NAME = "Super Baguette Tradition (updated)";
    private static final String DEFAULT_PICTURE = "super_baguette.png";
    private static final String UPDATED_PICTURE = "super_baguette_updated.png";
    private static final String DEFAULT_POWERS = "eats baguette really quickly";
    private static final String UPDATED_POWERS = "eats baguette really quickly (updated)";
    private static final int DEFAULT_LEVEL = 42;
    private static final int UPDATED_LEVEL = 43;

    private static final int NB_HEROES = 941;
    private static String heroId;

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
    void testHelloEndpoint() {
        given()
            .header(ACCEPT, TEXT_PLAIN)
            .when()
            .get("/api/heroes/hello")
            .then()
            .statusCode(StatusCode.OK)
            .body(is("Hello REST Heroes"));
    }

    @Test
    void shouldNotGetUnknownHero() {
        given()
            .header(ACCEPT, APPLICATION_JSON)
            .pathParam("id", 1)
            .when()
            .get("/api/heroes/{id}")
            .then()
            .statusCode(StatusCode.NO_CONTENT);
    }

    @Test
    void shouldGetRandomHero() {
        given()
            .header(ACCEPT, APPLICATION_JSON)
            .get("/api/heroes/random")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON);
    }

    @Test
    void shouldNotAddInvalidItem() {
        Hero invalidHero = new Hero();
        invalidHero.name = null;
        invalidHero.otherName = DEFAULT_OTHER_NAME;
        invalidHero.picture = DEFAULT_PICTURE;
        invalidHero.powers = DEFAULT_POWERS;
        invalidHero.level = 0;

        given()
            .header(ACCEPT, APPLICATION_JSON)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body(invalidHero)
            .when()
            .post("/api/heroes")
            .then()
            .statusCode(StatusCode.BAD_REQUEST);
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Hero> heroes = get("/api/heroes")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .as(getListHeroTypeRef());

        assertEquals(NB_HEROES, heroes.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Hero validHero = new Hero();
        validHero.name = DEFAULT_NAME;
        validHero.otherName = DEFAULT_OTHER_NAME;
        validHero.picture = DEFAULT_PICTURE;
        validHero.powers = DEFAULT_POWERS;
        validHero.level = DEFAULT_LEVEL;

        String location = given()
            .header(ACCEPT, APPLICATION_JSON)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body(validHero)
            .when()
            .post("/api/heroes")
            .then()
            .statusCode(StatusCode.CREATED)
            .extract()
            .header("Location");

        assertTrue(location.contains("/api/heroes"));

        // Stores the id
        String[] segments = location.split("/");
        heroId = segments[segments.length - 1];
        assertNotNull(heroId);

        Hero addedHero = given()
            .header(ACCEPT, APPLICATION_JSON)
            .pathParam("id", heroId)
            .when()
            .get("/api/heroes/{id}")
            .then()
            .statusCode(StatusCode.OK)
            .extract()
            .as(Hero.class);
        assertEquals(validHero, addedHero);

        List<Hero> heroes = get("/api/heroes")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .as(getListHeroTypeRef());

        assertEquals(NB_HEROES + 1, heroes.size());
    }

    @Test
    @Order(3)
    void shouldUpdateAnItem() {
        Hero hero = new Hero();
        hero.id = Long.valueOf(heroId);
        hero.name = UPDATED_NAME;
        hero.otherName = UPDATED_OTHER_NAME;
        hero.picture = UPDATED_PICTURE;
        hero.powers = UPDATED_POWERS;
        hero.level = UPDATED_LEVEL;

        Hero updatedHero = given()
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .body(hero)
            .when()
            .put("/api/heroes")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .as(Hero.class);
        assertEquals(hero, updatedHero);

        List<Hero> heroes = get("/api/heroes")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .as(getListHeroTypeRef());

        assertEquals(NB_HEROES + 1, heroes.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
            .pathParam("id", heroId)
            .when()
            .delete("/api/heroes/{id}")
            .then()
            .statusCode(StatusCode.NO_CONTENT);

        List<Hero> heroes = get("/api/heroes")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .as(getListHeroTypeRef());
        assertEquals(NB_HEROES, heroes.size());
    }

    private TypeRef<List<Hero>> getListHeroTypeRef() {
        return new TypeRef<>() {
        };
    }

}
