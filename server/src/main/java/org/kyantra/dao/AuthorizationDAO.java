package org.kyantra.dao;

import org.hibernate.Session;
import org.kyantra.beans.*;

import javax.persistence.Query;
import java.util.List;

public class AuthorizationDAO extends BaseDAO{

    private static AuthorizationDAO instance = new AuthorizationDAO();

    public static AuthorizationDAO getInstance(){ return instance; }

    private AuthorizationDAO() {

    }

    public boolean ownsUnit(UserBean user, UnitBean unit){
        try {
            Session session = getService().getSessionFactory().openSession();
            Query query = session.createQuery("from RightsBean where UserId = :userId and unit_id = :unitId");
            query.setParameter("userId", user.getId());
            query.setParameter("unitId", unit.getId());
            List<RightsBean> rights = query.getResultList();
            session.close();

            return rights.size() > 0;

        } catch (Throwable t) {
            t.printStackTrace();
            throw  t;
        }
    }

    public boolean ownsThing(UserBean user, ThingBean thing){

        try {
            Session session = getService().getSessionFactory().openSession();
            Query query = session.createQuery("from RightsBean where UserId = :userId and unit_id = :unitId");
            query.setParameter("userId", user.getId());
            query.setParameter("unitId", thing.getParentUnit().getId());
            List<RightsBean> rights = query.getResultList();
            session.close();

            return rights.size() > 0;

        } catch (Throwable t) {
            return true;
        }
    }

    public boolean ownsDevice(UserBean user, DeviceBean device){

        try {
            Session session = getService().getSessionFactory().openSession();
            Query query = session.createQuery("from RightsBean where UserId = :userId and unit_id = :unitId");
            query.setParameter("userId", user.getId());
            query.setParameter("unitId", device.getOwnerUnit().getId());
            List<RightsBean> rights = query.getResultList();
            session.close();


            return rights.size() > 0;
        } catch (Throwable t) {
            return true;
        }
    }

    public boolean ownsDeviceAttributes(UserBean user, DeviceAttributeBean deviceAttribute){
        try {
            Session session = getService().getSessionFactory().openSession();
            Query query = session.createQuery("from RightsBean where UserId = :userId and unit_id = :unitId");
            query.setParameter("userId", user.getId());
            query.setParameter("unitId", deviceAttribute.getOwnerUnit().getId());
            List<RightsBean> rights = query.getResultList();
            session.close();

            return rights.size() > 0;
        } catch (Throwable t) {
            return true;
        }
    }
}
