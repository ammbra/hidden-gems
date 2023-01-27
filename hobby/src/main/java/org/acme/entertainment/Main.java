package org.acme.entertainment;


import io.helidon.common.http.Http;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.media.jsonb.JsonbSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.common.LogConfig;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.tracing.TracerBuilder;
import io.helidon.tracing.jaeger.JaegerTracerBuilder;
import io.helidon.tracing.opentelemetry.HelidonOpenTelemetry;
import io.helidon.tracing.opentelemetry.OpenTelemetryTracerProvider;
import io.opentelemetry.api.OpenTelemetry;
import io.helidon.webserver.Routing;
//import io.helidon.webserver.ServerConfiguration;
//import io.helidon.webserver.Service;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.staticcontent.StaticContentSupport;

import static io.helidon.tracing.opentelemetry.OpenTelemetryTracerProvider.*;


/**
 * The application main class.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     */
    static Single<WebServer> startServer() {

        // load logging configuration
        LogConfig.configureRuntime();

        // By default this will pick up application-dev.yaml from the classpath
        Config config = Config.create();

        WebServer server = WebServer.builder(createRouting(config))
                .config(config.get("host"))
                .port(config.get("server.port").asInt().get())
                .addMediaSupport(JsonbSupport.create())
                .tracer(TracerBuilder.create(config.get("tracing")).serviceName("hobby").build())
                .build();

        Single<WebServer> webserver = server.start();

        // Try to start the server. If successful, print some info and arrange to
        // print a message at shutdown. If unsuccessful, print the exception.
        webserver.thenAccept(ws -> {
            System.out.println("WEB server is up! http://localhost:" + ws.port() );
            ws.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));
        })
        .exceptionallyAccept(t -> {
            System.err.println("Startup failed: " + t.getMessage());
            t.printStackTrace(System.err);
        });

        return webserver;
    }

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with JSON support, a health check, and a service
     * @param config configuration of this server
     */
    private static Routing createRouting(Config config) {
        FrontendService frontendService = new FrontendService(config);

        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks()) // Adds a convenient set of checks
                .build();

        var staticContent = StaticContentSupport.builder("META-INF/resources", ClassLoader.getSystemClassLoader())
                .welcomeFileName("index.html")
                .build();

        Routing.Builder builder = Routing.builder()
                .register(MetricsSupport.create()) // Metrics at "/metrics"
                .register(health)
                .register("/api", frontendService)
                .register(staticContent)
                .register("/{+}", staticContent);


        return builder.build();
    }
}
