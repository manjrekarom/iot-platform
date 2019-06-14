package org.kyantra.core;

import org.glassfish.jersey.server.mvc.Template;
import org.kyantra.core.actuator.ActuatorDAO;
import org.kyantra.core.auth.RightsDAO;
import org.kyantra.core.sns.SnsBean;
import org.kyantra.core.actuator.ActuatorBean;
import org.kyantra.core.sns.SnsDAO;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.thing.ThingDAO;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.unit.UnitDAO;
import org.kyantra.core.user.UserBean;
import org.kyantra.exception.ExceptionMessage;
import org.kyantra.annotation.Session;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Context;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// chatbot imports
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.dialogflow.v2.*;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.google.protobuf.util.*;
import org.kyantra.common.BaseResource;

@Path("/")
public class HomeResource extends BaseResource {

    HomeResource(@Context SecurityContext sc,
                 @Context HttpServletRequest request) {
        super(sc, request);
    }

    @GET
    @Path("/session/status")
    @Session
    public String setSession(){
        return gson.toJson(getSecurityContext().getUserPrincipal());
    }


    @GET
    @Template(name = "/index.ftl")
    @Session
    public Map<String, Object> index() throws URISyntaxException {
        final Map<String, Object> map = new HashMap<>();
        map.put("active","home");
        UserBean userBean = (UserBean) getSecurityContext().getUserPrincipal();
        Set<UnitBean> unitBeanList = RightsDAO.getInstance().getUnitsByUser(userBean);
        map.put("units",unitBeanList);
        setCommonData(map);
        return map;
    }

    // for chatbot ui
    @GET
    @Path("/oauth")
    @Template(name = "/auth/oauth.ftl")
    public Map<String, Object> oauth() {
        final Map<String, Object> map = new HashMap<String, Object>();
        return map;
    }

    @GET
    @Path("/chatbot")
    @Template(name = "/chatbot/chatbot.ftl")
    public Map<String, Object> chatbot() {
        final Map<String, Object> map = new HashMap<String, Object>();
        return map;
    }

    @POST
    @Path("/eyiot")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eyIOT(JsonNode data, @CookieParam("authorization") String token){
        if(token == null || token == "" || token.length()==0) {
            Response res = Response.status(Response.Status.OK).entity("{\"response\" : \"sign in required\" }").build();
            return res;
        }
        String str = data.get("str").asText();
        try(SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName session = SessionName.of("eyisa-44cfb", token);
            Builder textInput = TextInput.newBuilder().setText(str).setLanguageCode("en-US");
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            QueryResult queryResult = response.getQueryResult();
            String jsonString = JsonFormat.printer().print(queryResult);
            Response res = Response.status(Response.Status.OK).entity("{\"response\" : "+jsonString+"}").build();
            return res;
        }
        catch(Exception e) {
            System.out.println("error: "+e);
            Response res = Response.status(Response.Status.OK).entity("{\"response\" : \"i didn't get that !\" }").build();
            return res;
        }
    }

    @GET
    @Path("/signup")
    @Template(name = "/auth/signup.ftl")
    public Map<String, Object> signup() {
        final Map<String, Object> map = new HashMap<String, Object>();
        return map;
    }

    // TODO: 6/1/18 if user has logged in move him to HOME path
    @GET
    @Path("/login")
    @Template(name = "/auth/login.ftl")
    public Map<String, Object> login() throws URISyntaxException {
        UserBean userBean = (UserBean)getSecurityContext().getUserPrincipal();

        if (userBean != null)
            throw new RedirectionException(ExceptionMessage.PERMANENTLY_MOVED,
                    Response.Status.MOVED_PERMANENTLY.getStatusCode(),
                    new URI("/"));

        final Map<String, Object> map = new HashMap<String, Object>();
        return map;
    }

//    @GET
//    @Path("/units/list")
//    @Template(name = "/units/list.ftl")
//    @Session
//    public Map<String, Object> listUnits() {
//        final Map<String, Object> map = new HashMap<String, Object>();
//        map.put("active","unit");
//        setCommonData(map);
//        return map;
//    }

    // TODO: 6/1/18 Test if required?
//    @GET
//    @Path("/units/create")
//    @Template(name = "/units/create.ftl")
//    @Session
//    public Map<String, Object> createUnit(@QueryParam("id") Integer id) throws AccessDeniedException {
//        //TODO: required?
//        if (AuthorizationDAO.getInstance().ownsUnit((UserBean)getSecurityContext().getUserPrincipal(),UnitDAO.getInstance().get(id))) {
//            final Map<String, Object> map = new HashMap<String, Object>();
//            map.put("active", "unit");
//            if (id != null) {
//                map.put("unit", UnitDAO.getInstance().get(id));
//            }
//            setCommonData(map);
//            return map;
//        }
//        else{
//            throw new AccessDeniedException();
//        }
//    }

//    @GET
//    @Path("/things/list")
//    @Template(name = "/thing/list.ftl")
//    @Session
//    public Map<String, Object> listThings() {
//        final Map<String, Object> map = new HashMap<String, Object>();
//        map.put("active","thing");
//        setCommonData(map);
//        return map;
//    }

//    @GET
//    @Path("/things/create")
//    @Template(name = "/thing/create.ftl")
//    @Session
//    public Map<String, Object> createThing() {
//        final Map<String, Object> map = new HashMap<String, Object>();
//        map.put("active","thing");
//        setCommonData(map);
//        return map;
//    }


//    @GET
//    @Path("/rights/create")
//    @Template(name = "/rights/create.ftl")
//    @Session
//    public Map<String, Object> createRight() {
//        final Map<String, Object> map = new HashMap<String, Object>();
//        map.put("active","right");
//        setCommonData(map);
//        return map;
//    }


//    @GET
//    @Path("/rights/list")
//    @Template(name = "/rights/list.ftl")
//    @Session
//    public Map<String, Object> listRight() {
//        final Map<String, Object> map = new HashMap<String, Object>();
//        map.put("active","right");
//        setCommonData(map);
//        return map;
//    }


//    @GET
//    @Path("/users/create")
//    @Template(name = "/users/create.ftl")
//    @Session
//    public Map<String, Object> createUsers() {
//        final Map<String, Object> map = new HashMap<String, Object>();
//        map.put("active","user");
//        setCommonData(map);
//        return map;
//    }

//    @GET
//    @Path("/users/list")
//    @Template(name = "/users/list.ftl")
//    @Session
//    public Map<String, Object> listusers() {
//        final Map<String, Object> map = new HashMap<String, Object>();
//        map.put("active","user");
//        setCommonData(map);
//        return map;
//    }


    @GET
    @Path("/logout")
    @Session
    @Template(name = "index.ftl")
    public Map<String, Object> logout(@Context HttpServletRequest request) throws URISyntaxException {
        final Map<String, Object> map = new HashMap<String, Object>();
        throw new RedirectionException(ExceptionMessage.TEMP_REDIRECT,
                Response.Status.TEMPORARY_REDIRECT.getStatusCode(),
                new URI("/login"));
    }


    @GET
    @Path("/unauthorized")
    @Template(name = "/auth/unauthorized.ftl")
    @Session
    public Map<String, Object> unauthorized() throws URISyntaxException {
        final Map<String, Object> map = new HashMap<String, Object>();
        throw new WebApplicationException(Response.temporaryRedirect(new URI("/login")).cookie(new NewCookie("authorization","")).build());
    }

    private void setCommonData(Map<String, Object> map) {
        map.put("user",getSecurityContext().getUserPrincipal());
    }

    @GET
    @Path("/units/get/{id}")
    @Template(name = "/units/get.ftl")
    @Session
    public Map<String, Object> getUnit(@PathParam("id") Integer id) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active","unit");
        UnitBean unit = UnitDAO.getInstance().get(id);
        map.put("unit",unit);
        setCommonData(map);
        return map;
    }

    @GET
    @Path("/things/get/{id}")
    @Template(name = "/thing/get.ftl")
    @Session
    public Map<String, Object> getThing(@PathParam("id") Integer id) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active","thing");
        ThingBean thing = ThingDAO.getInstance().get(id);
        map.put("thing",thing);
        setCommonData(map);
        return map;
    }

    @GET
    @Path("/things/dashboard/{id}")
    @Template(name = "/thing/dashboard.ftl")
    @Session
    public Map<String, Object> getDashboard(@PathParam("id") Integer id) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active","thing");
        ThingBean thing = ThingDAO.getInstance().get(id);
        map.put("thing",thing);
        setCommonData(map);
        return map;
    }

    @GET
    @Path("/profile")
    @Template(name = "/profile/profile.ftl")
    @Session
    public Map<String, Object> profile() throws URISyntaxException {
        final Map<String, Object> map = new HashMap<>();
        map.put("active","profile");
        UserBean userBean = (UserBean) getSecurityContext().getUserPrincipal();
        //Set<UnitBean> unitBeanList = RightsDAO.getInstance().getUnitsByUser(userBean);
        map.put("user",userBean);
        setCommonData(map);
        return map;
    }

    @GET
    @Path("/rules/sns/{id}")
    @Template(name = "/rules/sns/get.ftl")
    @Session
    public Map<String, Object> getSnsRules(@PathParam("id") Integer id) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active","sns");
        SnsBean sns = SnsDAO.getInstance().get(id);
        map.put("sns", sns);
        setCommonData(map);
        return map;
    }

    @GET
    @Path("/rules/actuator/{id}")
    @Template(name = "/rules/actuator/get.ftl")
    @Session
    public Map<String, Object> getActuatorRules(@PathParam("id") Integer id) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active","actuator");
        ActuatorBean actuator = ActuatorDAO.getInstance().get(id);
        map.put("actuator", actuator);
        setCommonData(map);
        return map;
    }
}
