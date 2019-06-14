package org.kyantra.core.cron;

import io.swagger.annotations.Api;
import org.kyantra.common.RoleEnum;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.user.UserBean;
import org.kyantra.core.thing.ThingDAO;
import org.kyantra.exception.AccessDeniedException;
import org.kyantra.core.auth.AuthorizationHelper;
import org.kyantra.annotation.Secure;
import org.kyantra.annotation.Session;
import org.kyantra.common.BaseResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.Set;
import java.util.List;

/**
 * Created by Lenovo on 12-11-2017.
 */

@Path("/cron")
@Api(value="cron")
public class CronResource extends BaseResource {

    int limit = 10;

    CronResource(@Context SecurityContext sc,
                  @Context HttpServletRequest request) {
        super(sc, request);
    }

    @GET
    @Path("get/{id}")
    @Session
    @Secure(roles = {RoleEnum.READ, RoleEnum.WRITE, RoleEnum.ALL})
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("id") Integer id) throws AccessDeniedException {
        CronBean bean = CronDAO.getInstance().get(id);
        ThingBean targetThing = bean.getParentThing();
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();
        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            return gson.toJson(bean);
        }
        else throw new AccessDeniedException();
    }


    @GET
    @Path("thing/{id}")
    @Session
    @Secure(roles = {RoleEnum.READ, RoleEnum.WRITE, RoleEnum.ALL})
    @Produces(MediaType.APPLICATION_JSON)
    public String getByThing(@PathParam("id") Integer id) throws AccessDeniedException {
        ThingBean targetThing = ThingDAO.getInstance().get(id);
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();

        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            Set<CronBean> bean = CronDAO.getInstance().getByThingId(id);
            return gson.toJson(bean);
        }
        else throw new AccessDeniedException();
    }


    @DELETE
    @Path("delete/{id}")
    @Session
    @Secure(roles = {RoleEnum.ALL,RoleEnum.WRITE}, subjectType = "deviceAttributes", subjectField = "parentId")
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@PathParam("id") Integer id) throws AccessDeniedException {
        CronBean bean = CronDAO.getInstance().get(id);
        ThingBean targetThing = bean.getParentThing();
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();
        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            try {
                CronDAO.getInstance().delete(id);
                return "{}";
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return "{}";
        }
        else throw new AccessDeniedException();
    }

    @GET
    @Session
    @Path("list/page/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@PathParam("page") Integer page) {
        List<CronBean> crons= CronDAO.getInstance().list(page,limit);
        return gson.toJson(crons);
    }

    @POST
    @Path("create")
    @Session
    @Secure(roles = {RoleEnum.ALL,RoleEnum.WRITE}, subjectType = "unit", subjectField = "parent_id")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String create(@FormParam("thingId") Integer thingId,
            @FormParam("name") String cronName,
            @FormParam("cronExpression") String cronExpression,
            @FormParam("desiredState") String desiredState,
            @FormParam("ruleCronXml") String ruleCronXml) throws AccessDeniedException {
        ThingBean targetThing = ThingDAO.getInstance().get(thingId);
        UserBean user = (UserBean) getSecurityContext().getUserPrincipal();
        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            try {
                CronBean bean = new CronBean();
                bean.setCronName(cronName);
                bean.setCronExpression(cronExpression);
                bean.setDesiredState(desiredState);
                bean.setParentThing(ThingDAO.getInstance().get(thingId));
                bean = CronDAO.getInstance().add(bean);

                return gson.toJson(bean);

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return "{\"success\":false}";
        }
        else throw new AccessDeniedException();
    }
}
