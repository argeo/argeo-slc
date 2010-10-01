package org.argeo.slc.client.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

	// We select here only getters from classes of the contentprovider package
	// that need to get data from hibernate

	// PointCuts
	@Pointcut("(execution (* org.argeo.slc.client.contentprovider.ProcessListTableLabelProvider.get*(..)) && args(o,..))"
			+ " || (execution (* org.argeo.slc.client.contentprovider.ProcessDetailContentProvider.get*(..)) && args(o,..))"
			+ " || (execution (* org.argeo.slc.client.contentprovider.ResultDetailContentProvider.get*(..)) && args(o,..))")
	void contentProviderGetterWrapper(Object o) {
	}

	// Advices
	@Around("contentProviderGetterWrapper(o)")
	public Object aroundGetWrapper(ProceedingJoinPoint thisJoinPoint, Object o)
			throws Throwable {

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
