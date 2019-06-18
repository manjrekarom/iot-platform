package org.kyantra.core.invite;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.kyantra.core.auth.SessionBean;
import org.kyantra.common.BaseDAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public class InviteDAO extends BaseDAO<InviteBean> {
    private static InviteDAO instance = new InviteDAO();
    public static InviteDAO getInstance() {
        return instance;
    }

    private InviteDAO() {
    }

    public InviteBean add(InviteBean inviteBean) {
        Session session = getService().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(inviteBean);
        tx.commit();
        session.close();
        return inviteBean;
    }

    public InviteBean get(Integer id) {
        Session session = getService().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        InviteBean inviteBean = session.get(InviteBean.class, id);
        tx.commit();
        session.close();
        return inviteBean;
    }

    public InviteBean getByToken(String token) {
        Session session = mService.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SessionBean> criteriaQuery = builder.createQuery(SessionBean.class);
        InviteBean inviteBean = (InviteBean) session.createQuery("FROM InviteBean WHERE Token = :token")
                .setParameter("token", token)
                .uniqueResult();
        session.close();
        return inviteBean;
    }

    public InviteBean update(Integer id, InviteBean inviteBean) {
        if (id <= 0)
            return null;

        Session session = getService().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        InviteBean oldInvite = session.get(InviteBean.class, id);

        // resend invitation and change sent date to the latest
        // change accepted status and access level
        oldInvite.setSentDate(inviteBean.getSentDate());
        oldInvite.setAccepted(inviteBean.isAccepted());
        oldInvite.setAccess(inviteBean.getAccess());

        tx.commit();
        session.close();
        return oldInvite;
    }

    @Override
    public void delete(InviteBean inviteBean) {

    }
}
