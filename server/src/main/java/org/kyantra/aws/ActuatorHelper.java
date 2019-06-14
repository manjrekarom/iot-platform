package org.kyantra.aws;

import com.amazonaws.services.iot.model.Action;
import com.amazonaws.services.iot.model.TopicRulePayload;
import org.kyantra.core.rule.RuleBean;
import org.kyantra.core.actuator.ActuatorBean;
import org.kyantra.config.ConfigDAO;

import java.util.ArrayList;
import java.util.List;

public class ActuatorHelper {

    private static ActuatorHelper instance = new ActuatorHelper();

    public static ActuatorHelper getInstance() {
        return instance;
    }

    public TopicRulePayload createActuatorRulePayload(RuleBean ruleBean, ActuatorBean actuatorBean, String suffix) {

        // set up for rules
        String ruleCondition = " WHERE ";
        System.out.println(ruleBean.getCondition());
        if (!ruleBean.getCondition().equals(""))
            ruleCondition = ruleCondition + ruleBean.getCondition();
        else
            ruleCondition = ruleBean.getCondition();

        // create rule payload: {lambda function that uses SNS}
        Action action = ActionHelper.getInstance()
                .createLambdaAction(ConfigDAO.getInstance().get("lambdaActuatorArn").getValue());
        List<Action> actionList = new ArrayList<>();
        actionList.add(action);


        // 3. create rulePayload
        TopicRulePayload rulePayload = new TopicRulePayload();
        rulePayload.withDescription(ruleBean.getDescription())
                .withSql("SELECT " + ruleBean.getData() + suffix
                        + " FROM '$aws/things/thing"
                        + ruleBean.getParentThing().getId()
                        + "/shadow/update'" + ruleCondition)
                .withRuleDisabled(false)
                .withAwsIotSqlVersion("2016-03-23")
                .withActions(actionList);

        return rulePayload;
    }
}
