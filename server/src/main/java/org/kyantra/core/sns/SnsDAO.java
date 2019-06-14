package org.kyantra.core.sns;

import org.hibernate.Session;
import org.kyantra.core.rule.RuleBean;
import org.kyantra.core.rule.RuleDAO;
import org.kyantra.common.BaseDAO;

public class SnsDAO extends BaseDAO {
    static SnsDAO instance = new SnsDAO();
    public static SnsDAO getInstance() { return instance; }

    public SnsBean add(SnsBean bean) {
        Session session = getService().getSessionFactory().openSession();
        session.beginTransaction();

        RuleBean ruleBean = RuleDAO.getInstance().get(bean.getParentRule().getId());
        SnsBean snsBean = ruleBean.addSNSAction(bean);

        session.saveOrUpdate(ruleBean);
        session.getTransaction().commit();
        session.close();
        return snsBean;
    }

    public SnsBean get(Integer id) {
        Session session = getService().getSessionFactory().openSession();
        SnsBean snsBean = session.get(SnsBean.class, id);
        session.close();
        return snsBean;
    }
}
