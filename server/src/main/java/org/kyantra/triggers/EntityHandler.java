package org.kyantra.triggers;

import java.util.Set;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.kyantra.core.thing.ThingDAO;
import org.kyantra.core.device.DeviceDAO;
import org.kyantra.core.deviceAttribute.DeviceAttributeDAO;
import org.kyantra.config.ConfigDAO;
import org.kyantra.core.unit.UnitDAO;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.deviceAttribute.DeviceAttributeBean;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.device.DeviceBean;


public class EntityHandler {
    static EntityHandler instance = new EntityHandler();
    public static EntityHandler getInstance() { return instance; }
//    static String devAccessToken = "c26d9d9ed040446ab76f3a81d6638e24";
    static String devAccessToken = ConfigDAO.getInstance().get("devAccessToken").getValue();

    public static String[] getParameters(Object o) {
        String parameters[] = new String[3];
        if(o instanceof DeviceBean) {
            DeviceBean deviceBean = (DeviceBean) o;
            parameters[0] = deviceBean.getName();
            parameters[1] = ConfigDAO.getInstance().get("device").getValue();
            parameters[2] = deviceBean.getId().toString();
        } else if(o instanceof ThingBean) {
            ThingBean thingBean = (ThingBean) o;
            parameters[0] = thingBean.getName();
            parameters[1] = ConfigDAO.getInstance().get("thing").getValue();
            parameters[2] = thingBean.getId().toString();
        } else if(o instanceof UnitBean) {
            UnitBean unitBean = (UnitBean) o;
            parameters[0] = unitBean.getUnitName();
            parameters[1] = ConfigDAO.getInstance().get("unit").getValue();
            parameters[2] = unitBean.getId().toString();
        } else if(o instanceof DeviceAttributeBean) {
            DeviceAttributeBean deviceAttributeBean = (DeviceAttributeBean) o;
            parameters[0] = deviceAttributeBean.getName();
            parameters[1] = ConfigDAO.getInstance().get("deviceAttribute").getValue();
            parameters[2] = deviceAttributeBean.getId().toString();
        } else {
            return null;
        }
        return parameters;
    }

    // request to dialogflow for accessing APIs
    public String requestFor(String url,String type,String inputData) {
        HttpURLConnection con = null;
        StringBuffer content = null;
        try {
            URL urlForRequest = new URL(url);
            con = (HttpURLConnection) urlForRequest.openConnection();
            con.setRequestMethod(type);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + devAccessToken);
            con.setDoOutput(true);
            String str =  inputData;
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = con.getOutputStream();
            os.write( outputInBytes );
            os.close();
            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
            String inputLine;
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch(Throwable t) {
            t.printStackTrace();
        }
        return ""+content;
    }

    // get list of all objects
    public Boolean isValid(String objectName,Object o) {
        if(o instanceof ThingBean) {
            int count = 0;
            int page = 0;
            List <ThingBean> objects = ThingDAO.getInstance().list(page++,10);
            while(! objects.isEmpty()) {
                List <ThingBean> objectsPage = ThingDAO.getInstance().list(page,10);
                if( objectsPage.size() == 0) {
                    break;
                }
                objects.addAll(objectsPage);
                page++;
            }
            for(ThingBean object : objects) {
                if(object.getName().equals(objectName)) {
                    count++;
                }
            }
            return count == 1;
        } else if(o instanceof DeviceBean) {
            int count = 0;
            int page = 0;
            List <DeviceBean> objects = DeviceDAO.getInstance().list(page++,10);
            while(! objects.isEmpty()) {
                List <DeviceBean> objectsPage = DeviceDAO.getInstance().list(page,10);
                if( objectsPage.size() == 0) {
                    break;
                }
                objects.addAll(objectsPage);
                page++;
            }
            for(DeviceBean object : objects) {
                if(object.getName().equals(objectName)) {
                    count++;
                }
            }
            return count == 1;
        } else if(o instanceof UnitBean) {
            int count = 0;
            int page = 0;
            List <UnitBean> objects = UnitDAO.getInstance().list(page++,10);
            while(! objects.isEmpty()) {
                List <UnitBean> objectsPage = UnitDAO.getInstance().list(page,10);
                if( objectsPage.size() == 0) {
                    break;
                }
                objects.addAll(objectsPage);
                page++;
            }
            for(UnitBean object : objects) {
                if(object.getUnitName().equals(objectName)) {
                    count++;
                }
            }
            return count == 1;
        } else if(o instanceof DeviceAttributeBean) {
            int count = 0;
            int page = 0;
            List <DeviceAttributeBean> objects = DeviceAttributeDAO.getInstance().list(page++,10);
            while(! objects.isEmpty()) {
                List <DeviceAttributeBean> objectsPage = DeviceAttributeDAO.getInstance().list(page,10);
                if( objectsPage.size() == 0) {
                    break;
                }
                objects.addAll(objectsPage);
                page++;
            }
            for(DeviceAttributeBean object : objects) {
                if(object.getName().equals(objectName)) {
                    count++;
                }
            }
            return count == 1;
        }
        return false;
    }

    // add new Entity to dialogflow
    public void triggerAdd(Object o) {
        String parameters[] = getParameters(o);
        String objectName = parameters[0];
        String eid = parameters[1];
        String url = new String("https://api.dialogflow.com/v1/entities/"+eid+"/entries?v=20150910");
        String inputData =  "[{'synonyms': ['" + objectName + "'],'value': '" + objectName + "'}]";
        String output = requestFor(url,"POST",inputData);
        System.out.println(objectName + " added from dialogflow");
    }

    // remove entities from dialogflow
    // added recursive strategy for subEntities
    public void triggerDelete(Object o) {
        String parameters[] = getParameters(o);
        String objectName = parameters[0];
        if(isValid(objectName, o)) {
            int id = Integer.parseInt(parameters[2]);
            String eid = parameters[1];
            String url = new String("https://api.dialogflow.com/v1/entities/"+eid+"/entries?v=20150910");
            String inputData =  "['" + objectName + "']";
            String output = requestFor(url,"DELETE",inputData);
            System.out.println(objectName + " deleted from dialogflow");
            if(o instanceof DeviceBean) {
                DeviceBean deviceBean = (DeviceBean) o;
                List <DeviceAttributeBean> attributes = deviceBean.getDeviceAttributes();
                for(DeviceAttributeBean deviceAttribute : attributes){
                    triggerDelete(deviceAttribute);
                }
            } else if(o instanceof ThingBean) {
                ThingBean thingBean = (ThingBean) o;
                Set<DeviceBean> devices = thingBean.getDevices();
                for(DeviceBean device : devices){
                    triggerDelete(device);
                }
            } else if (o instanceof UnitBean) {
                UnitBean unitBean = (UnitBean) o;
                List <UnitBean> units = unitBean.getSubunits();
                for(UnitBean unit : units) {
                    triggerDelete(unit);
                }
                List <ThingBean> things = unitBean.getThings();
                for(ThingBean thing : things){
                    triggerDelete(thing);
                }
            }
        }
    }

    // update entities from dialogflow
    public void triggerUpdate(String newObjectName,Object object) {
        String parameters[] = getParameters(object);
        String objectName = parameters[0];
        if(objectName.equals(newObjectName)) {
            return ;
        }
        if(isValid(objectName,object)) {
            String eid = parameters[1];
            String url = new String("https://api.dialogflow.com/v1/entities/"+eid+"/entries?v=20150910");
            String inputData =  "['" + objectName + "']";
            String output = requestFor(url,"DELETE",inputData);
            System.out.println(objectName + " deleted from dialogflow");
        }
        // don't check isValid while adding
        objectName = (String)newObjectName;
        String eid = parameters[1];
        String url = new String("https://api.dialogflow.com/v1/entities/"+eid+"/entries?v=20150910");
        String inputData =  "[{'synonyms': ['" + objectName + "'],'value': '" + objectName + "'}]";
        String output = requestFor(url,"POST",inputData);
        System.out.println(objectName + " added from dialogflow");
    }
}
