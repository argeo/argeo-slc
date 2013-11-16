package org.argeo.slc.akb.ui.composites;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.ui.commands.ForceRefresh;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ActiveItemHeaderComposite extends Composite {

	private final AkbService akbService;
	private final Node envNode;
	private final Node itemNode;
	private final FormToolkit toolkit;
	private final IManagedForm form;
	// Don't forget to unregister on dispose
	private AbstractFormPart formPart;

	/**
	 * 
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param form
	 * @param envNode
	 * @param itemNode
	 * @param akbService
	 */
	public ActiveItemHeaderComposite(Composite parent, int style,
			FormToolkit toolkit, IManagedForm form, Node envNode,
			Node itemNode, AkbService akbService) {
		super(parent, style);
		this.envNode = envNode;
		this.itemNode = itemNode;
		this.toolkit = toolkit;
		this.form = form;
		this.akbService = akbService;
		populate();
		toolkit.adapt(this);
	}

	private void populate() {
		// Initialization
		Composite parent = this;

		parent.setLayout(new GridLayout(3, false));

		final Label envLbl = toolkit.createLabel(parent, "");

		final Link editActiveConnLk = new Link(parent, SWT.NONE);
		toolkit.adapt(editActiveConnLk, false, false);
		editActiveConnLk.setText("<a>Edit Connector</a>");
		editActiveConnLk.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true,
				false));

		final Link refreshLk = new Link(parent, SWT.NONE);
		toolkit.adapt(refreshLk, false, false);
		refreshLk.setText("<a>Refresh</a>");

		new Label(parent, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL)
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
						3, 1));

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				envLbl.setText("Environment: "
						+ AkbJcrUtils.get(envNode, Property.JCR_TITLE)
						+ " - Connector:"
						+ AkbJcrUtils.get(itemNode, Property.JCR_TITLE));

				// Node activeConnector =
				// akbService.getActiveConnectorByAlias(envNode,
				// AkbJcrUtils.get(itemNode,
				// AkbNames.AKB_CONNECTOR_ALIAS_PATH));
				// if
				// (AkbJcrUtils.isEmptyString(AkbJcrUtils.get(activeConnector,
				// AkbNames.AKB_CONNECTOR_URL)))
				// conLbl.setImage(SWT.);
			}
		};

		// Listeners
		editActiveConnLk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				try {
					String pathId = AkbJcrUtils.get(itemNode,
							AkbNames.AKB_USED_CONNECTOR);

					Node activeConnector = akbService
							.getActiveConnectorByAlias(envNode, pathId);

					String id = AkbJcrUtils
							.getIdentifierQuietly(activeConnector);
					Map<String, String> params = new HashMap<String, String>();
					params.put(OpenAkbNodeEditor.PARAM_NODE_JCR_ID, id);
					params.put(OpenAkbNodeEditor.PARAM_CURR_ENV_JCR_ID,
							AkbJcrUtils.getIdentifierQuietly(envNode));

					CommandUtils.callCommand(OpenAkbNodeEditor.ID, params);
				} catch (RepositoryException e) {
					throw new AkbException("Error opening active connector", e);
				}
			}
		});

		refreshLk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				CommandUtils.callCommand(ForceRefresh.ID);
			}
		});
		form.addPart(part);
	}

	@Override
	public boolean setFocus() {
		return true;
	}

	protected void disposePart(AbstractFormPart part) {
		if (part != null) {
			form.removePart(part);
			part.dispose();
		}
	}

	@Override
	public void dispose() {
		disposePart(formPart);
		super.dispose();
	}
}