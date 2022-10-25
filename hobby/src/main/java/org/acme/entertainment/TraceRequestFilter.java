package org.acme.entertainment;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.IOException;
import java.util.List;

@Provider
public class TraceRequestFilter implements ContainerRequestFilter {

    private final Config config = ConfigProvider.getConfig();

    @ConfigProperty(name = "custom.trace.header", defaultValue = "trace-debug-id")
    private String customTraceHeader;


    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {

        System.err.println("here");

        if (requestContext.getHeaders().containsKey(customTraceHeader)) {
            List<String> debugHeaders = requestContext.getHeaders().get(customTraceHeader);
            Span.fromContext(Context.current()).setAttribute(customTraceHeader, String.valueOf(debugHeaders));
            System.out.println("here2");

        }

        Baggage baggage = Baggage.fromContext(Context.current());
        for (var entry : baggage.asMap().entrySet()) {
            Span.current().setAttribute(entry.getKey(), entry.getValue().getValue());
        }
        Span.current().makeCurrent();
    }
}