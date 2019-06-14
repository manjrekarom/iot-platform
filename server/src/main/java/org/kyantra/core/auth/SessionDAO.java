package org.kyantra.core.auth;

import org.hibernate.Session;
import org.kyantra.common.BaseDAO;

public class SessionDAO extends BaseDAO {
    static SessionDAO instance = new SessionDAO();
    public static SessionDAO getInstance() { return instance; }

    public SessionBean add(SessionBean bean) {
        Session session = getService().getSessionFactory().openSession();
        session.beginTransaction();
        session.save(bean);
        session.getTransaction().commit();
        session.close();
        return bean;
    }
}
