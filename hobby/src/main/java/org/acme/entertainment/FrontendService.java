package org.acme.entertainment;

import io.helidon.common.http.MediaType;
import io.helidon.config.Config;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.tracing.Span;
import io.helidon.tracing.SpanContext;
import io.helidon.tracing.Tag;
import io.helidon.tracing.Tracer;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class FrontendService implements Service {
    private final AtomicReference<String> backendService = new AtomicReference<>();

    private static final Logger LOGGER = Logger.getLogger(FrontendService.class.getName());

    private static final String ID = "frontend-helidon-" + UUID.randomUUID()
            .toString().substring(0, 4);

    private final AtomicInteger requestSequence = new AtomicInteger(0);
    private final Data data = new Data();

    private final WebClient webClient;

    FrontendService(Config config) {
        backendService.set(config.get("backend.url").asString().orElse("http://localhost:8083"));
        webClient = WebClient.builder()
                .baseUri(backendService.get())
                .addReader(JacksonSupport.reader())
                .addWriter(JacksonSupport.writer())
                .build();

    }


    @Override
    public void update(Routing.Rules rules) {
        rules.any(this::workWithHeaders);
        rules.get("/data", this::getResponse);
        rules.post("/send-request", Handler.create(Request.class, this::sendRequest));
    }

    private void workWithHeaders(ServerRequest serverRequest, ServerResponse serverResponse) {
        if (serverRequest.headers().value("trace-debug-id").isPresent()) {
            String debugHeader = serverRequest.headers().value("trace-debug-id").get();
            Tracer tracer = serverRequest.tracer();
            SpanContext context = serverRequest.spanContext().get();
            Span.Builder spanBuilder = tracer.spanBuilder(context.traceId()).parent(context)
                    .kind(Span.Kind.SERVER)
                    .tag(Tag.create("trace-debug-id", debugHeader));
            spanBuilder.start().end();
        }
        serverRequest.next();

    }

    /**
     * Return a worldly greeting message.
     *
     * @param request  the server request
     * @param response the server response
     */
    private void getResponse(ServerRequest request, ServerResponse response) {
        response.send(data);
    }

    private void sendRequest(ServerRequest serverRequest,
                             ServerResponse response,
                             Request request) {
        final String requestId = ID + "/" + requestSequence.incrementAndGet();

        RandomHobby hobby = null;
        try {
            hobby = webClient.get()
                    .path("activity")
                    .contentType(MediaType.APPLICATION_JSON)
                    .request(RandomHobby.class).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        this.data.addRequestId(requestId);
        this.data.putResponse(requestId, hobby);
        this.data.updateWorker(hobby.getWorkerId(), hobby.getCloudId());
        response.status(202).send(this.data);
    }


}
