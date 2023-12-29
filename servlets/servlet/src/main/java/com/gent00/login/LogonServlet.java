package com.gent00.login;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class LogonServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getOutputStream().println("<html><body>");
        String user = req.getParameter("user");
        if(user!=null && !user.isEmpty()) {
            HttpSession s = req.getSession(true);
//            s.setAttribute("DTTM",System.currentTimeMillis());
            s.setAttribute("USER",user);

            resp.getOutputStream().println("Session Stored");
        }else{
            resp.getOutputStream().println("You don't want to login?");
        }
        resp.getOutputStream().println("</body></html>");
        resp.getOutputStream().flush();

    }
}
