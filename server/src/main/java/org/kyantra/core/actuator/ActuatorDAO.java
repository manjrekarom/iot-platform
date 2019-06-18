package org.kyantra.core.actuator;

import org.hibernate.Session;
import org.kyantra.core.rule.RuleBean;
import org.kyantra.common.BaseDAO;
import org.kyantra.core.rule.RuleDAO;

public class ActuatorDAO extends BaseDAO<ActuatorBean> {
    static ActuatorDAO instance = new ActuatorDAO();
    public static ActuatorDAO getInstance() { return instance; }

    public ActuatorBean add(ActuatorBean bean) {
        Session session = getService().getSessionFactory().openSession();
        session.beginTransaction();

        RuleBean ruleBean = RuleDAO.getInstance().get(bean.getParentRule().getId());
        ActuatorBean actuatorBean = ruleBean.addActuatorAction(bean);

        session.saveOrUpdate(ruleBean);
        session.getTransaction().commit();
        session.close();
        return actuatorBean;
    }

    @Override
    public ActuatorBean update(Integer id, ActuatorBean actuatorBean) {
        return null;
    }

    @Override
    public void delete(ActuatorBean actuatorBean) {

    }

    public ActuatorBean get(Integer id) {
        Session session = getService().getSessionFactory().openSession();
        ActuatorBean actuatorBean = session.get(ActuatorBean.class, id);
        session.close();
        return actuatorBean;
    }
}
