package com.gent00;

import javax.ejb.EJB;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.transaction.Transactional;
import java.sql.SQLException;

public class HTTPSessionAttributeListener implements HttpSessionAttributeListener {

    @EJB
    HTTPSessionBean sessionBean;


    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String attributeName = event.getName();
        Object attributeValue = event.getValue();
        System.out.println("Attribute added : " + attributeName + " : " + attributeValue);
        String jsesh = event.getSession().getId();
        try {
            sessionBean.addAttribute(jsesh, attributeName, attributeValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        String attributeName = event.getName();
        Object attributeValue = event.getValue();
        String jsesh = event.getSession().getId();

        System.out.println("Attribute removed for " + event.getSession().getId() + ": " +attributeName + " : " + attributeValue);
        try {
            sessionBean.removeAttribute(jsesh, attributeName, attributeValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        String attributeName = event.getName();
        Object attributeValue = event.getSession().getAttribute(attributeName);
        String jsesh = event.getSession().getId();

        System.out.println("Attribute replaced for " + event.getSession().getId() + ": " + attributeName + " with " + attributeValue);
        try {
            sessionBean.replaceAttribute(jsesh, attributeName, attributeValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
