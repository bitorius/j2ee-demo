package com.gent00;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/eethread")
public class TwiddleEEThread extends HttpServlet {

    @Resource
    private ManagedThreadFactory threadFactory;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            List<Thread> threads = new ArrayList();
            while (true) {
                Thread t = threadFactory.newThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if (t != null) {
                    t.start();
                    System.out.println("Starting " + t.getName());
                    threads.add(t);
                } else {
                    break;
                }
            }
            for (Thread x : threads) {
//            x.interrupt();
                try {
                    x.join();
                    System.out.println("Joined " + x.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        resp.getOutputStream().println(threads.size() + " at " + System.currentTimeMillis());
        }

}
