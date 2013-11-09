package org.argeo.slc.akb.ui.composites;

import javax.jcr.Node;
import javax.jcr.Property;

import org.argeo.slc.akb.ui.AkbUiUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class MixTitleComposite extends Composite {
	// private final static Log log =
	// LogFactory.getLog(MixTitleComposite.class);

	private final Node akbNode;
	private final FormToolkit toolkit;
	private final IManagedForm form;
	// Don't forget to unregister on dispose
	private AbstractFormPart formPart;

	// To enable set focus
	private Text titleTxt;

	public MixTitleComposite(Composite parent, int style, FormToolkit toolkit,
			IManagedForm form, Node akbNode) {
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

		parent.setLayout(new GridLayout(2, false));

		// first line: connector type and name
		toolkit.createLabel(parent, "Name");
		titleTxt = toolkit.createText(parent, "", SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		titleTxt.setLayoutData(gd);

		// 2nd line: description
		Label lbl = toolkit.createLabel(parent, "Description");
		lbl.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text descTxt = toolkit.createText(parent, "", SWT.BORDER
				| SWT.MULTI | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
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
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(titleTxt, akbNode, Property.JCR_TITLE,
				part);
		AkbUiUtils.addTextModifyListener(descTxt, akbNode,
				Property.JCR_DESCRIPTION, part);
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
