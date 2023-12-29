package com.gent00;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import javax.servlet.*;
import java.io.IOException;

public class CookieFilter implements Filter {
    HazelcastInstance hz = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.setClusterName("dev");
//        clientConfig.getNetworkConfig().addAddress("localhost");
//        HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig);

        HazelcastInstance hz = Hazelcast.newHazelcastInstance();

        this.hz=hz;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(hz!=null) {
            if (hz.getMap("samplemap") == null || hz.getMap("samplemap").isEmpty()) {
                hz.getMap("samplemap").put("SHAREKEY", 1);
            } else {
                int currentValue = Integer.parseInt(hz.getMap("samplemap").get("SHAREKEY").toString());
                hz.getMap("samplemap").put("SHAREKEY", currentValue + 1);
            }
            hz.getMap("samplemap").forEach((o, o2) -> System.out.println(o + "\t=>\t" + o2));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
