package org.argeo.slc.web.mvc;

import java.util.Locale;

import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

/**
 * Returns a MarshallerView based on the underlying marshaller. View name is the
 * model key of the marshaller view.
 */
public class MarshallerViewResolver extends AbstractCachingViewResolver {
	private final Marshaller marshaller;

	public MarshallerViewResolver(Marshaller marshaller) {
		super();
		this.marshaller = marshaller;
	}

	/**
	 * Caches a marshaller view.
	 * 
	 * @param viewName
	 *            can be null, default marshaller view behavior is then used
	 */
	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		return new MarshallerView(marshaller, viewName);
	}

}
