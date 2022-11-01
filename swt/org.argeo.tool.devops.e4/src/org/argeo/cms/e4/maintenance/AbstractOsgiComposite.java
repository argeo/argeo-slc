package org.argeo.cms.e4.maintenance;

import java.util.Collection;

import org.argeo.api.cms.CmsLog;
import org.argeo.cms.swt.CmsSwtUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

abstract class AbstractOsgiComposite extends Composite {
	private static final long serialVersionUID = -4097415973477517137L;
	protected final BundleContext bc = FrameworkUtil.getBundle(getClass()).getBundleContext();
	protected final CmsLog log = CmsLog.getLog(getClass());

	public AbstractOsgiComposite(Composite parent, int style) {
		super(parent, style);
		parent.setLayout(CmsSwtUtils.noSpaceGridLayout());
		setLayout(CmsSwtUtils.noSpaceGridLayout());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		initUi(style);
	}

	protected abstract void initUi(int style);

	protected <T> T getService(Class<? extends T> clazz) {
		return bc.getService(bc.getServiceReference(clazz));
	}

	protected <T> Collection<ServiceReference<T>> getServiceReferences(Class<T> clazz, String filter) {
		try {
			return bc.getServiceReferences(clazz, filter);
		} catch (InvalidSyntaxException e) {
			throw new IllegalArgumentException("Filter " + filter + " is invalid", e);
		}
	}
}
