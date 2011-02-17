package org.argeo.slc.client.ui.dist.views;

import org.argeo.eclipse.ui.jcr.views.GenericJcrBrowser;

public class DistributionView extends GenericJcrBrowser {

	@Override
	protected int[] getWeights() {
		return new int[] { 60, 40 };
	}

}
