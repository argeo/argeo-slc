package org.argeo.slc.akb.ui;

import java.util.HashMap;
import java.util.Map;

import org.argeo.slc.akb.AkbTypes;

// TODO implement i18n
public class AkbMessages {

	// Shortcut to provide a label for each nodeType
	public final static Map<String, String> typeLabels = new HashMap<String, String>() {
		private static final long serialVersionUID = 6790463815849374432L;

		{
			put(AkbTypes.AKB_ENV_TEMPLATE, "Template environment");
			put(AkbTypes.AKB_ENV, "Active environment");
			put(AkbTypes.AKB_CONNECTOR_FOLDER, "Connector folder");
			put(AkbTypes.AKB_CONNECTOR, "Connector");
			put(AkbTypes.AKB_CONNECTOR_ALIAS, "Connector alias");
			put(AkbTypes.AKB_SSH_CONNECTOR, "SSH connector");
			put(AkbTypes.AKB_JDBC_CONNECTOR, "JDBC connector");
			put(AkbTypes.AKB_JCR_CONNECTOR, "JCR connector");
			put(AkbTypes.AKB_ITEM_FOLDER, "Item folder");
			put(AkbTypes.AKB_ITEM, "Item");
			put(AkbTypes.AKB_SSH_FILE, "SSH file");
			put(AkbTypes.AKB_SSH_COMMAND, "SSH command");
			put(AkbTypes.AKB_JDBC_QUERY, "JDBC query");
			put(AkbTypes.AKB_NOTE, "Note");
		}
	};

	public final static String getLabelForType(String nodeType) {
		return typeLabels.get(nodeType);
	}

}
