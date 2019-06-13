package org.kyantra.core.deviceAttribute;

import io.swagger.annotations.Api;
import org.kyantra.common.RoleEnum;
import org.kyantra.core.device.DeviceBean;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.user.UserBean;
import org.kyantra.core.device.DeviceDAO;
import org.kyantra.core.unit.UnitDAO;
import org.kyantra.exception.AccessDeniedException;
import org.kyantra.core.auth.AuthorizationHelper;
import org.kyantra.annotation.Secure;
import org.kyantra.annotation.Session;
import org.kyantra.common.BaseResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * Created by Siddhesh Prabhugaonkar on 13-11-2017.
 */
@Path("/attribute")
@Api(value="attribute")
public class DeviceAttributeResource extends BaseResource {

    int limit = 10;

    DeviceAttributeResource(@Context SecurityContext sc,
                  @Context HttpServletRequest request) {
        super(sc, request);
    }

    @GET
    @Path("get/{id}")
    @Session
    @Secure(roles = {RoleEnum.READ, RoleEnum.WRITE, RoleEnum.ALL})
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("id") Integer id) throws AccessDeniedException {
        DeviceAttributeBean deviceAttributeBean = DeviceAttributeDAO.getInstance().get(id);
        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();

        if (AuthorizationHelper.getInstance().checkAccess(userBean, deviceAttributeBean)) {
            return gson.toJson(deviceAttributeBean);
        }

        else throw new AccessDeniedException();
    }


    @GET
    @Path("/list/page/{page}")
    @Session
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@PathParam("page") Integer pageNumber) {
        List<DeviceAttributeBean> deviceAttributeBeanList = DeviceAttributeDAO.getInstance().list(pageNumber, limit);

        return gson.toJson(deviceAttributeBeanList);
    }


    @PUT
    @Path("update/{id}")
    @Session
    @Secure(roles = {RoleEnum.ALL, RoleEnum.WRITE}, subjectType = "deviceAttributes", subjectField = "parentId")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String update(@PathParam("id") Integer id,
                         @FormParam("name") String name,
                         @FormParam("type") String type,
                         @FormParam("def") String def) throws AccessDeniedException{
        DeviceAttributeBean deviceAttributeBean = DeviceAttributeDAO.getInstance().get(id);
        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();

        if (AuthorizationHelper.getInstance().checkAccess(userBean, deviceAttributeBean)) {
            DeviceAttributeDAO.getInstance().update(id, name, type, def);
            DeviceAttributeBean bean = DeviceAttributeDAO.getInstance().get(id);
            return gson.toJson(bean);
        }
        else throw new AccessDeniedException();
    }


    @DELETE
    @Path("delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secure(roles = {RoleEnum.ALL, RoleEnum.WRITE}, subjectType = "deviceAttributes", subjectField = "parentId")
    @Session
    public String delete(@PathParam("id") Integer id) throws  AccessDeniedException{
        DeviceAttributeBean deviceAttributeBean = DeviceAttributeDAO.getInstance().get(id);
        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();

        if (AuthorizationHelper.getInstance().checkAccess(userBean, deviceAttributeBean)) {
            try {
                DeviceAttributeDAO.getInstance().delete(id);
                return "{}";
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return "{}";
        }
        else throw new AccessDeniedException();
    }


    @POST
    @Path("create")
    @Session
    @Secure(roles = {RoleEnum.ALL, RoleEnum.WRITE}, subjectType = "deviceAttributes", subjectField = "parentId")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String create(@FormParam("name") String name,
                         @FormParam("type") String type,
                         @FormParam("def") String def,
                         @FormParam("parentDeviceId") Integer parentDeviceId,
                         @FormParam("ownerUnitId") Integer ownerUnitId) throws AccessDeniedException {
        UnitBean targetUnit = UnitDAO.getInstance().get(ownerUnitId);
        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();

        if (AuthorizationHelper.getInstance().checkAccess(userBean, targetUnit)) {
            try {
                String s = "Found something";
                DeviceAttributeBean deviceAttribute = new DeviceAttributeBean();
                deviceAttribute.setName(name);
                deviceAttribute.setType(type);
                deviceAttribute.setDef(def);
                deviceAttribute.setParentDevice(DeviceDAO.getInstance().get(parentDeviceId));
                deviceAttribute.setOwnerUnit(targetUnit);

                DeviceAttributeBean deviceAttributeBean = DeviceAttributeDAO.getInstance().add(deviceAttribute);
                return gson.toJson(deviceAttributeBean);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return "{\"success\":false}";
        }
        else throw new AccessDeniedException();
    }


    @POST
    @Path("add/{deviceId}")
    @Session
    @Secure(roles = {RoleEnum.ALL, RoleEnum.WRITE})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String add(@PathParam("deviceId") Integer deviceId,
                         List<DeviceAttributeBean> attributes) throws AccessDeniedException {
        DeviceBean deviceBean = DeviceDAO.getInstance().get(deviceId);
        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();

        if (AuthorizationHelper.getInstance().checkAccess(userBean, deviceBean)) {
            for (DeviceAttributeBean att : attributes) {
                att.setParentDevice(DeviceDAO.getInstance().get(deviceId));
                try {
                    DeviceAttributeDAO.getInstance().add(att);
                } catch (Throwable t) {
                    t.printStackTrace();
                } //later change to something
            }
            return "{\"success\": true}";
        }
        else throw new AccessDeniedException();
    }
}
