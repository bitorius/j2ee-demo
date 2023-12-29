package com.gent00;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CookieMDBFilter implements Filter {

    @EJB
    HTTPSessionBean sessionBean;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestedSessionID = request.getRequestedSessionId();
        boolean sessionUnknownToJVM = true;//request.getSession(false) == null;

        if (!sessionUnknownToJVM) { //Session is able to be retrieved
            System.out.println("HTTP Session found on this instance");
        } else if
            //If previous sessionID exists, and we don't have a session, let's rehydrate if possible
        (requestedSessionID != null && !requestedSessionID.isEmpty() && sessionUnknownToJVM) {
            System.out.println("Created new session because an existing session ID cookie found for " + requestedSessionID);
            HttpSession session = ((HttpServletRequest) servletRequest).getSession();
            String newJSesh = session.getId();
            try {
                Map<String, Object> databaseSessionData = new HashMap<>();
                sessionBean.createSession(newJSesh, requestedSessionID, databaseSessionData);
                for (String key : databaseSessionData.keySet()) {
                    session.setAttribute(key, databaseSessionData.get(key));
                }
            } catch (SQLException e) {
                throw new EJBException(e);
            }
        }else{
            System.out.println("Creating blank session for subsequent usage " + request.getSession(true));
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
