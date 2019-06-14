package org.kyantra.core.auth;

import org.kyantra.core.device.DeviceBean;
import org.kyantra.core.deviceAttribute.DeviceAttributeBean;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.unit.UnitHelper;
import org.kyantra.core.user.UserBean;

import java.util.Set;

public class AuthorizationHelper {
    private static AuthorizationHelper instance = new AuthorizationHelper();

    private AuthorizationHelper() {

    }

    public static AuthorizationHelper getInstance() {
        return instance;
    }

    public Boolean checkAccess(UserBean user, UnitBean targetUnit) {
        Set<UnitBean> userUnits = RightsDAO.getInstance().getUnitsByUser(user);
        Set<UnitBean> targetAncestors = UnitHelper.getInstance().getAllParents(targetUnit);

        for(UnitBean userUnit: userUnits) {
            // contains will check for equality using equals
            // if no equals is overridden it'll default to equal from Object class
            // which is same as '=='
            if (targetAncestors.contains(userUnit))
                return true;
        }
        return false;
    }


    public Boolean checkAccess(UserBean user, ThingBean targetThing) {
        UnitBean targetUnit = targetThing.getParentUnit();
        return checkAccess(user, targetUnit);
    }


    public Boolean checkAccess(UserBean user, DeviceBean targetDevice) {
        UnitBean targetUnit = targetDevice.getOwnerUnit();
        return checkAccess(user, targetUnit);
    }


    public Boolean checkAccess(UserBean user, DeviceAttributeBean targetDeviceAttribute) {
        UnitBean targetUnit = targetDeviceAttribute.getOwnerUnit();
        DeviceBean targetDevice = targetDeviceAttribute.getParentDevice();

        // TODO: 6/6/18 Change after bean validation; Required OwnerUnit
        if (targetDevice != null)
            return checkAccess(user, targetDevice);

        return checkAccess(user, targetUnit);
    }
}
