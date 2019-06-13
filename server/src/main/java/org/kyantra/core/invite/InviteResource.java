package org.kyantra.core.invite;

import com.amazonaws.services.simpleemail.model.*;
import org.kyantra.common.RoleEnum;
import org.kyantra.core.auth.RightsBean;
import org.kyantra.core.auth.RightsDAO;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.unit.UnitDAO;
import org.kyantra.core.user.UserBean;
import org.kyantra.core.user.UserDAO;
import org.kyantra.exception.DataNotFoundException;
import org.kyantra.exception.ExceptionMessage;
import org.kyantra.core.auth.AuthorizationHelper;
import org.kyantra.annotation.Secure;
import org.kyantra.annotation.Session;
import org.kyantra.common.BaseResource;
import org.kyantra.utils.AwsIotHelper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.UUID;

@Path("/invite")
public class InviteResource extends BaseResource {

    private String source = "support_10@e-yantra.org";

    @Context HttpServletRequest request;
    @Context SecurityContext securityContext;

    public InviteResource(@Context SecurityContext sc,
                   @Context HttpServletRequest request) {
        super(sc, request);
    }

    // TODO: getRequest() returns Null
    private String makeInvitation(String userName, String unitName, String role, String token) {
        System.out.println(token);
        System.out.println(request);
        String url = String.format("http://localhost:8002/invite/accept/?token=%s", token.toString());
        return String.format("%s has invited you to join %s unit with %s access level. \n%s",
                userName, unitName, role, url);
    }

    /*TODO:
    *  1. Check if the receiver has access to the same or parent unit.
    * */
    @POST
    @Path("/email")
    @Session
    @Secure(roles = {RoleEnum.ALL, RoleEnum.WRITE})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String sendInvite(@FormParam("email") @NotNull String email,
                             @FormParam("unitId") @NotNull Integer unitId,
                             @FormParam("role") @NotNull RoleEnum role) throws Exception {

        UnitBean unitBean = UnitDAO.getInstance().get(unitId);
        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();

        // check if the receiver doesn't have any access to the unit
        UserBean invitedUser = UserDAO.getInstance().getByEmail(email);
        if ((invitedUser != null) &&
                (AuthorizationHelper.getInstance().checkAccess(UserDAO.getInstance().getByEmail(email), unitBean)))
            throw new Exception("The user already has some access to the unit!");
        // continue, even if user has no account

        if (unitBean == null)
            throw new DataNotFoundException(ExceptionMessage.DATA_NOT_FOUND);

        if (!AuthorizationHelper.getInstance().checkAccess(userBean, unitBean))
            throw new ForbiddenException(ExceptionMessage.FORBIDDEN);

        String token = UUID.randomUUID().toString();

        InviteBean inviteBean = new InviteBean();
        inviteBean.setEmail(email);
        inviteBean.setToken(token);
        inviteBean.setSentDate(new Date());
        inviteBean.setUnit(unitBean);
        inviteBean.setUser(userBean);
        inviteBean.setAccess(role);
        inviteBean.setAccepted(false);

        // persist
        inviteBean = InviteDAO.getInstance().add(inviteBean);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withText(new Content(makeInvitation(
                                            userBean.getName(),
                                            unitBean.getUnitName(),
                                            role.toString(),
                                            inviteBean.getToken())
                                        )))
                                .withSubject(new Content("Invitation to join IoT Platform")))
                .withSource(source);

        AwsIotHelper.getAmazonSESClient().sendEmail(request);
        // TODO: sensible responses please
        return gson.toJson(inviteBean);
    }

    @GET
    @Path("/accept")
    @Session
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String acceptInvite(@QueryParam("token") @NotNull String token) throws Exception {

        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();
        InviteBean inviteBean = InviteDAO.getInstance().getByToken(token);

        // check if emails are same
        if (!inviteBean.getEmail().equals(userBean.getEmail()))
            throw new Exception("Not a valid account!");

        // all is well, then add entry to rightsBean
        RightsBean rightsBean = new RightsBean();
        rightsBean.setUser(userBean);
        rightsBean.setUnit(inviteBean.getUnit());
        rightsBean.setRole(inviteBean.getAccess());
        RightsDAO.getInstance().add(rightsBean);

        inviteBean.setAccepted(true);
        InviteDAO.getInstance().update(inviteBean.getId(), inviteBean);

        // TODO: sensible responses please
        return gson.toJson(rightsBean);
    }
}
