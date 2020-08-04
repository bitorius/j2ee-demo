package com.gent00;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ThreadBean {


    //    @Resource(name = "threading/mtf")
    private ManagedThreadFactory threadFactory;

//    @Resource(lookup = "java:jboss/ee/concurrency/executor/default")


    private ManagedExecutorService managedExecutorService;

    //    @Resource(lookup = "java:jboss/ee/concurrency/scheduler/default")
    @Resource
    private ManagedScheduledExecutorService managedScheduledExecutorService;

    @EJB
    private HashOperation hashOperation;


    public String twiddle() {
        return "Hello Thread Bean!";
    }


    public void showManagedExecutorService() {
    }

    public String spinThreads(final int threads, final int hashsPerThread, boolean useEJB) throws ExecutionException, InterruptedException {
        System.out.println(threadFactory);

        final class HashCallable implements Callable<Long> {
            private ManagedThreadFactory mtf;
            private boolean useEJB = false;

            public HashCallable(ManagedThreadFactory threadFactory, HashOperation hashOperation, boolean useEJB) {
                this.mtf = threadFactory;
                this.hashOperation = hashOperation;
                this.useEJB = useEJB;
                if (!useEJB) {
                    this.hashUtil = new HashUtil();
                }

            }

            public long getExecuteStart() {
                return executeStart;
            }

            private long executeStart = -1;

            HashOperation hashOperation;
            HashUtil hashUtil;

            @Override
            public Long call() throws Exception {
                boolean isStartSet = false;
                System.out.println("Running Task " + this.toString() + " at " + System.currentTimeMillis() + " with " + hashOperation);


                try {
                    for (int x = 0; x < hashsPerThread; x++) {
                        if (useEJB) {
                            hashOperation.generateHash("HelloWorld");
                            if (!isStartSet) {
                                executeStart = System.currentTimeMillis();
                                isStartSet=true;
                            }
                        } else {
                            hashUtil.generateHash("HelloWorld");
                            if (!isStartSet) {
                                executeStart = System.currentTimeMillis();
                                isStartSet=true;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return executeStart;
            }
        }

        Map<HashCallable, Long[]> thread2Schedule = new HashMap<>();
        List<Future<Long>> futures = new ArrayList<>();

        for (int x = 0; x < threads; x++) {
            HashCallable htr = new HashCallable(threadFactory, hashOperation, useEJB);
            long scheduleTime = System.currentTimeMillis();
            futures.add(managedScheduledExecutorService.schedule(htr, 0, TimeUnit.SECONDS));
            thread2Schedule.put(htr, new Long[]{scheduleTime, 0L});
            System.out.println("Scheduled Task " + htr.toString() + " at " + scheduleTime);
        }

        System.out.println("Waiting for threads to finish hashing");
        for (Future future : futures) {
            future.get();
        }

        for (HashCallable htr : thread2Schedule.keySet()) {
            long execute = htr.getExecuteStart();
            Long[] times = thread2Schedule.get(htr);
            times[1] = execute;
            thread2Schedule.put(htr, times);
        }


        StringBuilder builder = new StringBuilder();
        for (HashCallable key : thread2Schedule.keySet()) {
            long alpha = thread2Schedule.get(key)[0];
            long omega = thread2Schedule.get(key)[1];
            builder.append(key + " -> " + Arrays.toString(thread2Schedule.get(key)) + "-> " + (omega - alpha) + "\n");
        }

        long totalDelayInMs = 0;
        for (HashCallable key : thread2Schedule.keySet()) {
            long alpha = thread2Schedule.get(key)[0];
            long omega = thread2Schedule.get(key)[1];
            totalDelayInMs += omega - alpha;
        }
        builder.append("\n\nTotal Skew -> " + totalDelayInMs);
        return builder.toString();
    }
}
