package org.kyantra.core;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/assistant")
public class AssistantHookResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String rePost(String jsonReq) throws UnirestException {
        System.out.println(jsonReq);
        HttpResponse<JsonNode> response = Unirest.post("http://127.0.0.1:8004/")
                .header("Content-Type", "application/json")
                .body(jsonReq)
                .asJson();
        System.out.println(response.getBody());
        return String.valueOf(response.getBody());
    }

}
