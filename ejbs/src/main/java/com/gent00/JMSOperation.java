package com.gent00;

import io.prometheus.client.Counter;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.*;
import java.util.concurrent.TimeUnit;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JMSOperation {

//    @Resource(lookup = "java:comp/env/concurrent/CustomMSES")
    @Resource(mappedName = "mses")
    public ManagedScheduledExecutorService managedScheduledExecutorService;


    @Resource(lookup = "jms/qcf")
    private QueueConnectionFactory connectionFactory;

    @Resource(lookup = "jms/queue")
    private Queue queue;
    final static Counter jms_send_requests = Counter.build()
            .name("jms_send")
            .help("Number of jms message puts").register();

    final static Counter jms_send_requests_commit = Counter.build()
            .name("jms_send_commit")
            .help("Number of jms message put commit").register();

    final static Counter jms_recv_requests = Counter.build()
            .name("jms_recv")
            .help("Number of jms message recvs").register();

    final static Counter jms_recv_requests_commit = Counter.build()
            .name("jms_recv_commit")
            .help("Number of jms message recv commit").register();

    public void scheduleSendMessage(String messageText, int messageCount, int commitSize) {
        int iterationsLeft = messageCount;
        while (iterationsLeft > 0) {
            JMSSender jmsSender = new JMSSender((iterationsLeft >= commitSize ? commitSize : iterationsLeft), messageText);
            managedScheduledExecutorService.schedule(jmsSender, 0L, TimeUnit.SECONDS);
            iterationsLeft -= commitSize;
        }
    }

    class JMSSender implements Runnable {
        public JMSSender(int messageCount, String messageText) {
            this.messageCount = messageCount;
            this.messageText = messageText;
        }

        UserTransaction utx;
        int messageCount;
        String messageText;

        @Override
        public void run() {
//            System.out.println("" + Thread.currentThread().getName() + " executing " + System.currentTimeMillis() + " to put " + messageCount);
            UserTransaction utx = null;
            try {
                InitialContext ic = null;
                ic = new InitialContext();
                utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                QueueConnection queueConnection = connectionFactory.createQueueConnection();
                Session session = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(queue);
                queueConnection.start();
                utx.begin();
                for (int i = 0; i < (messageCount); i++) {
                    TextMessage textMessage = session.createTextMessage(messageText);
                    producer.send(textMessage);
                    jms_send_requests.inc();
                }
                utx.commit();
                jms_send_requests_commit.inc();

            } catch (NamingException | NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                throw new RuntimeException(e);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }

        }
    }


    public void scheduleRecvMessage(int messageCount, int commitSize) {
        int iterationsLeft = messageCount;
        while (iterationsLeft > 0) {
            JMSReceiver jmsReceiver = new JMSReceiver((iterationsLeft >= commitSize ? commitSize : iterationsLeft));
            managedScheduledExecutorService.schedule(jmsReceiver, 0L, TimeUnit.SECONDS);
            iterationsLeft -= commitSize;
        }
    }

    class JMSReceiver implements Runnable {
        public JMSReceiver(int messageCount) {
            this.messageCount = messageCount;
        }

        UserTransaction utx;
        int messageCount;

        @Override
        public void run() {
//            System.out.println("" + Thread.currentThread().getName() + " executing " + System.currentTimeMillis() + " to recv " + messageCount);
            UserTransaction utx = null;
            try {
                InitialContext ic = null;
                ic = new InitialContext();
                utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                QueueConnection queueConnection = connectionFactory.createQueueConnection();
                Session session = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(queue);
                queueConnection.start();
                utx.begin();
                for (int i = 0; i < (messageCount); i++) {
                    consumer.receive(0);//At most, 5s
                    jms_recv_requests.inc();
                }
                utx.commit();
                jms_recv_requests_commit.inc();
            } catch (NamingException | NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                throw new RuntimeException(e);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }

        }
    }


//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    public void sendMessage(String messageText, int howMany) throws JMSException {
//        QueueConnection queueConnection = connectionFactory.createQueueConnection();
//        Session session = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
//        MessageProducer producer = session.createProducer(queue);
//        queueConnection.start();
//        for (int i = 0; i < howMany; i++) {
//            TextMessage textMessage = session.createTextMessage(messageText);
//            producer.send(textMessage);
//            jms_send_requests.inc();
//        }
//    }


}
