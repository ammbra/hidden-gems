package org.acme.entertainment;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class TraceRequestFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "custom.trace.header", defaultValue = "trace-debug-id")
    private String customTraceHeader;

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {

        if (requestContext.getHeaders().containsKey(customTraceHeader)) {
            List<String> debugHeaders = requestContext.getHeaders().get(customTraceHeader);
            Span.fromContext(Context.current()).setAttribute(customTraceHeader, String.valueOf(debugHeaders));
        }

        Baggage baggage = Baggage.fromContext(Context.current());
        for (var entry : baggage.asMap().entrySet()) {
            Span.current().setAttribute(entry.getKey(), entry.getValue().getValue());
        }
        Span.current().makeCurrent();
    }
}