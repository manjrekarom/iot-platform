package org.kyantra.resources;

import com.amazonaws.services.simpleemail.model.*;
import org.kyantra.beans.RoleEnum;
import org.kyantra.beans.SnsBean;
import org.kyantra.beans.UnitBean;
import org.kyantra.dao.SnsDAO;
import org.kyantra.dao.UnitDAO;
import org.kyantra.interfaces.Secure;
import org.kyantra.interfaces.Session;
import org.kyantra.utils.AwsIotHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/invite")
public class InviteResource extends BaseResource {
    InviteResource(@Context SecurityContext sc,
                   @Context HttpServletRequest request) {
        super(sc, request);
    }

    @POST
    @Path("/email")
    @Session
    @Secure(roles = {RoleEnum.ALL, RoleEnum.WRITE})
    @Produces(MediaType.APPLICATION_JSON)
    public String sendInvite(@FormParam("email") String email,
                             @FormParam("invitedBy") Integer userId,
                             @FormParam("unitId") Integer unitId,
                             @FormParam("role") RoleEnum role) {
        UnitBean unitBean = UnitDAO.getInstance().get(unitId);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses())
                .withMessage(new Message()
                        .withBody(new Body()
                                .withText(new Content(makeInvitation()))));

        AwsIotHelper.getAmazonSESClient().sendEmail(request);
        return "{\"success\": true}";
    }

    private String makeInvitation(String userName, String ) {
        return
    }
}
