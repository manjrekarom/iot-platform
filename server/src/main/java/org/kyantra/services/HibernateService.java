package org.kyantra.services;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import org.kyantra.config.ConfigBean;
import org.kyantra.config.EnvironmentConfig;
import org.kyantra.core.actuator.ActuatorBean;
import org.kyantra.core.auth.RightsBean;
import org.kyantra.core.auth.SessionBean;
import org.kyantra.core.blockly.BlocklyBean;
import org.kyantra.core.cron.CronBean;
import org.kyantra.core.device.DeviceBean;
import org.kyantra.core.deviceAttribute.DeviceAttributeBean;
import org.kyantra.core.invite.InviteBean;
import org.kyantra.core.rule.RuleBean;
import org.kyantra.core.sns.SnsBean;
import org.kyantra.core.sns.SnsSubscriptionBean;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.user.UserBean;

public class HibernateService {

    private static HibernateService mService = null;
    private static SessionFactory sessionFactory = null;

    private HibernateService(String envConf) {
        Configuration configuration = new Configuration();

        if (envConf.equals("test"))
            configuration.configure("hibernate-test.cfg.xml");
        else if (envConf.equals("dev"))
            configuration.configure("hibernate.cfg.xml");
        else if (System.getProperty("environment") == null)
            configuration.configure("hibernate.cfg.xml");
        else if (System.getProperty("environment").equals(EnvironmentConfig.TEST))
            configuration.configure("hibernate-test.cfg.xml");
        else
            configuration.configure("hibernate.cfg.xml");

        configuration.addAnnotatedClass(UserBean.class)
                .addAnnotatedClass(RightsBean.class)
                .addAnnotatedClass(UnitBean.class)
                .addAnnotatedClass(DeviceAttributeBean.class)
                .addAnnotatedClass(DeviceBean.class)
                .addAnnotatedClass(ThingBean.class)
                .addAnnotatedClass(SessionBean.class)
                .addAnnotatedClass(ConfigBean.class)
                .addAnnotatedClass(CronBean.class)
                .addAnnotatedClass(RuleBean.class)
                .addAnnotatedClass(SnsBean.class)
                .addAnnotatedClass(SnsSubscriptionBean.class)
                .addAnnotatedClass(BlocklyBean.class)
                .addAnnotatedClass(ActuatorBean.class)
                .addAnnotatedClass(InviteBean.class);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());

        sessionFactory = configuration.buildSessionFactory(builder.build());
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static HibernateService getInstance() {
        if (mService == null)
            mService = new HibernateService("dev");
        return mService;
    }

    public static HibernateService getInstance(String envConf) {
        if (mService == null)
            mService = new HibernateService(envConf);
        return mService;
    }
}
