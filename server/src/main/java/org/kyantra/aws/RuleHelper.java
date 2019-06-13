package org.kyantra.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.iot.model.*;
import org.kyantra.core.rule.RuleBean;
import org.kyantra.core.sns.SnsBean;
import org.kyantra.core.actuator.ActuatorBean;
import org.kyantra.core.rule.RuleDAO;
import org.kyantra.utils.AwsIotHelper;

public class RuleHelper {

    public static RuleHelper instance = new RuleHelper();

    public static RuleHelper getInstance() {
        return instance;
    }


    public CreateTopicRuleResult createTopicRule(RuleBean ruleBean, Object actionBean) {

        CreateTopicRuleRequest topicRuleRequest = new CreateTopicRuleRequest();

        if (ruleBean.getType().equals("SNS")) {
            SnsBean snsBean = (SnsBean) actionBean;

            // constructed names of entities
            String thingName = "thing" + ruleBean.getParentThing().getId();
            String ruleName = ruleBean.getName();
            String ruleNameAws = thingName + "_sns_" + ruleName;

            // add ruleName in rule query
            String data = ruleBean.getData();
            // this is string prefix needed to get ruleName inside a rule
            String suffix = "";
            if(!data.equals("")) {
                suffix = ", \"" + thingName + "_sns_" + ruleName + "\" as ruleName";
            }

            // put details about the item in DynamoDB NotificationDetail table
            AmazonDynamoDB amazonDynamoDB = AwsIotHelper.getAmazonDynamoDBClient();
            DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
            Table table = dynamoDB.getTable("NotificationDetail");
            Item item = new Item()
                    .withPrimaryKey("ruleName", ruleNameAws)
                    .withString("topic", snsBean.getTopic())
                    .withString("topicArn", snsBean.getTopicARN())
                    .withLong("timestamp", 0l)
                    .withString("message", snsBean.getMessage())
                    .withString("subject", snsBean.getSubject())
                    .withInt("interval", snsBean.getInterval());
            PutItemOutcome outcome = table.putItem(item);

            // create rule payload: {using data provided in ruleBean and snsBean}
            TopicRulePayload rulePayload = SnsHelper.getInstance().createSnsRulePayload(ruleBean, snsBean, suffix);

            // create rule at AWS
            topicRuleRequest.withRuleName(ruleNameAws)
                    .withTopicRulePayload(rulePayload);
        }
        else if (ruleBean.getType().equals("Actuator")) {
            ActuatorBean actuatorBean = (ActuatorBean) actionBean;

            // constructed names of entities
            String thingName = "thing" + ruleBean.getParentThing().getId();
            String ruleName = ruleBean.getName();
            String ruleNameAws = thingName + "_actuator_" + ruleName;

            // add ruleName in rule query
            String data = ruleBean.getData();
            // this is string prefix needed to get ruleName inside a rule
            String suffix = "";
            if(!data.equals("")) {
                suffix = ", \"" + thingName + "_actuator_" + ruleName + "\" as ruleName";
            }

            // put details about the item in DynamoDB NotificationDetail table
            AmazonDynamoDB amazonDynamoDB = AwsIotHelper.getAmazonDynamoDBClient();
            DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
            Table table = dynamoDB.getTable("actuatorStates");
            Item item = new Item()
                    .withPrimaryKey("ruleName", ruleNameAws)
                    .withString("attribute", actuatorBean.getAttribute())
                    .withString("newValue", actuatorBean.getNewValue());
            PutItemOutcome outcome = table.putItem(item);

            // create rule payload: {using data provided in ruleBean and snsBean}
            TopicRulePayload rulePayload = ActuatorHelper.getInstance().createActuatorRulePayload(ruleBean, actuatorBean, suffix);

            // create rule at AWS
            topicRuleRequest.withRuleName(ruleNameAws)
                    .withTopicRulePayload(rulePayload);
        }
        return AwsIotHelper.getIotClient().createTopicRule(topicRuleRequest);
    }



    public ReplaceTopicRuleResult replaceTopicRule(RuleBean ruleBean, Object actionBean) {

        ReplaceTopicRuleRequest topicRuleRequest = new ReplaceTopicRuleRequest();

        if (ruleBean.getType().equals("SNS")) {
            SnsBean snsBean = (SnsBean) actionBean;

            //constructed names of entities
            String thingName = "thing" + ruleBean.getParentThing().getId();
            String ruleName = ruleBean.getName();
            String ruleNameAws = thingName + "_sns_" + ruleName;

            // add ruleName in rule query
            String data = ruleBean.getData();
            // this is string prefix needed to get ruleName inside a rule
            String suffix = "";
            if(!data.equals("")) {
                suffix = ", \"" + thingName + "_sns_" + ruleName + "\" as ruleName";
            }

            // create rule payload: {using data provided in ruleBean and snsBean}
            TopicRulePayload rulePayload = SnsHelper.getInstance().createSnsRulePayload(ruleBean, snsBean, suffix);

            // replace rule at AWS
            topicRuleRequest.withRuleName(thingName + "_sns_" + ruleName)
                    .withTopicRulePayload(rulePayload);

            // Update the rule in DB
            RuleDAO.getInstance().update(ruleBean);
        } else if (ruleBean.getType().equals("Actuator")) {
            ActuatorBean actuatorBean = (ActuatorBean) actionBean;

            // constructed names of entities
            String thingName = "thing" + ruleBean.getParentThing().getId();
            String ruleName = ruleBean.getName();
            String ruleNameAws = thingName + "_actuator_" + ruleName;

            // add ruleName in rule query
            String data = ruleBean.getData();
            // this is string prefix needed to get ruleName inside a rule
            String suffix = "";
            if(!data.equals("")) {
                suffix = ", \"" + thingName + "_actuator_" + ruleName + "\" as ruleName";
            }


            // create rule payload: {using data provided in ruleBean and snsBean}
            TopicRulePayload rulePayload = ActuatorHelper.getInstance().createActuatorRulePayload(ruleBean, actuatorBean, suffix);

            // create rule at AWS
            topicRuleRequest.withRuleName(ruleNameAws)
                    .withTopicRulePayload(rulePayload);
        }

        return AwsIotHelper.getIotClient().replaceTopicRule(topicRuleRequest);
    }


    public GetTopicRuleResult getTopicRule(String ruleName) {
        return AwsIotHelper.getIotClient().getTopicRule(new GetTopicRuleRequest()
                    .withRuleName(ruleName));
    }


    public DeleteTopicRuleResult deleteRule(RuleBean ruleBean) {
        DeleteTopicRuleRequest deleteTopicRuleRequest = new DeleteTopicRuleRequest();
        String ruleName = "thing" + ruleBean.getParentThing().getId() + "_sns_" + ruleBean.getName();
        deleteTopicRuleRequest.withRuleName(ruleName);

        // delete item from NotificationDetail table
        AmazonDynamoDB amazonDynamoDB = AwsIotHelper.getAmazonDynamoDBClient();
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable("NotificationDetail");
        table.deleteItem(new PrimaryKey("ruleName", ruleName));

        ruleName = "thing" + ruleBean.getParentThing().getId() + "_actuator_" + ruleBean.getName();
        deleteTopicRuleRequest.withRuleName(ruleName);

        table = dynamoDB.getTable("actuatorStates");
        table.deleteItem(new PrimaryKey("ruleName", ruleName));

        DeleteTopicRuleResult deleteTopicRuleResult =
                AwsIotHelper.getIotClient().deleteTopicRule(deleteTopicRuleRequest);
        return deleteTopicRuleResult;
    }
}
