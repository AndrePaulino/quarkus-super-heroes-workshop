package io.apaulino.superheroes.villain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class VillainResourceTest {
    @Test
    void tesVillainsEndpoint() {
        given()
          .when().get("/api/villains")
          .then()
             .statusCode(200)
            .body(containsString("Hello"));
    }
}
