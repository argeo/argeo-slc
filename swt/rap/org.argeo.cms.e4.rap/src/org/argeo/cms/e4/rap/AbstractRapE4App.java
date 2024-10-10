package org.argeo.cms.e4.rap;

import java.util.HashMap;
import java.util.Map;

import org.argeo.api.cms.CmsApp;
import org.argeo.api.cms.CmsLog;
import org.argeo.cms.swt.dialogs.CmsFeedback;
import org.eclipse.rap.e4.E4ApplicationConfig;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ExceptionHandler;
import org.eclipse.rap.rwt.client.WebClient;
import org.osgi.framework.BundleContext;

/** Base class for CMS RAP applications. */
public abstract class AbstractRapE4App implements ApplicationConfiguration {
	private final static CmsLog log = CmsLog.getLog(AbstractRapE4App.class);

	private String e4Xmi;
	private String path;
	private String lifeCycleUri = "bundleclass://org.argeo.cms.e4.rap/org.argeo.cms.e4.rap.CmsLoginLifecycle";

	private Map<String, String> baseProperties = new HashMap<String, String>();

	private BundleContext bundleContext;
	public final static String CONTEXT_NAME_PROPERTY = "contextName";
	private String contextName;

	/**
	 * To be overridden in order to add multiple entry points, directly or using
	 * {@link #addE4EntryPoint(Application, String, String, Map)}.
	 */
	protected void addEntryPoints(Application application) {
	}

	public void configure(Application application) {
		application.setExceptionHandler(new ExceptionHandler() {

			@Override
			public void handleException(Throwable throwable) {
				CmsFeedback.error("Unexpected RWT exception", throwable);
			}
		});

		if (e4Xmi != null) {// backward compatibility
			addE4EntryPoint(application, path, e4Xmi, getBaseProperties());
		} else {
			addEntryPoints(application);
		}
		log.info("Configured E4 web app /" + contextName);
	}

	protected Map<String, String> getBaseProperties() {
		return baseProperties;
	}

//	protected void addEntryPoint(Application application, E4ApplicationConfig config, Map<String, String> properties) {
//		CmsE4EntryPointFactory entryPointFactory = new CmsE4EntryPointFactory(config);
//		application.addEntryPoint(path, entryPointFactory, properties);
//		application.setOperationMode(OperationMode.SWT_COMPATIBILITY);
//	}

	protected void addE4EntryPoint(Application application, String path, String e4Xmi, Map<String, String> properties) {
		E4ApplicationConfig config = createE4ApplicationConfig(e4Xmi);
		CmsE4EntryPointFactory entryPointFactory = new CmsE4EntryPointFactory(config);
		application.addEntryPoint(path, entryPointFactory, properties);
		application.setOperationMode(OperationMode.SWT_COMPATIBILITY);
		log.debug("Added web entry point /" + contextName + path);
	}

	/**
	 * To be overridden for further configuration.
	 * 
	 * @see E4ApplicationConfig
	 */
	protected E4ApplicationConfig createE4ApplicationConfig(String e4Xmi) {
		return new E4ApplicationConfig(e4Xmi, lifeCycleUri, null, null, false, true, true);
	}

	@Deprecated
	public void setPageTitle(String pageTitle) {
		if (pageTitle != null)
			baseProperties.put(WebClient.PAGE_TITLE, pageTitle);
	}

	/** Returns a new map used to customise and entry point. */
	public Map<String, String> customise(String pageTitle) {
		Map<String, String> custom = new HashMap<>(getBaseProperties());
		if (pageTitle != null)
			custom.put(WebClient.PAGE_TITLE, pageTitle);
		return custom;
	}

	@Deprecated
	public void setE4Xmi(String e4Xmi) {
		this.e4Xmi = e4Xmi;
	}

	@Deprecated
	public void setPath(String path) {
		this.path = path;
	}

	public void setLifeCycleUri(String lifeCycleUri) {
		this.lifeCycleUri = lifeCycleUri;
	}

	protected BundleContext getBundleContext() {
		return bundleContext;
	}

	protected void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public void init(BundleContext bundleContext, Map<String, String> properties) {
		this.bundleContext = bundleContext;
		this.baseProperties = properties;
//		for (String key : properties.keySet()) {
//			Object value = properties.get(key);
//			if (value != null)
//				baseProperties.put(key, value);
//		}

		if (properties.containsKey(CmsApp.CONTEXT_NAME_PROPERTY)) {
			contextName = properties.get(CmsApp.CONTEXT_NAME_PROPERTY);
		} else {
			contextName = "<unknown context>";
		}
	}

	public void destroy(Map<String, Object> properties) {

	}
}
