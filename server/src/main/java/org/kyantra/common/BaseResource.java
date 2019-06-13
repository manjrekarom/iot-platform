package org.kyantra.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;
import org.kyantra.services.HibernateService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class BaseResource {

    public static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static HibernateService service;

    @Context
    public SecurityContext sc;

    @Context
    public HttpServletRequest request;

    public BaseResource() {
        if(service==null) {
            service = HibernateService.getInstance();
        }
    }

    public BaseResource(SecurityContext sc, HttpServletRequest request) {
        if(service==null) {
            service = HibernateService.getInstance();
        }
        this.sc = sc;
        this.request = request;
    }

    public Session getSession(){
        return service.getSessionFactory().openSession();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public SecurityContext getSecurityContext() {
        return sc;
    }
}
