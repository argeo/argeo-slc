package org.argeo.slc.client.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;

/**
 * Intercepts all calls to get methods of the
 * org.argeo.slc.client.contentprovider package to insure that objects that are
 * to be rendered in views are correctly linked to the hibernate session.
 * 
 * @author bsinou
 * 
 */

@Aspect
public class ContentProviderAspect {

	// private final static Log log = LogFactory
	// .getLog(ContentProviderAspect.class);

	private SessionFactory sessionFactory;

	// Advices
	@Around("execution (* org.argeo.slc.client.contentprovider.*.get*(Object, int))")
	public Object aroundGetVariable(ProceedingJoinPoint thisJoinPoint)
			throws Throwable {

		Object o = thisJoinPoint.getArgs()[0];

		// TODO : find a mean to handle session & manager with txManager
		// in order to not have to re-begin a transaction here.
		sessionFactory.getCurrentSession().beginTransaction();
		
		// reassociate a transient instance with a session (LockMode.NONE).
		sessionFactory.getCurrentSession().lock(o, LockMode.NONE);
		
		Object result = thisJoinPoint.proceed();
		sessionFactory.getCurrentSession().getTransaction().commit();
		return result;
	}

	// IoC
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
