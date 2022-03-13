package org.acme.entertainment;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/activity")
@RegisterRestClient
@Singleton
public interface BackendService {

    @GET
    RandomHobby getActivity();
        
}