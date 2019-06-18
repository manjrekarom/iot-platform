package org.kyantra.core.auth;

import org.hibernate.Session;
import org.kyantra.common.BaseDAO;

public class SessionDAO extends BaseDAO<SessionBean> {
    static SessionDAO instance = new SessionDAO();
    public static SessionDAO getInstance() { return instance; }

    @Override
    public SessionBean get(Integer id) {
        return null;
    }

    public SessionBean add(SessionBean bean) {
        Session session = getService().getSessionFactory().openSession();
        session.beginTransaction();
        session.save(bean);
        session.getTransaction().commit();
        session.close();
        return bean;
    }

    @Override
    public SessionBean update(Integer id, SessionBean sessionBean) {
        return null;
    }

    @Override
    public void delete(SessionBean sessionBean) {

    }
}
