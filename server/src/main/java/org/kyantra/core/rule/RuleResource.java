package org.kyantra.core.rule;

import io.swagger.annotations.Api;
import org.kyantra.aws.ActionHelper;
import org.kyantra.annotation.Session;
import org.kyantra.common.BaseResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/rule")
@Api(value = "/rule")
public class RuleResource extends BaseResource {

    // Sub resource classes instances don't get the request object so you have to explicitly provide
    // securityContext (sc) and requestContext (request) while bootstrapping
    RuleResource(@Context SecurityContext sc,
                  @Context HttpServletRequest request) {
        super(sc, request);
    }

    @Path("/ddb")
    public DDBRuleResource getDDBRule() {
        return new DDBRuleResource(sc, request);
    }

    @Path("/sns")
    public SnsRuleResource getSNSRule() {
        return new SnsRuleResource(sc, request);
    }

    @Path("/actuator")
    public ActuatorRuleResource getActuatorRule() {
        return new ActuatorRuleResource(sc, request);
    }

    @GET
    @Path("/actions")
    @Session
    @Produces(MediaType.APPLICATION_JSON)
    public String getActionTypes() {
        return gson.toJson(ActionHelper.getInstance().getActionTypes());
    }
}
