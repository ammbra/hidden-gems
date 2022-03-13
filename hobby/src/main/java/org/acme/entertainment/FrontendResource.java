package org.acme.entertainment;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import io.opentelemetry.api.trace.Tracer;
import javax.ws.rs.core.Context;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api")
public class FrontendResource {

    private static final String ID = "frontend-quarkus-" + UUID.randomUUID()
                                                .toString().substring(0, 4);

    private final AtomicInteger requestSequence = new AtomicInteger(0);
    private final Data data = new Data();

    @Inject
    Tracer tracer;

    @RestClient
    @Inject
    BackendService backendService;

    @POST
    @Path("/send-request")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendRequest(Request request) {

        final String requestId = ID + "/" + requestSequence.incrementAndGet();
        final RandomHobby hobby = this.backendService.getActivity();

        this.data.addRequestId(requestId);
        this.data.putResponse(requestId, hobby);
        this.data.updateWorker(hobby.getWorkerId(), hobby.getCloudId());

        return Response.status(202).entity(this.data).build();
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Data getResponse() {
        return data;
    }

}