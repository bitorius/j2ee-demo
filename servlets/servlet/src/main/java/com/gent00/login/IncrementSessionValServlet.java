package com.gent00.login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class IncrementSessionValServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getOutputStream().println("<html><body>Try POST instead!");
        resp.getOutputStream().println("</body></html>");
        resp.getOutputStream().flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(true);
        int cardinal = s.getAttribute("INC") != null ? Integer.valueOf(s.getAttribute("INC").toString()) : 0;
        System.out.println("INC.prev=" + cardinal);
        cardinal++;
        System.out.println("INC.next=" + cardinal);
        s.setAttribute("INC", Integer.toString(cardinal));
        resp.sendRedirect(req.getContextPath());
    }
}
