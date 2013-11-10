package org.argeo.slc.akb.ui;

import org.eclipse.swt.graphics.Image;

/** Shared icons. */
public class AkbImages {

	public final static Image LOGO_SMALL = AkbUiPlugin.getImageDescriptor(
			"icons/smallerOrnamentLogo.png").createImage();

	public final static Image TEMPLATE = AkbUiPlugin.getImageDescriptor(
			"icons/template.gif").createImage();
	public final static Image CONNECTOR_FOLDER = AkbUiPlugin
			.getImageDescriptor("icons/connectors.gif").createImage();

	public final static Image ITEM_FOLDER = AkbUiPlugin.getImageDescriptor(
			"icons/itemFolder.gif").createImage();
	public final static Image JDBC_CONNECTOR = AkbUiPlugin.getImageDescriptor(
			"icons/jdbcConnector.gif").createImage();
	public final static Image JDBC_QUERY = AkbUiPlugin.getImageDescriptor(
			"icons/jdbcQuery.gif").createImage();

	public final static Image SSH_CONNECTOR = AkbUiPlugin.getImageDescriptor(
			"icons/sshConnector.png").createImage();
	public final static Image SSH_COMMAND = AkbUiPlugin.getImageDescriptor(
			"icons/sshCommand.png").createImage();
	public final static Image SSH_FILE = AkbUiPlugin.getImageDescriptor(
			"icons/sshFile.gif").createImage();
}
