package org.argeo.slc.akb.ui.wizards;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** Creates a new active instance of an AKB env template */
public class CreateEnvInstanceWizard extends Wizard {
	// private final static Log log = LogFactory
	// .getLog(CreateEnvInstanceWizard.class);

	private Session session;
	private AkbService akbService;
	private Node createdNode;

	// pages
	private ChooseTemplatePage chooseTemplatePage;

	public CreateEnvInstanceWizard(AkbService akbService, Session session) {
		this.akbService = akbService;
		this.session = session;
	}

	@Override
	public void addPages() {
		chooseTemplatePage = new ChooseTemplatePage();
		addPage(chooseTemplatePage);
	}

	public Node getCreatedNode() {
		return createdNode;
	}

	@Override
	public boolean performFinish() {
		if (!canFinish())
			return false;
		try {
			createdNode = akbService.createActiveEnv(
					chooseTemplatePage.getTemplate(),
					chooseTemplatePage.getActiveEnvName(),
					chooseTemplatePage.getUseDefaultConnectors());

			return true;
		} catch (RepositoryException re) {
			throw new AkbException("Unable to create environment instance", re);
		}
	}

	public boolean canFinish() {
		if (chooseTemplatePage.getActiveEnvName() != null
				&& chooseTemplatePage.getTemplate() != null)
			return true;
		else
			return false;
	}

	// //////////////////////
	// Pages definition
	/**
	 * Displays a combo box that enables user to choose which action to perform
	 */
	private class ChooseTemplatePage extends WizardPage {
		private Text valueTxt;
		private Combo chooseTemplateCmb;
		private Button useDefaultConnChk;

		public ChooseTemplatePage() {
			super("Choose template");
			setTitle("Choose template.");
			setDescription("Define the new instance parameters");
		}

		@Override
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NO_FOCUS);
			GridLayout gl = new GridLayout(2, false);
			container.setLayout(gl);

			new Label(container, NONE).setText("Name");
			valueTxt = new Text(container, SWT.NONE);
			valueTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			valueTxt.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					//TODO implement here name validation.
					getWizard().getContainer().updateButtons();
				}
			});

			new Label(container, NONE).setText("Parent template");
			chooseTemplateCmb = new Combo(container, SWT.NO_FOCUS);
			chooseTemplateCmb.setItems(getTemplates());
			chooseTemplateCmb.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
					true, false));

			chooseTemplateCmb.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					getWizard().getContainer().updateButtons();
				}
			});

			new Label(container, SWT.SEPARATOR | SWT.SHADOW_OUT
					| SWT.HORIZONTAL).setLayoutData(new GridData(SWT.FILL,
					SWT.FILL, false, false, 3, 1));

			useDefaultConnChk = new Button(container, SWT.CHECK);
			useDefaultConnChk.setText("Import default connectors");

			setControl(container);
		}

		private String[] getTemplates() {
			List<Node> templates = AkbJcrUtils.getDefinedTemplate(session);
			String[] values = new String[templates.size()];
			int i = 0;
			for (Node node : templates) {
				values[i++] = AkbJcrUtils.get(node, Property.JCR_TITLE);
			}
			return values;
		}

		protected String getActiveEnvName() {
			return AkbJcrUtils.isEmptyString(valueTxt.getText()) ? null
					: valueTxt.getText();
		}

		protected Node getTemplate() {
			int index = chooseTemplateCmb.getSelectionIndex();
			if (index >= 0) {
				return AkbJcrUtils.getTemplateByName(session,
						chooseTemplateCmb.getItem(index));
			} else
				return null;
		}

		protected boolean getUseDefaultConnectors() {
			return useDefaultConnChk.getSelection();
		}
	}
}