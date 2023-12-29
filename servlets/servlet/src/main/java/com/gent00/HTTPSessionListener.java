package com.gent00;

import javax.ejb.EJB;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.sql.SQLException;

public class HTTPSessionListener implements HttpSessionListener {

    @EJB
    HTTPSessionBean sessionBean;
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSessionListener.super.sessionCreated(se);
        System.out.println("Session created for " + se.getSession().getId());
//        try {
//            sessionBean.createSession(se.getSession().getId());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSessionListener.super.sessionDestroyed(se);
        System.out.println("Session destroyed for " + se.getSession().getId());
        try {
            sessionBean.deleteSession(se.getSession().getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
