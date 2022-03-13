package org.acme.entertainment;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("activity")
public class RandomHobbyResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomHobbyResource.class);

    @ConfigProperty(name = "worker.cloud.id", defaultValue = "local")
    String cloudId;

    @Inject
    Tracer tracer;

    private final String ID;

    @RestClient
    @Inject
    ActivityService service;


    public RandomHobbyResource() {
        ID = "worker-quarkus-" + UUID.randomUUID()
                .toString().substring(0, 4);
    }


    @GET
    public Response getRandomHobby() throws InterruptedException {
        Response response;
        RandomHobby hobby = service.getActivity();
        hobby.setCloudId(cloudId);
        hobby.setWorkerId(ID);
        Thread.sleep((long)(Math.random() * 1000));
        return   response = Response.status(Response.Status.OK).entity(hobby).build();

    }

    @GET
    @Path("{key}")
    public BasicHobby getHobbyByKey(@PathParam("key") long key) {
        return service.getActivityByKey(key);
    }

    @GET
    @Path("accessibility")
    public AccessibleHobby getHobbyByKey(@QueryParam("min") double min, @QueryParam("max") double max) {
        if (min < 0 || max < 0) {
            return AccessibleHobby.empty(null, null, null, 0, -1);
        }
        return service.getActivityByAccessibility(min, max);
    }


    @GET
    @Path("type/{type}")
    public Response getHobbyByType(@PathParam("type") String type) {
        return switch (type) {
            case "busywork" -> timeout(type);
            case "dyi" -> invokeServiceUnavailable(type);
            default -> Response.status(Response.Status.OK)
                    .entity(service.getActivityByType(type)).build();
        };
    }


    private Response timeout(String type) {
        Response response;
        Span span = tracer.spanBuilder("timeoutspan").startSpan();

        // put the span into the current Context
        try (Scope scope = span.makeCurrent()) {
            LOGGER.debug(String.format("Thread interrupted on %s", cloudId));
                Thread.sleep(2000);
             span.setAttribute("timeout", "exec");
             response = Response.status(Response.Status.GATEWAY_TIMEOUT)
                        .entity(service.getActivityByType(type)).build();
        } catch(Exception t){
                span.setAttribute("alarm", "unexpected");
                span.setStatus(StatusCode.ERROR, "Something wrong happened!");
                span.recordException(t);
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .build();
            } finally {
                span.end(); // Cannot set a span after this call
            }
        return response;

    }

    private Response invokeServiceUnavailable(String type) {
        LOGGER.debug(String.format("Misbehaving %s", cloudId));
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }


}