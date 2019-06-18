package org.kyantra.filters;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class HTTPSRedirectFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            System.out.println("Filtering because https was enabled");
            if(requestContext.getUriInfo().getRequestUri().getScheme().equals("http")) {
                System.out.println("Redirecting");
                throw new WebApplicationException(Response.temporaryRedirect(new URI("https://intg.io/")).build());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
