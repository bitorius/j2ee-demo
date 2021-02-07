package com.gent00;

import org.apache.commons.lang3.StringUtils;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TwiddleJMSServlet extends HttpServlet {

    @EJB
    JMSOperation jmsOperation;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String messageText = req.getParameter("messageText");
        String messageCountStr = req.getParameter("messageCount");
        String commitSizeStr = req.getParameter("commitSize");
        if (action.equals("send")) {
            //action=send?
            if (StringUtils.isEmpty(messageText)) {
                resp.getOutputStream().println("Please pass a messageText GET parameter");
            }

            int messageCount = 0;
            if (StringUtils.isEmpty(messageCountStr)) {
                resp.getOutputStream().println("Please pass a messageCountStr GET parameter. Max is " + Integer.MAX_VALUE);
            } else {
                messageCount = Integer.parseInt(messageCountStr);
            }

            int commitSize = 0;
            if (StringUtils.isEmpty(commitSizeStr)) {
                resp.getOutputStream().println("Please pass a commitSize GET parameter");
            } else {
                commitSize = Integer.parseInt(commitSizeStr);
            }
            jmsOperation.scheduleSendMessage(messageText, messageCount, commitSize);
            resp.getOutputStream().println("OK");
        } else if (action.equals("recv")) {
            //action=recv?

            int messageCount = 0;
            if (StringUtils.isEmpty(messageCountStr)) {
                resp.getOutputStream().println("Please pass a messageCountStr GET parameter. Max is " + Integer.MAX_VALUE);
            } else {
                messageCount = Integer.parseInt(messageCountStr);
            }

            int commitSize = 0;
            if (StringUtils.isEmpty(commitSizeStr)) {
                resp.getOutputStream().println("Please pass a commitSize GET parameter");
            } else {
                commitSize = Integer.parseInt(commitSizeStr);
            }
            jmsOperation.scheduleRecvMessage(messageCount,commitSize);
            resp.getOutputStream().println("OK");
        }
    }
}
