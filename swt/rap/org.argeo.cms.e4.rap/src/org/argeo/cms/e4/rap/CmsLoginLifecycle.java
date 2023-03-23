package org.argeo.cms.e4.rap;

import java.security.AccessController;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.argeo.api.cms.CmsAuth;
import org.argeo.api.cms.CmsContext;
import org.argeo.api.cms.CmsLog;
import org.argeo.api.cms.ux.CmsImageManager;
import org.argeo.api.cms.ux.CmsView;
import org.argeo.api.cms.ux.UxContext;
import org.argeo.cms.CurrentUser;
import org.argeo.cms.swt.CmsSwtUtils;
import org.argeo.cms.swt.SimpleSwtUxContext;
import org.argeo.cms.swt.acr.AcrSwtImageManager;
import org.argeo.cms.swt.auth.CmsLoginShell;
import org.argeo.cms.swt.dialogs.CmsFeedback;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@SuppressWarnings("restriction")
public class CmsLoginLifecycle implements CmsView {
	private final static CmsLog log = CmsLog.getLog(CmsLoginLifecycle.class);

	@Inject
	private CmsContext cmsContext;
	
	private UxContext uxContext;
	private CmsImageManager imageManager;

	private LoginContext loginContext;
	private BrowserNavigation browserNavigation;

	private String state = null;
	private String uid;

	@PostContextCreate
	boolean login(final IEventBroker eventBroker) {
		uid = UUID.randomUUID().toString();
		browserNavigation = RWT.getClient().getService(BrowserNavigation.class);
		if (browserNavigation != null)
			browserNavigation.addBrowserNavigationListener(new BrowserNavigationListener() {
				private static final long serialVersionUID = -3668136623771902865L;

				@Override
				public void navigated(BrowserNavigationEvent event) {
					state = event.getState();
					if (uxContext != null)// is logged in
						stateChanged();
				}
			});

		Subject subject = Subject.getSubject(AccessController.getContext());
		Display display = Display.getCurrent();
//		UiContext.setData(CmsView.KEY, this);
		CmsLoginShell loginShell = new CmsLoginShell(this, cmsContext);
		CmsSwtUtils.registerCmsView(loginShell.getShell(), this);
		loginShell.setSubject(subject);
		try {
			// try pre-auth
			loginContext = new LoginContext(CmsAuth.LOGIN_CONTEXT_USER, subject, loginShell);
			loginContext.login();
		} catch (LoginException e) {
			loginShell.createUi();
			loginShell.open();

			while (!loginShell.getShell().isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
		if (CurrentUser.getUsername(getSubject()) == null)
			return false;
		uxContext = new SimpleSwtUxContext();
		imageManager = (CmsImageManager) new AcrSwtImageManager();

		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				startupComplete();
				eventBroker.unsubscribe(this);
			}
		});

		// lcs.changeApplicationLocale(Locale.FRENCH);
		return true;
	}

	@PreSave
	void destroy() {
		// logout();
	}

	@Override
	public UxContext getUxContext() {
		return uxContext;
	}

	@Override
	public void navigateTo(String state) {
		browserNavigation.pushState(state, state);
	}

	@Override
	public void authChange(LoginContext loginContext) {
		if (loginContext == null)
			throw new IllegalArgumentException("Login context cannot be null");
		// logout previous login context
		// if (this.loginContext != null)
		// try {
		// this.loginContext.logout();
		// } catch (LoginException e1) {
		// System.err.println("Could not log out: " + e1);
		// }
		this.loginContext = loginContext;
	}

	@Override
	public void logout() {
		if (loginContext == null)
			throw new IllegalStateException("Login context should not be null");
		try {
			CurrentUser.logoutCmsSession(loginContext.getSubject());
			loginContext.logout();
		} catch (LoginException e) {
			throw new IllegalStateException("Cannot log out", e);
		}
	}

	@Override
	public void exception(Throwable e) {
		String msg = "Unexpected exception in Eclipse 4 RAP";
		log.error(msg, e);
		CmsFeedback.error(msg, e);
	}

	@Override
	public CmsImageManager getImageManager() {
		return imageManager;
	}

	protected Subject getSubject() {
		return loginContext.getSubject();
	}

	@Override
	public boolean isAnonymous() {
		return CurrentUser.isAnonymous(getSubject());
	}

	@Override
	public String getUid() {
		return uid;
	}

	// CALLBACKS
	protected void startupComplete() {
	}

	protected void stateChanged() {

	}

	// GETTERS
	protected BrowserNavigation getBrowserNavigation() {
		return browserNavigation;
	}

	protected String getState() {
		return state;
	}

	@Override
	public <T> T doAs(Callable<T> action) {
		throw new UnsupportedOperationException();
	}

}
