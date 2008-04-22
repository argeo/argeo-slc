package argeo.slc.springtest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class MyBeanTest extends TestCase {
	private Log log = LogFactory.getLog(getClass());
	
	public void testMyBeanSimple() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"argeo/slc/springtest/applicationContext.xml");
		
		MyBean myBean = (MyBean)context.getBean("testBean");
		
		log.info("Retrieved bean from spring");
		log.debug("Debug some stuff");
		
		assertEquals("Gwen", myBean.getName());
		assertEquals(new Long(10), myBean.getValue());
	}

}
