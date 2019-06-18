package org.kyantra.core.sns;

import org.hibernate.Session;
import org.kyantra.common.BaseDAO;

public class SnsSubscriptionDAO extends BaseDAO<SnsSubscriptionBean> {
    static SnsSubscriptionDAO instance = new SnsSubscriptionDAO();
    public static SnsSubscriptionDAO getInstance() {
        return instance;
    }

    public SnsSubscriptionBean add(SnsSubscriptionBean subscriptionBean) {

        Session session = getService().getSessionFactory().openSession();
        session.beginTransaction();

        SnsBean snsBean = SnsDAO.getInstance().get(subscriptionBean.getParentSNSBean().getId());
        SnsSubscriptionBean snsSubscriptionBean = snsBean.addSubscription(subscriptionBean);

        session.saveOrUpdate(snsBean);
        session.getTransaction().commit();
        session.close();
        return snsSubscriptionBean;
    }

    @Override
    public SnsSubscriptionBean update(Integer id, SnsSubscriptionBean snsSubscriptionBean) {
        return null;
    }

    @Override
    public void delete(SnsSubscriptionBean snsSubscriptionBean) {

    }

    public  SnsSubscriptionBean get(Integer id) {

        Session session = getService().getSessionFactory().openSession();
        SnsSubscriptionBean snsSubscriptionBean = session.get(SnsSubscriptionBean.class, id);
        session.close();
        return snsSubscriptionBean;
    }
}
