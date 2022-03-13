package org.acme.entertainment;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class RandomHobbyResourceTest {

    @Test
    public void testCachedEndpoint() {
        given()
          .when().get("/activity/5881028")
          .then()
             .statusCode(200)
             .body(notNullValue());
    }

    public void testGetHobbyWithBusywork() {
        given()
                .when().header("jaeger-debug-id", "finding-me").get("/activity/type/busywork")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }


//    @Test
//    public void testLimitedCachedEndpoint() {
//        for (int i=0; i<20; i++) {
//            given()
//                    .when().get("/activity/limited/5881028")
//                    .then()
//                    .statusCode(200).header("X-Rate-Limit-Remaining", String.valueOf((20 - i - 1)))
//                    .body(notNullValue());
//        }
//        given()
//                .when().get("/activity/limited/5881028")
//                .then()
//                .statusCode(429).header("X-Rate-Limit-Retry-After-Seconds", notNullValue())
//                .body(notNullValue());
//    }

}