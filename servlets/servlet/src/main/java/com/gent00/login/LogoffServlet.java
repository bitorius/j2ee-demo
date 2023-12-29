package com.gent00.login;

import com.gent00.HTTPSessionBean;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;


public class LogoffServlet extends HttpServlet {
    @EJB
    HTTPSessionBean sessionBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getOutputStream().println("<html><body>");
        if (req.getSession(false) != null) {
            try {
                boolean isAbleToDelete = sessionBean.deleteSession(req.getSession(false).getId());

                if (isAbleToDelete){
                    resp.getOutputStream().println("Logged off " + req.getSession(false).getId());
                }else{
                    resp.getOutputStream().println("Not able to find a session or delete it");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        resp.getOutputStream().println("</body></html>");
        resp.getOutputStream().flush();

    }
}
