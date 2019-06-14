package org.kyantra.core.rule;

import com.amazonaws.services.iot.model.*;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.AddPermissionRequest;
import io.swagger.annotations.Api;
import org.kyantra.aws.RuleHelper;
import org.kyantra.common.RoleEnum;
import org.kyantra.config.ConfigDAO;
import org.kyantra.core.actuator.ActuatorBean;
import org.kyantra.core.actuator.ActuatorDAO;
import org.kyantra.core.blockly.BlocklyBean;
import org.kyantra.core.blockly.BlocklyDAO;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.thing.ThingDAO;
import org.kyantra.core.user.UserBean;
import org.kyantra.exception.DataNotFoundException;
import org.kyantra.exception.ExceptionMessage;
import org.kyantra.core.auth.AuthorizationHelper;
import org.kyantra.annotation.Secure;
import org.kyantra.annotation.Session;
import org.kyantra.common.BaseResource;
import org.kyantra.services.ValidatorService;
import org.kyantra.utils.AwsIotHelper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.Set;
import java.util.UUID;

@Api(value = "")
public class ActuatorRuleResource extends BaseResource {

    /* TODO: Atomicity with AWS and DB
     * For transactions, like if DB transactions don't commit properly rollback the AWS operations too*/

    // TODO: 5/25/18 Change id path-param to form-param

    public ActuatorRuleResource(SecurityContext sc, HttpServletRequest request) {
        super(sc, request);
    }

    @POST
    @Path("/create/{id}")
    @Session
    @Secure(roles = {RoleEnum.WRITE, RoleEnum.ALL}, subjectType = "rule", subjectField = "parentId")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@PathParam("id") Integer parentThingId,
                         @FormParam("name") String name,
                         @FormParam("description") String description,
                         @FormParam("data") String data,
                         @FormParam("condition") String condition,
                         @FormParam("attribute") String attribute,
                         @FormParam("newValue") String newValue,
                         @FormParam("ruleIfXml") String ruleIfXml) {
        /*
         * Steps:
         * 1. create ActuatorBean
         * 2. create ActuatorAction in AWS (but actually lambda)
         * 3. create rule in AWS
         * 4. create RuleBean
         * 5. add rule to DB
         * 6. link rule and Actuator in DB
         * */

        ThingBean targetThing = ThingDAO.getInstance().get(parentThingId);
        UserBean user = (UserBean) getSecurityContext().getUserPrincipal();

        if (targetThing == null)
            throw  new DataNotFoundException(ExceptionMessage.DATA_NOT_FOUND);

        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            // create ActuatorBean
            ActuatorBean actuatorBean = new ActuatorBean();
            actuatorBean.setAttribute(attribute);
            // set parameters or revert to default
            actuatorBean.setNewValue(newValue);

            // create RuleBean
            RuleBean ruleBean = new RuleBean();
            ruleBean.setName(name);
            ruleBean.setDescription(description);
            ruleBean.setData(data);
            ruleBean.setCondition(condition);
            ruleBean.setType("Actuator");
            ruleBean.setParentThing(ThingDAO.getInstance().get(parentThingId));

            BlocklyBean blocklyIfXmlBean = new BlocklyBean();
            blocklyIfXmlBean.setXml(ruleIfXml);
            blocklyIfXmlBean.setParentThing(ThingDAO.getInstance().get(parentThingId));

            Set<ConstraintViolation<RuleBean>> constraintViolations = ValidatorService.getValidator().validate(ruleBean);

            System.out.println(constraintViolations);


            try {
                // create rule in AWS
                CreateTopicRuleResult ruleResult = RuleHelper.getInstance().createTopicRule(ruleBean, actuatorBean);

                // get the rule from AWS with for its ARN
                String ruleArn = RuleHelper.getInstance().getTopicRule("thing" + parentThingId + "_actuator_" + ruleBean.getName()).getRuleArn();

                // add trigger permission in lambda function (notificationService) for this rule
                AWSLambda lambda = AwsIotHelper.getAWSLambdaClient();
                String functionArn = ConfigDAO.getInstance().get("lambdaActuatorArn").getValue();
                lambda.addPermission(new AddPermissionRequest()
                        .withFunctionName(functionArn)
                        .withStatementId(UUID.randomUUID().toString())
                        .withPrincipal("iot.amazonaws.com")
                        .withSourceArn(ruleArn)
                        .withAction("lambda:InvokeFunction"));

                // add rule to DB
                RuleDAO.getInstance().add(ruleBean);

                // link rule and Actuator in DB
                actuatorBean.setParentRule(ruleBean);
                ActuatorDAO.getInstance().add(actuatorBean);

                // Get updated ruleBean
                ruleBean = RuleDAO.getInstance().get(ruleBean.getId());
                blocklyIfXmlBean.setBlockId(ruleBean.getId());
                BlocklyDAO.getInstance().add(blocklyIfXmlBean);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            return gson.toJson(ruleBean);
        }
        else throw new ForbiddenException(ExceptionMessage.FORBIDDEN);
    }


    @GET
    @Session
    @Secure(roles = {RoleEnum.READ, RoleEnum.WRITE, RoleEnum.ALL})
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("id") Integer ruleId) {
        RuleBean ruleBean = RuleDAO.getInstance().get(ruleId);

        ThingBean targetThing = ruleBean.getParentThing();
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();

        if (targetThing == null)
            throw new DataNotFoundException(ExceptionMessage.DATA_NOT_FOUND);

        if (!AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            return gson.toJson(ruleBean);
        }
        else throw new ForbiddenException(ExceptionMessage.FORBIDDEN);
    }


    @PUT
    @Path("/update/{id}")
    @Session
    @Secure(roles = {RoleEnum.ALL}, subjectType = "rule", subjectField = "parentId")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@PathParam("id") Integer ruleId,
                         @FormParam("description") String description,
                         @FormParam("data") String data,
                         @FormParam("condition") String condition,
                         @FormParam("parentThing") Integer parentThingId,
                         @FormParam("ruleIfXml") String ruleIfXml) {

        // create RuleBean
        RuleBean ruleBean = RuleDAO.getInstance().get(ruleId);

        ThingBean targetThing = ruleBean.getParentThing();
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();

        if (targetThing == null)
            throw new DataNotFoundException(ExceptionMessage.DATA_NOT_FOUND);

        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            ruleBean.setDescription(description);
            ruleBean.setData(data);
            ruleBean.setCondition(condition);

            ActuatorBean actuatorBean = ruleBean.getActuatorAction();

            // update rule in AWS
            RuleHelper.getInstance().replaceTopicRule(ruleBean, actuatorBean);

            // add rule to DB
            RuleDAO.getInstance().update(ruleBean);

            // link rule and Actuator in DB
            actuatorBean.setParentRule(ruleBean);
            ActuatorDAO.getInstance().add(actuatorBean);

            // Get updated ruleBean
            ruleBean = RuleDAO.getInstance().get(ruleBean.getId());

            BlocklyBean blocklyIfXmlBean = BlocklyDAO.getInstance().getByBlockId(ruleBean.getId());
            BlocklyDAO.getInstance().update(blocklyIfXmlBean.getId(),
                                    ruleBean.getId(),
                                    ruleIfXml);

            return gson.toJson(ruleBean);
        }
        else throw new ForbiddenException(ExceptionMessage.FORBIDDEN);
    }


    /*TODO: Delete Actuator topics when there is no rule in AWS pointing to it*/
    @DELETE
    @Path("/delete/{id}")
    @Session
    @Secure(roles = {RoleEnum.WRITE, RoleEnum.ALL})
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@PathParam("id") Integer ruleId) {
        /*
         * Steps:
         * 1. Get ruleBean
         * 2. delete rule in AWS
         * 3. delete rule in DB which should also delete entries from Actuator
         * */

        // get ruleBean
        RuleBean ruleBean = RuleDAO.getInstance().get(ruleId);

        ThingBean targetThing = ruleBean.getParentThing();
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();

        if (targetThing == null)
            throw new DataNotFoundException(ExceptionMessage.DATA_NOT_FOUND);

        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            // delete rule in AWS
            DeleteTopicRuleResult deleteTopicRuleResult = RuleHelper.getInstance().deleteRule(ruleBean);

            BlocklyBean blocklyIfBean = BlocklyDAO.getInstance().getByBlockId(ruleBean.getId());
            BlocklyDAO.getInstance().delete(blocklyIfBean.getId());

            // delete rule bean which should also delete entries from Actuator
            RuleDAO.getInstance().delete(ruleId);
            return "{\"success\": true}";
        }
        else throw new ForbiddenException(ExceptionMessage.FORBIDDEN);
    }


    @DELETE
    @Path("/delete/")
    @Session
    @Secure(roles = {RoleEnum.WRITE, RoleEnum.ALL})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteByName(@NotNull @FormParam("name") String ruleName,
                               @NotNull @FormParam("parentThing") Integer Id) {
        /*
         * Steps:
         * 1. Get ruleBean
         * 2. delete rule in AWS
         * 3. delete rule in DB which should also delete entries from Actuator
         * */

        // get ruleBean
        RuleBean ruleBean = RuleDAO.getInstance().getByName(ruleName);

        ThingBean targetThing = ruleBean.getParentThing();
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();

        if (targetThing == null)
            throw new DataNotFoundException(ExceptionMessage.DATA_NOT_FOUND);

        if (AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            // delete rule in AWS
            DeleteTopicRuleResult deleteTopicRuleResult = RuleHelper.getInstance().deleteRule(ruleBean);

            BlocklyBean blocklyIfBean = BlocklyDAO.getInstance().getByBlockId(ruleBean.getId());
            BlocklyDAO.getInstance().delete(blocklyIfBean.getId());
            // delete rule bean which should also delete entries from Actuator
            RuleDAO.getInstance().deleteByName(ruleName);
            return "{\"success\": true}";
        }
        else throw new ForbiddenException(ExceptionMessage.FORBIDDEN);
    }

    @GET
    @Path("/thing/{id}")
    @Session
    @Secure(roles = {RoleEnum.READ, RoleEnum.WRITE, RoleEnum.ALL})
    @Produces(MediaType.APPLICATION_JSON)
    public String getByThing(@PathParam("id") Integer parentThingId) {
        ThingBean targetThing = ThingDAO.getInstance().get(parentThingId);
        UserBean user = (UserBean)getSecurityContext().getUserPrincipal();

        if (targetThing == null)
            throw new DataNotFoundException(ExceptionMessage.DATA_NOT_FOUND);

        if(AuthorizationHelper.getInstance().checkAccess(user, targetThing)) {
            Set<RuleBean> ruleBean = RuleDAO.getInstance().getByThing(parentThingId);
            return gson.toJson(ruleBean);
        }
        else throw new ForbiddenException(ExceptionMessage.FORBIDDEN);
    }
}
