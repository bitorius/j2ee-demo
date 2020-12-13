package com.gent00;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.concurrent.ManagedThreadFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ThreadBean {


    //    @Resource(lookup = "java:jboss/ee/concurrency/scheduler/default")
    @Resource
    public ManagedScheduledExecutorService managedScheduledExecutorService;

    //    @Resource(lookup = "java:jboss/ee/concurrency/executor/default")
    //    @Resource(name = "threading/mtf")
    private ManagedThreadFactory threadFactory;
    private ManagedExecutorService managedExecutorService;
    @EJB
    private HashOperation hashOperation;


    public String twiddle() {
        return "Hello Thread Bean!";
    }


    public void showManagedExecutorService() {
    }

    public String spinThreads(final int threads, final int hashsPerThread) throws ExecutionException, InterruptedException {
        System.out.println(threadFactory);

        final class HashCallable implements Callable<Long[]> {
            HashOperation hashOperation;
            private ManagedThreadFactory mtf;
            private long executeStart = -1, executeEnd = -1, ejbWait = -1;

            public HashCallable(ManagedThreadFactory threadFactory, HashOperation hashOperation) {
                this.mtf = threadFactory;
                this.hashOperation = hashOperation;


            }

            public long getExecuteStart() {
                return executeStart;
            }

            @Override
            public Long[] call() throws Exception {
                boolean isStartSet = false;
                System.out.println("Running Task " + this.toString() + " at " + System.currentTimeMillis() + " with " + hashOperation);
                executeStart = System.currentTimeMillis();
                try {
                    long ejbStart = System.currentTimeMillis();
                    hashOperation.generateHash("HelloWorld", hashsPerThread);
                    long ejbFinish = System.currentTimeMillis();
                    ejbWait = ejbFinish - ejbStart;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new Long[]{executeStart, System.currentTimeMillis(), ejbWait};//Start, End , Time to obtain EJB and generate Hash
            }
        }

        Map<Future<Long[]>, Long> futuresToScheduleTime = new HashMap<>();
        List<Future<Long[]>> futures = new ArrayList<>();

        for (int x = 0; x < threads; x++) {
            HashCallable hashCallable = new HashCallable(threadFactory, hashOperation);
            long scheduleTime = System.currentTimeMillis();
            Future<Long[]> future = managedScheduledExecutorService.schedule(hashCallable, 0, TimeUnit.SECONDS);
            futures.add(future);
            futuresToScheduleTime.put(future, scheduleTime);
            System.out.println("Scheduled Task " + hashCallable.toString() + " at " + scheduleTime);
        }

        System.out.println("Waiting for threads to finish hashing.");
        for (Future future : futures) {
            future.get();
        }
        System.out.println("Threads have completed.");


        StringBuilder builder = new StringBuilder();
        for (Future<Long[]> future : futuresToScheduleTime.keySet()) {
            long executeStart = future.get()[0];
            long executeFinish = future.get()[1];
            long ejbWait = future.get()[2];
            long scheduleTime = futuresToScheduleTime.get(future);

            builder.append("Future ").append(future.hashCode()).append("> ").append(executeStart).append(" -> ")
                    .append(executeFinish).append("; ").append("ejbWait+Compute=").append(ejbWait).append(", scheduleDelayToExecute=").append(executeStart - scheduleTime).append("\n\n");
        }

        return builder.toString();
    }
}
