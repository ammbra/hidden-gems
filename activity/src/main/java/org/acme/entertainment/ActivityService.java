package org.acme.entertainment;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import org.eclipse.microprofile.faulttolerance.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@RegisterRestClient
@Path("/api/activity")
@Singleton
public interface ActivityService {

    @GET
    @CircuitBreaker(failureRatio=0.75, delay = 1000 )
    @Timeout(150)
    @Retry(maxRetries = 4, delay = 100)
    @Fallback(DefaultBasicHobby.class)
    BasicHobby getActivityByType(@QueryParam("type") String type);

    @GET
    RandomHobby getActivity();


    @GET
    @CacheResult(cacheName = "activity")
    BasicHobby getActivityByKey(@CacheKey @QueryParam("key") long key);

    @GET
    AccessibleHobby getActivityByAccessibility(@QueryParam("minaccessibility") double minaccessibility, @QueryParam("maxaccessibility") double maxaccessibility);

    record DefaultBasicHobby() implements FallbackHandler<BasicHobby> {

        @Override
        public BasicHobby handle(ExecutionContext executionContext) {
            return BasicHobby.empty(null, null, null, 0, 0.0);
        }
    }

}
