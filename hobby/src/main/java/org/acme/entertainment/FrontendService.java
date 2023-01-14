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

public class FrontendService implements Service {

    String debugHeader;

    private static final String ID = "frontend-hobby-" + UUID.randomUUID()
            .toString().substring(0, 4);

    private final AtomicInteger requestSequence = new AtomicInteger(0);
    private final Data data = new Data();

    private final WebClient webClient;

    FrontendService(Config config) {
        debugHeader = config.get("debug.header").asString().orElse("trace-debug-id");
        AtomicReference<String> backendService = new AtomicReference<>();
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
        if (serverRequest.headers().value(debugHeader).isPresent()) {
            String value = serverRequest.headers().value(debugHeader).get();
            Tracer tracer = serverRequest.tracer();
            SpanContext context = serverRequest.spanContext().get();
            Span.Builder spanBuilder = tracer.spanBuilder(context.traceId()).parent(context)
                    .kind(Span.Kind.SERVER)
                    .tag(Tag.create(debugHeader, value));
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

        RandomHobby hobby;
        System.out.println(webClient.get().path("activity").toString());

        try {
            hobby = webClient.get()
                    .path("activity")
                    .contentType(MediaType.APPLICATION_JSON)
                    .request(RandomHobby.class).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        var text = hobby.getActivity();

        if (request.isUppercase()) {
            hobby.setActivity(text.toUpperCase());
        }

        if (request.isReverse()) {
           hobby.setActivity(new StringBuilder(text).reverse().toString());
        }

        this.data.addRequestId(requestId);
        this.data.putResponse(requestId, hobby);
        this.data.updateWorker(hobby.getWorkerId(), hobby.getCloudId());
        response.status(202).send(this.data);
    }


}
