package org.acme.entertainment;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class HobbyResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/data")
          .then()
             .statusCode(200);
    }

}