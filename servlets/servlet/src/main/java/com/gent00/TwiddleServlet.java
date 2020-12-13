package com.gent00;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@WebServlet("/report")
public class TwiddleServlet extends HttpServlet {

    @EJB
    ThreadBean threadBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String threads_str = req.getParameter("threads");
            String hashs_str = req.getParameter("hashes");
            int threads = 1, hashes = 1000;
            if (threads_str != null && hashs_str != null && !threads_str.isEmpty() && !hashs_str.isEmpty() ) {
                threads = Integer.parseInt(threads_str);
                hashes = Integer.parseInt(hashs_str);
            }

            long totalDuration = System.currentTimeMillis();
            resp.getOutputStream().println(threadBean.spinThreads(threads, hashes));
            resp.getOutputStream().println("\n\nTotal Duration to call: " + (System.currentTimeMillis() - totalDuration));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
