package org.argeo.slc.ide.ui.launch.osgi;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** Main tab of OSGiBoot launch configuration UI. */
public class OsgiBootMainTab extends AbstractLaunchConfigurationTab implements
		OsgiLauncherConstants {
	private Listener listener = new Listener();

	private Button syncBundles;
	private Button clearDataDirectory;

	private Button addJvmPaths;
	private Text additionalVmArgs;

	private Text additionalProgramArgs;

	private final Boolean isEclipse;

	public OsgiBootMainTab(Boolean isEclipse) {
		super();
		this.isEclipse = isEclipse;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createGeneral(container);
		createAdditionalProgramArgs(container);
		createAdditionalVmArgumentBlock(container);
		Dialog.applyDialogFont(container);
		setControl(container);
	}

	protected void createGeneral(Composite parent) {
		Group container = new Group(parent, SWT.NONE);
		container.setText("General");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		syncBundles = new Button(container, SWT.CHECK);
		syncBundles.addSelectionListener(listener);
		new Label(container, SWT.NONE)
				.setText("Keep bundles in line with target platform and workspace (recommended)");
		clearDataDirectory = new Button(container, SWT.CHECK);
		clearDataDirectory.addSelectionListener(listener);
		new Label(container, SWT.NONE)
				.setText("Clear data directory before launch");
	}

	protected void createAdditionalProgramArgs(Composite parent) {
		Group container = new Group(parent, SWT.NONE);
		container.setText("Additional Program Arguments");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		additionalProgramArgs = new Text(container, SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 60;
		gd.widthHint = 100;
		gd.horizontalSpan = 2;
		additionalProgramArgs.setLayoutData(gd);
		additionalProgramArgs.addModifyListener(listener);
	}

	protected void createAdditionalVmArgumentBlock(Composite parent) {
		Group container = new Group(parent, SWT.NONE);
		container.setText("Additional VM Arguments");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		additionalVmArgs = new Text(container, SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 60;
		gd.widthHint = 100;
		gd.horizontalSpan = 2;
		additionalVmArgs.setLayoutData(gd);
		additionalVmArgs.addModifyListener(listener);

		addJvmPaths = new Button(container, SWT.CHECK);
		addJvmPaths.addSelectionListener(listener);
		new Label(container, SWT.NONE)
				.setText("Add workspace JVM paths as non-standard system properties");

	}

	public String getName() {
		return "OSGi Boot";
	}

	@Override
	public Image getImage() {
		return SlcIdeUiPlugin.getDefault().getImage("icons/slc-launch.gif");
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		// System.out.println("initializeFrom");
		try {
			syncBundles.setSelection(configuration.getAttribute(
					ATTR_SYNC_BUNDLES, true));
			clearDataDirectory.setSelection(configuration.getAttribute(
					ATTR_CLEAR_DATA_DIRECTORY, false));

			additionalProgramArgs.setText(configuration.getAttribute(
					ATTR_ADDITIONAL_PROGRAM_ARGS, ""));
			addJvmPaths.setSelection(configuration.getAttribute(
					ATTR_ADD_JVM_PATHS, false));
			additionalVmArgs.setText(configuration.getAttribute(
					ATTR_ADDITIONAL_VM_ARGS, ""));
			// readProperties(configuration);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// System.out.println("performApply");
		configuration.setAttribute(ATTR_SYNC_BUNDLES,
				syncBundles.getSelection());
		configuration.setAttribute(ATTR_CLEAR_DATA_DIRECTORY,
				clearDataDirectory.getSelection());

		configuration.setAttribute(ATTR_ADDITIONAL_PROGRAM_ARGS,
				additionalProgramArgs.getText());
		configuration.setAttribute(ATTR_ADDITIONAL_VM_ARGS,
				additionalVmArgs.getText());
		configuration.setAttribute(ATTR_ADD_JVM_PATHS,
				addJvmPaths.getSelection());
		// writeProperties(configuration);

		OsgiLaunchHelper.updateLaunchConfiguration(configuration, isEclipse);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// System.out.println("setDefaults");
		configuration.setAttribute(ATTR_SYNC_BUNDLES, true);
		configuration.setAttribute(ATTR_CLEAR_DATA_DIRECTORY, false);
		configuration.setAttribute(ATTR_ADD_JVM_PATHS, false);
		configuration.setAttribute(ATTR_ADDITIONAL_VM_ARGS, "-Xmx128m");
		configuration.setAttribute(ATTR_ADDITIONAL_PROGRAM_ARGS, "-console");
	}

	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		initializeFrom(workingCopy);
		try {
			workingCopy.doSave();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		// do nothing
	}

	class Listener extends SelectionAdapter implements ModifyListener {
		public void widgetSelected(SelectionEvent e) {
			// Object source = e.getSource();
			// setDirty(true);
			updateLaunchConfigurationDialog();
		}

		public void modifyText(ModifyEvent e) {
			// System.out.println("modifyText : " + e);
			// setDirty(true);
			updateLaunchConfigurationDialog();
		}
	}

	// private void readProperties(ILaunchConfiguration configuration) {
	// BufferedReader reader = null;
	// try {
	// IFile propertiesFile = (IFile) configuration.getMappedResources()[0];
	// propertiesFile.refreshLocal(IResource.DEPTH_ONE, null);
	// reader = new BufferedReader(new InputStreamReader(propertiesFile
	// .getContents()));
	// String line = null;
	// StringBuffer buf = new StringBuffer("");
	// while ((line = reader.readLine()) != null) {
	// buf.append(line);
	// buf.append("\n");
	// }
	// propertiesText.setText(buf.toString());
	// } catch (CoreException e) {
	// ErrorDialog.openError(Display.getCurrent().getActiveShell(),
	// "Error", "Cannot read properties", e.getStatus());
	// return;
	// } catch (Exception e) {
	// ErrorDialog.openError(Display.getCurrent().getActiveShell(),
	// "Error", "Cannot read properties",
	// new Status(IStatus.ERROR, SlcIdeUiPlugin.ID,
	// e.getMessage(), e));
	// return;
	// } finally {
	// if (reader != null)
	// try {
	// reader.close();
	// } catch (IOException e) {
	// // silent
	// }
	// }
	//
	// }
	//
	// private void writeProperties(ILaunchConfiguration configuration) {
	// InputStream in = null;
	// IFile propertiesFile = null;
	// try {
	// propertiesFile = (IFile) configuration.getMappedResources()[0];
	// in = new ByteArrayInputStream(propertiesText.getText().getBytes());
	// propertiesFile.setContents(in, true, true, null);
	// propertiesFile.refreshLocal(IResource.DEPTH_ONE, null);
	// } catch (CoreException e) {
	// ErrorDialog.openError(Display.getCurrent().getActiveShell(),
	// "Error", "Cannot write properties", e.getStatus());
	// return;
	// } catch (Exception e) {
	// ErrorDialog.openError(Display.getCurrent().getActiveShell(),
	// "Error", "Cannot write properties",
	// new Status(IStatus.ERROR, SlcIdeUiPlugin.ID,
	// e.getMessage(), e));
	// return;
	// } finally {
	// if (in != null)
	// try {
	// in.close();
	// } catch (IOException e) {
	// // silent
	// }
	// }
	//
	// }

}
