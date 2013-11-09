package org.argeo.slc.akb.ui.composites;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;

import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ItemTemplateTitleComposite extends Composite {
	// private final static Log log =
	// LogFactory.getLog(MixTitleComposite.class);

	private final Node akbNode;
	private final FormToolkit toolkit;
	private final IManagedForm form;
	// Don't forget to unregister on dispose
	private AbstractFormPart formPart;

	// To enable set focus
	private Text titleTxt;

	public ItemTemplateTitleComposite(Composite parent, int style,
			FormToolkit toolkit, IManagedForm form, Node akbNode) {
		super(parent, style);
		this.akbNode = akbNode;
		this.toolkit = toolkit;
		this.form = form;
		populate();
		toolkit.adapt(this);
	}

	private void populate() {
		// Initialization
		Composite parent = this;

		parent.setLayout(new GridLayout(4, false));

		// first line: Item name
		toolkit.createLabel(parent, "Name");
		titleTxt = toolkit.createText(parent, "", SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		titleTxt.setLayoutData(gd);

		// Second line: alias management
		toolkit.createLabel(parent, "Alias");
		final Combo typeCmb = new Combo(parent, SWT.READ_ONLY);

		// TODO enable live refresh
		final List<Node> definedAliases = AkbJcrUtils
				.getDefinedAliasForNode(akbNode);

		final String[] names = new String[definedAliases.size()];
		int i = 0;
		for (Node node : definedAliases)
			names[i++] = AkbJcrUtils.get(node, Property.JCR_TITLE);
		typeCmb.setItems(names);

		gd = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		typeCmb.setLayoutData(gd);

		// 3rd line: description
		Label lbl = toolkit.createLabel(parent, "Description");
		lbl.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text descTxt = toolkit.createText(parent, "", SWT.BORDER
				| SWT.MULTI | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		descTxt.setLayoutData(gd);

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(titleTxt, akbNode,
						Property.JCR_TITLE);
				AkbUiUtils.refreshFormTextWidget(descTxt, akbNode,
						Property.JCR_DESCRIPTION);

				// TODO implement this
				// typeCmb.select(getCurrTypeIndex());
				typeCmb.setEnabled(AkbJcrUtils.isNodeCheckedOutByMe(akbNode));
			}
		};

		// Listeners
		AkbUiUtils.addTextModifyListener(titleTxt, akbNode, Property.JCR_TITLE,
				part);
		AkbUiUtils.addTextModifyListener(descTxt, akbNode,
				Property.JCR_DESCRIPTION, part);

		typeCmb.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				// try {
				int oldIndex = -1; // getCurrTypeIndex();
				int selIndex = typeCmb.getSelectionIndex();

				// insure something has really been modified
				if (selIndex < 0 || oldIndex == selIndex)
					return;

				// TODO set new alias
				part.markDirty();
				// } catch (RepositoryException e) {
				// throw new AkbException(
				// "Error while updating connector alias", e);
				// }
			}
		});

		form.addPart(part);
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
