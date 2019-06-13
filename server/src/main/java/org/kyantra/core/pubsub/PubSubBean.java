package org.kyantra.core.pubsub;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.NaturalId;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;

public class PubSubBean {

    @Expose
    Integer deviceAttributeId;

    @Expose
    String deviceAttributeName;

    @Expose
    Integer deviceId;

    @Expose
    String deviceName;

    @Expose
    Integer parentThingId;

    @Expose
    String parentThingName;

    public Integer getDeviceAttributeId() {
        return deviceAttributeId;
    }

    public void setDeviceAttributeId(Integer deviceAttributeId) {
        this.deviceAttributeId = deviceAttributeId;
    }

    public void setParentThingName(String parentThingName) {
        this.parentThingName = parentThingName;
    }

    public String getParentThingName() {
        return parentThingName;
    }

    public void setDeviceAttributeName(String deviceAttributeName) {
        this.deviceAttributeName = deviceAttributeName;
    }

    public String getDeviceAttributeName() {
        return deviceAttributeName;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getParentThingId() {
        return parentThingId;
    }

    public void setParentThingId(Integer parentThingId) {
        this.parentThingId = parentThingId;
    }
}
