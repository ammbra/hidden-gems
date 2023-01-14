
package org.acme.entertainment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

import io.helidon.media.jsonb.JsonbSupport;
import io.helidon.common.http.Http;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientResponse;
import io.helidon.webserver.WebServer;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class MainTest {


    private static WebServer webServer;
    private static WebClient webClient;

    @BeforeAll
    static void startTheServer() {
        webServer = Main.startServer().await();

        webClient = WebClient.builder()
                .baseUri("http://localhost:" + webServer.port())
                .addMediaSupport(JsonbSupport.create())
                .build();
    }

    @AfterAll
    static void stopServer() throws ExecutionException, InterruptedException, TimeoutException {
        if (webServer != null) {
            webServer.shutdown()
                    .toCompletableFuture()
                    .get(10, TimeUnit.SECONDS);
        }
    }


    @Test
    void testMicroprofileMetrics() {
        String get = webClient.get()
                .path("/api/data")
                .request(String.class)
                .await();

        assertThat(get, notNullValue());

        String openMetricsOutput = webClient.get()
                .path("/metrics")
                .request(String.class)
                .await();

        assertThat("Metrics output", openMetricsOutput, notNullValue());
    }

    @Test
    void testMetrics() {
        WebClientResponse response = webClient.get()
                .path("/metrics")
                .request()
                .await();
        assertThat(response.status().code(), is(200));
    }

    @Test
    void testHealth() {
        WebClientResponse response = webClient.get()
                .path("health")
                .request()
                .await();
        assertThat(response.status().code(), is(200));
    }

}
