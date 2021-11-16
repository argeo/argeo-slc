package org.argeo.slc.client.ui.specific;

import org.argeo.slc.client.rap.SlcRapPlugin;

/**
 * Workaround a single sourcing problem:
 * 
 * final static variable are imported at compilation time (we build against RCP)
 * and we still must have a different ID (the prefix must be the PLUGIN ID) for
 * RAP and RCP in order to be able to inject beans in the corresponding commands 
 */
public class OpenJcrFileCmdId {

	public String getCmdId() {
		return SlcRapPlugin.PLUGIN_ID + ".openJcrFile";
	}
}
