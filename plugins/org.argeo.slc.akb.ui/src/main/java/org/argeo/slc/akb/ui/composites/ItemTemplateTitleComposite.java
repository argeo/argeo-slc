package org.argeo.slc.akb.ui.composites;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ItemTemplateTitleComposite extends Composite {
	// private final static Log log =
	// LogFactory.getLog(MixTitleComposite.class);

	private final AkbService akbService;
	private final Node envNode;
	private final Node itemNode;
	private final FormToolkit toolkit;
	private final IManagedForm form;
	// Don't forget to unregister on dispose
	private AbstractFormPart formPart;

	// To enable set focus
	private Text titleTxt;
	private Combo aliasCmb;

	private List<Node> definedAliases;

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
	public ItemTemplateTitleComposite(Composite parent, int style,
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

		parent.setLayout(new GridLayout(5, false));

		// first line: Item name
		toolkit.createLabel(parent, "Name");
		titleTxt = toolkit.createText(parent, "", SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1);
		titleTxt.setLayoutData(gd);

		// Second line: alias management
		toolkit.createLabel(parent, "Alias");
		aliasCmb = new Combo(parent, SWT.READ_ONLY);
		toolkit.adapt(aliasCmb, false, false);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		aliasCmb.setLayoutData(gd);

		final Link openAliasLk = new Link(parent, SWT.NONE);
		toolkit.adapt(openAliasLk, false, false);
		openAliasLk.setText("<a>Edit Alias</a>");
		openAliasLk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				int index = aliasCmb.getSelectionIndex();
				if (index != -1) {
					Node currAlias = definedAliases.get(index);
					String id = AkbJcrUtils.getIdentifierQuietly(currAlias);
					Map<String, String> params = new HashMap<String, String>();
					params.put(OpenAkbNodeEditor.PARAM_NODE_JCR_ID, id);
					params.put(OpenAkbNodeEditor.PARAM_CURR_ENV_JCR_ID,
							AkbJcrUtils.getIdentifierQuietly(envNode));

					CommandUtils.callCommand(OpenAkbNodeEditor.ID, params);
				} else
					MessageDialog.openError(getShell(), "Error",
							"No selected alias");
			}
		});

		// 3rd line: description
		Label lbl = toolkit.createLabel(parent, "Description");
		lbl.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text descTxt = toolkit.createText(parent, "", SWT.BORDER
				| SWT.MULTI | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		descTxt.setLayoutData(gd);

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(titleTxt, itemNode,
						Property.JCR_TITLE);
				AkbUiUtils.refreshFormTextWidget(descTxt, itemNode,
						Property.JCR_DESCRIPTION);

				refreshTypeCmbValues();
				aliasCmb.select(getCurrAliasIndex());
				aliasCmb.setEnabled(AkbJcrUtils.isNodeCheckedOutByMe(itemNode));
			}
		};

		// Listeners
		AkbUiUtils.addTextModifyListener(titleTxt, itemNode,
				Property.JCR_TITLE, part);
		AkbUiUtils.addTextModifyListener(descTxt, itemNode,
				Property.JCR_DESCRIPTION, part);

		aliasCmb.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				try {
					int oldIndex = getCurrAliasIndex();
					int selIndex = aliasCmb.getSelectionIndex();

					// insure something has really been modified
					if (selIndex < 0 || oldIndex == selIndex)
						return;

					// set new alias
					Node newAlias = definedAliases.get(selIndex);

					// Only relies on the alias
					itemNode.setProperty(AkbNames.AKB_USED_CONNECTOR,
							newAlias.getPath());
					part.markDirty();
				} catch (RepositoryException e) {
					throw new AkbException(
							"Error while updating connector alias", e);
				}
			}
		});

		form.addPart(part);
	}

	private void refreshTypeCmbValues() {
		List<Node> newAliases;
		try {
			newAliases = JcrUtils.nodeIteratorToList(akbService
					.getDefinedAliases(
							AkbJcrUtils.getCurrentTemplate(itemNode),
							AkbJcrUtils.getAliasTypeForNode(itemNode)));
		} catch (RepositoryException e) {
			throw new AkbException("Unable to get defined aliases for node "
					+ itemNode, e);
		}
		boolean hasChanged = false;
		// manually ckeck if something has changed
		if (definedAliases == null
				|| newAliases.size() != definedAliases.size())
			hasChanged = true;
		else {

			for (int i = 0; i < newAliases.size(); i++) {
				if (!newAliases.get(i).equals(definedAliases.get(i))) {
					hasChanged = true;
					break;
				}
			}
		}

		if (hasChanged) {
			definedAliases = newAliases;
			final String[] names = new String[definedAliases.size()];
			int i = 0;
			for (Node node : definedAliases)
				names[i++] = AkbJcrUtils.get(node, Property.JCR_TITLE);
			aliasCmb.setItems(names);
		}
	}

	/**
	 * Returns the index in definedAliases list of the CURRENT defined alias as
	 * set in the item node <CODE>AkbNames.AKB_USED_CONNECTOR</CODE> if defined,
	 * -1 otherwise
	 */
	private int getCurrAliasIndex() {
		try {
			if (itemNode.hasProperty(AkbNames.AKB_USED_CONNECTOR)) {
				String aliasPath = itemNode.getProperty(
						AkbNames.AKB_USED_CONNECTOR).getString();
				Node alias = itemNode.getSession().getNode(aliasPath);
				return aliasCmb.indexOf(alias.getProperty(Property.JCR_TITLE).getString());
			} else
				return -1;
		} catch (RepositoryException re) {
			throw new AkbException("Unable to retrieve current Alias", re);
		}
	}

	@Override
	public boolean setFocus() {
		return titleTxt.setFocus();
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
