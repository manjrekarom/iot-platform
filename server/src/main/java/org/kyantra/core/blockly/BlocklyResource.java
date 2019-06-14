package org.kyantra.core.blockly;

import java.util.Set;
import java.util.List;

import org.kyantra.annotation.Session;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.kyantra.annotation.Secure;
import org.kyantra.common.RoleEnum;
import org.kyantra.core.device.DeviceBean;
import org.kyantra.core.deviceAttribute.DeviceAttributeBean;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.user.UserBean;
import org.kyantra.exception.AccessDeniedException;
import org.kyantra.core.thing.ThingDAO;
import org.kyantra.core.device.DeviceDAO;
import org.kyantra.core.unit.UnitDAO;
import org.kyantra.core.auth.AuthorizationHelper;
import org.kyantra.common.BaseResource;

@Path("/blockly")
public class BlocklyResource extends BaseResource {

    BlocklyResource(@Context SecurityContext sc,
                  @Context HttpServletRequest request) {
        super(sc, request);
    }

    @GET
    @Session
    @Path("/getJson")
    @Produces(MediaType.TEXT_PLAIN)
    public String getJSON() throws AccessDeniedException {
        int page = 0;
        //Units Json
        List <UnitBean> unitObjects = UnitDAO.getInstance().list(page++,10);
        while(! unitObjects.isEmpty()) {
            List <UnitBean> objectsPage = UnitDAO.getInstance().list(page,10);
            if( objectsPage.size() == 0) {
                break;
            }
            unitObjects.addAll(objectsPage);
            page++;
        }
        String allUnitsJson = "[";
        String unitJson = "{\"\":{\"subunits\": [[]],\"things\": [[]]},";
        for(UnitBean object : unitObjects) {
            allUnitsJson += "[\""+object.getId()+","+object.getUnitName()+"\",\""+object.getId()+","+object.getUnitName()+"\"],";
            unitJson += "\""+object.getId()+","+object.getUnitName()+"\" : { ";
            unitJson +="\"subunits\" : [ ";
            List <UnitBean> subunits = object.getSubunits();
            if(subunits.size() == 0) {
                unitJson += "[]";
            } else {
                for(UnitBean subObject : subunits) {
                    unitJson+="[\""+subObject.getId()+","+subObject.getUnitName()+"\",\""+subObject.getId()+","+subObject.getUnitName()+"\"],";
                }
            }
            unitJson+="],";
            unitJson+="\"things\" : [";
            List <ThingBean> things = object.getThings();
            if(things.size() == 0) {
                unitJson += "[]";
            } else {
                for(ThingBean subObject : things) {
                    unitJson+="[\""+subObject.getId()+","+subObject.getName()+"\",\""+subObject.getId()+","+subObject.getName()+"\"],";
                }
            }
            unitJson+="]},";
        }
        allUnitsJson += "]";
        allUnitsJson = allUnitsJson.replace(",]","]");
        unitJson+="}";
        unitJson = unitJson.replace(",]","]");
        unitJson = unitJson.replace(",}","}");

        //Things Json
        page = 0;
        List <ThingBean> thingObjects = ThingDAO.getInstance().list(page++,10);
        while(! thingObjects.isEmpty()) {
            List <ThingBean> thingObjectsPage = ThingDAO.getInstance().list(page,10);
            if( thingObjectsPage.size() == 0) {
                break;
            }
            thingObjects.addAll(thingObjectsPage);
            page++;
        }

        String thingJson = "{\"\": {\"devices\": [[]]},";
        for(ThingBean thingObject : thingObjects) {
            thingJson += "\""+thingObject.getId()+","+thingObject.getName()+"\" : { ";
            thingJson +="\"devices\" : [ ";
            Set <DeviceBean> devices = thingObject.getDevices();
            if(devices.size() == 0) {
                thingJson += "[]";
            } else {
                for(DeviceBean device : devices) {
                    thingJson+="[\""+device.getId()+","+device.getName()+"\",\""+device.getId()+","+device.getName()+"\"],";
                }
            }

            thingJson+="]},";
        }
        thingJson+="}";
        thingJson = thingJson.replace(",]","]");
        thingJson = thingJson.replace(",}","}");

        //devices Json
        page = 0;
        List <DeviceBean> deviceObjects = DeviceDAO.getInstance().list(page++,10);
        while(! deviceObjects.isEmpty()) {
            List <DeviceBean> objectsPage = DeviceDAO.getInstance().list(page,10);
            if( objectsPage.size() == 0) {
                break;
            }
            deviceObjects.addAll(objectsPage);
            page++;
        }
        String deviceJson = "{\"\": {\"attributes\": [[]]},";
        for(DeviceBean object : deviceObjects) {
            deviceJson += "\""+object.getId()+","+object.getName()+"\" : { ";
            deviceJson +="\"attributes\" : [ ";
            List <DeviceAttributeBean> attributes = object.getDeviceAttributes();
            if(attributes.size() == 0) {
                deviceJson += "[]";
            } else {
                for(DeviceAttributeBean subObject : attributes) {
                    deviceJson+="[\""+subObject.getId()+","+subObject.getName()+"\",\""+subObject.getId()+","+subObject.getName()+"\"],";
                }
            }
            deviceJson+="]},";
        }
        deviceJson+="}";
        deviceJson = deviceJson.replace(",]","]");
        deviceJson = deviceJson.replace(",}","}");

        String finalJson = "{\"allUnits\":"+allUnitsJson+",\"unitsJson\":"+unitJson+",\"thingsJson\":"+thingJson+",\"devicesJson\":"+deviceJson+"}";
        return finalJson;
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
            Set<BlocklyBean> blocklybeans = BlocklyDAO.getInstance().getByThingId(id);
            return gson.toJson(blocklybeans);
        }
        else throw new AccessDeniedException();
    }
}
