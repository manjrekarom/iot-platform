package org.kyantra.core.device;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.kyantra.core.thing.ThingBean;

import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kyantra.common.BaseDAO;
import org.kyantra.core.thing.ThingDAO;
import org.kyantra.triggers.EntityHandler;

/**
 * Created by Siddhesh Prabhugaonkar on 13-11-2017.
 */
public class DeviceDAO extends BaseDAO<DeviceBean> {
    static DeviceDAO instance = new DeviceDAO();
    public static DeviceDAO getInstance(){ return instance; }

    public DeviceBean add(DeviceBean bean){
        Session session = getService().getSessionFactory().openSession();
        session.beginTransaction();

        // required by bidirectional one to many
        ThingBean thingBean = ThingDAO.getInstance().get(bean.getParentThing().getId());
        DeviceBean deviceBean = thingBean.addDevice(bean);
        session.saveOrUpdate(thingBean);

        session.getTransaction().commit();
        session.close();
        EntityHandler.getInstance().triggerAdd(deviceBean);
        return deviceBean;
    }

    @Override
    public DeviceBean update(Integer id, DeviceBean deviceBean) {
        return null;
    }

    @Override
    public void delete(DeviceBean deviceBean) {

    }

    /**
     * Returns list of all devices, page by page
     * @param page
     * @param limit
     * @return
     */
    public List<DeviceBean> list(int page, int limit){
        Session session = mService.getSessionFactory().openSession();
        String ql = "from DeviceBean";
        Query query = session.createQuery(ql);
        query.setFirstResult(page*limit);
        query.setMaxResults(limit);
        List<DeviceBean> list = query.getResultList();
        session.close();
        return list;
    }



    public DeviceBean get(Integer id) {
        Session session = getService().getSessionFactory().openSession();
        DeviceBean deviceBean = session.get(DeviceBean.class,id);
        session.close();
        return deviceBean;
    }

    public void delete(Integer id){
        Session session = getService().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        DeviceBean device = session.get(DeviceBean.class, id);
        EntityHandler.getInstance().triggerDelete(device);
        // required by bidirectional one to many
        device.getParentThing().removeDevice(device);
//        session.delete(device);
        tx.commit();
        session.close();
    }

    public void update(int id, String name, String description){
        if(id <=0)
            return;
        Session session = getService().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        DeviceBean device = session.get(DeviceBean.class, id);
        String deviceNew = name;
        EntityHandler.getInstance().triggerUpdate(deviceNew,device);
        device.setName(name);
        device.setDescription(description);
        tx.commit();
        session.close();
    }

    public Set<DeviceBean> getByThing(Integer thingId) {
        Session session = getService().getSessionFactory().openSession();
        String ql = "from DeviceBean where parentThing_Id="+thingId;
        Query query = session.createQuery(ql);
        List<DeviceBean> list = query.getResultList();
        session.close();
        return new HashSet<>(list);
    }
}
