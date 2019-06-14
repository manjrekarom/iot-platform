package org.kyantra.core.sns;

import org.kyantra.annotation.Session;
import org.kyantra.common.BaseResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/sns")
public class SnsResource extends BaseResource {

    SnsResource(@Context SecurityContext sc,
                  @Context HttpServletRequest request) {
        super(sc, request);
    }

    @GET
    @Path("/get/{id}")
    @Session
    @Produces(MediaType.APPLICATION_JSON)
    public String getSnsBean(@PathParam("id") Integer id) {
        SnsBean snsBean = SnsDAO.getInstance().get(id);
        return gson.toJson(snsBean);
    }
}