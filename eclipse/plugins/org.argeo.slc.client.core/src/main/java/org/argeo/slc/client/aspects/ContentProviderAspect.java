package org.argeo.slc.client.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ContentProviderAspect {

	// private final static Log log = LogFactory
	// .getLog(ContentProviderAspect.class);

	// Advices
	@Around("execution (* getLabel(..))")
	public Object aroundGetVariable(ProceedingJoinPoint thisJoinPoint)
			throws Throwable {
		//log.debug("***************** IN THE ASPECT. Before proceed");
		// log.debug("We have an open session : "
		// + sessionFactory.getCurrentSession().isOpen());
		// log.debug("Current transaction is active : "
		// + sessionFactory.getCurrentSession().getTransaction()
		// .isActive());

		Object o = thisJoinPoint.proceed();
		//log.debug("**************** IN THE ASPECT. After proceed");
		return o;
	}

	// @Before("methodeTrace()")
	// public void log(JoinPoint joinPoint) {
	// log.info("Before calling " + joinPoint.getSignature().getName());
	// log.debug("SessionFactory " + sessionFactory.toString());
	// }

	// @Around("methodeTrace()")
	// public void proxyCalling(JoinPoint joinPoint) {
	// log.info("*********** We Call the method "
	// + joinPoint.getSignature().getName());
	// }
	//
	// // Pointcuts
	// @Pointcut("execution(* org.argeo.slc.client.core.ProcessListTableContent.set*(..))")
	// public void methodeTrace() {
	// }

	// IoC
	// public void setSessionFactory(SessionFactory sessionFactory) {
	// this.sessionFactory = sessionFactory;
	// }
	//
	// public void setTransactionManager(
	// PlatformTransactionManager transactionManager) {
	// if (transactionManager instanceof HibernateTransactionManager)
	// this.transactionManager = (HibernateTransactionManager)
	// transactionManager;
	// }
}
