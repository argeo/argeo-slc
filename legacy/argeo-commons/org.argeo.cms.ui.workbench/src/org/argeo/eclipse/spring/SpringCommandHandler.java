package org.argeo.eclipse.spring;

import org.argeo.api.cms.CmsLog;
import org.argeo.eclipse.ui.EclipseUiException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.springframework.context.ApplicationContext;

/** Allows to declare Eclipse commands as Spring beans */
public class SpringCommandHandler implements IHandler {
	private final static CmsLog log = CmsLog
			.getLog(SpringCommandHandler.class);

	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	public void dispose() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String commandId = event.getCommand().getId();
		String bundleSymbolicName = commandId.substring(0,
				commandId.lastIndexOf('.'));
		try {
			if (log.isTraceEnabled())
				log.trace("Execute " + event + " via spring command handler "
						+ this);
			// TODO: make it more flexible and robust
			ApplicationContext applicationContext = ApplicationContextTracker
					.getApplicationContext(bundleSymbolicName);
			if (applicationContext == null)
				throw new EclipseUiException(
						"No application context found for "
								+ bundleSymbolicName);

			// retrieve the command via its id
			String beanName = event.getCommand().getId();

			if (!applicationContext.containsBean(beanName)) {
				if (beanName.startsWith(bundleSymbolicName))
					beanName = beanName
							.substring(bundleSymbolicName.length() + 1);
			}

			if (!applicationContext.containsBean(beanName))
				throw new ExecutionException("No bean found with name "
						+ beanName + " in bundle " + bundleSymbolicName);
			Object bean = applicationContext.getBean(beanName);

			if (!(bean instanceof IHandler))
				throw new ExecutionException("Bean with name " + beanName
						+ " and class " + bean.getClass()
						+ " does not implement the IHandler interface.");

			IHandler handler = (IHandler) bean;
			return handler.execute(event);
		} catch (Exception e) {
			// TODO: use eclipse error management
			// log.error(e);
			throw new ExecutionException("Cannot execute Spring command "
					+ commandId + " in bundle " + bundleSymbolicName, e);
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isHandled() {
		return true;
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}
