package io.apaulino.superheroes.villain;

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
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VillainResourceTest {

    private static final String DEFAULT_NAME = "Super Chocolatine";
    private static final String UPDATED_NAME = "Super Chocolatine (updated)";
    private static final String DEFAULT_OTHER_NAME = "Super Chocolatine chocolate in";
    private static final String UPDATED_OTHER_NAME = "Super Chocolatine chocolate in (updated)";
    private static final String DEFAULT_PICTURE = "super_chocolatine.png";
    private static final String UPDATED_PICTURE = "super_chocolatine_updated.png";
    private static final String DEFAULT_POWERS = "does not eat pain au chocolat";
    private static final String UPDATED_POWERS = "does not eat pain au chocolat (updated)";
    private static final int DEFAULT_LEVEL = 42;
    private static final int UPDATED_LEVEL = 43;

    private static final int NB_VILLAINS = 570;
    private static String villainId;

    @Test
    void testHelloEndpoint() {
        given()
            .when()
            .header(ACCEPT, TEXT_PLAIN)
            .get("/api/villains/hello")
            .then()
            .statusCode(StatusCode.OK)
            .body(containsString("Hello"));
    }

    @Test
    void shouldNotGetUnknownVillain() {
        given()
            .pathParam("id", 1L)
            .when()
            .get("/api/villains/{id}")
            .then()
            .statusCode(StatusCode.NO_CONTENT);
    }

    @Test
    void shouldGetRandomVillain() {
        given()
            .when()
            .get("/api/villains/random")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON);
    }

    @Test
    void shouldNotAddInvalidItem() {
        Villain villain = new Villain();
        villain.name = null;
        villain.otherName = DEFAULT_OTHER_NAME;
        villain.picture = DEFAULT_PICTURE;
        villain.powers = DEFAULT_POWERS;
        villain.level = 0;

        given()
            .body(villain)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/villains")
            .then()
            .statusCode(StatusCode.BAD_REQUEST);
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Villain> villains = get("/api/villains")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS, villains.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Villain villain = new Villain();
        villain.name = DEFAULT_NAME;
        villain.otherName = DEFAULT_OTHER_NAME;
        villain.picture = DEFAULT_PICTURE;
        villain.powers = DEFAULT_POWERS;
        villain.level = DEFAULT_LEVEL;

        String location = given()
            .body(villain)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/villains")
            .then()
            .statusCode(StatusCode.CREATED)
            .extract()
            .header("Location");
        assertTrue(location.contains("/api/villains"));

        // Stores the id
        String[] segments = location.split("/");
        villainId = segments[segments.length - 1];
        assertNotNull(villainId);

        given()
            .pathParam("id", villainId)
            .when()
            .get("/api/villains/{id}")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .body("name", is(DEFAULT_NAME))
            .body("otherName", is(DEFAULT_OTHER_NAME))
            .body("level", is(DEFAULT_LEVEL))
            .body("picture", is(DEFAULT_PICTURE))
            .body("powers", is(DEFAULT_POWERS));

        List<Villain> villains = get("/api/villains")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS + 1, villains.size());
    }

    @Test
    @Order(3)
    void testUpdatingAnItem() {
        Villain villain = new Villain();
        villain.id = Long.valueOf(villainId);
        villain.name = UPDATED_NAME;
        villain.otherName = UPDATED_OTHER_NAME;
        villain.picture = UPDATED_PICTURE;
        villain.powers = UPDATED_POWERS;
        villain.level = UPDATED_LEVEL;

        given()
            .body(villain)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .put("/api/villains")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .body("name", is(UPDATED_NAME))
            .body("otherName", is(UPDATED_OTHER_NAME))
            .body("level", is(UPDATED_LEVEL))
            .body("picture", is(UPDATED_PICTURE))
            .body("powers", is(UPDATED_POWERS));

        List<Villain> villains = get("/api/villains")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS + 1, villains.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
            .pathParam("id", villainId)
            .when()
            .delete("/api/villains/{id}")
            .then()
            .statusCode(StatusCode.NO_CONTENT);

        List<Villain> villains = get("/api/villains")
            .then()
            .statusCode(StatusCode.OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS, villains.size());
    }

    private TypeRef<List<Villain>> getVillainTypeRef() {
        return new TypeRef<List<Villain>>() {
        };
    }
}
