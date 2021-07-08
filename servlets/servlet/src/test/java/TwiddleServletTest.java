import com.gent00.ThreadBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
//http://localhost:8080/servlet/Twiddle?threads=512&hashes=1000000&useEJB=true
//@RunWith(Arquillian.class)
//public class TwiddleServletTest {
//    @Deployment
//    public static JavaArchive createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class).addClass(ThreadBean.class).addPackage("com.gent00")
//                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//    }
//
//    @Inject
//    ThreadBean threadBean;
//
//    @Test
//    public void echo() throws Exception {
//        Assert.assertNotNull(threadBean);
//        System.out.println("Starting threads");
//        threadBean.spinThreads(256, 10000);
//        System.out.println("Ending threads");
//
//    }
//}
