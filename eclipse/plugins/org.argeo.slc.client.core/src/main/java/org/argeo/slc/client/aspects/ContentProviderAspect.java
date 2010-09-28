package org.argeo.slc.client.aspects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Aspect
public class ContentProviderAspect {

	private final static Log log = LogFactory
			.getLog(ContentProviderAspect.class);

	private SessionFactory sessionFactory;
	private HibernateTransactionManager transactionManager;

	// Advices
	@Around("execution (* org.argeo.slc.client.core.*.get*(Object, int))")
	public Object aroundGetVariable(ProceedingJoinPoint thisJoinPoint)
			throws Throwable {

		// log.debug("***************** IN THE ASPECT. Before proceed");
		// log.debug("We have an open session : "
		// + sessionFactory.getCurrentSession().isOpen());
		// log.debug("Current transaction is active : "
		// + sessionFactory.getCurrentSession().getTransaction()
		// .isActive());

		Object o = thisJoinPoint.getArgs()[0];
		sessionFactory.getCurrentSession().beginTransaction();
		// reassociate a transient instance with a session (LockMode.NONE).
		sessionFactory.getCurrentSession().lock(o, LockMode.NONE);
		Object result = thisJoinPoint.proceed();
		sessionFactory.getCurrentSession().getTransaction().commit();

		// log.debug("**************** IN THE ASPECT. After proceed");
		return result;
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
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setTransactionManager(
			PlatformTransactionManager transactionManager) {
		if (transactionManager instanceof HibernateTransactionManager)
			this.transactionManager = (HibernateTransactionManager) transactionManager;
	}
}
