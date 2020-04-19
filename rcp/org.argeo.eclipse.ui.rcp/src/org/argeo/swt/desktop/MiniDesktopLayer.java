package org.argeo.swt.desktop;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/** Implementation of an active desktop with icons. */
public class MiniDesktopLayer {
	public void init(Composite parent, Path context) {
		parent.setLayout(new RowLayout());
		try {
			DirectoryStream<Path> ds = Files.newDirectoryStream(context);
			ds.forEach((path) -> createIcon(parent, path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void createIcon(Composite parent, Path path) {
		String fileName = path.getFileName().toString();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		Program program = Program.findProgram(ext);
		if (program == null) {
			createDefaultIcon(parent, path);
			return;
		}

		Display display = parent.getDisplay();
		ImageData iconData = program.getImageData();

		Image iconImage;
		if (iconData == null) {
			iconImage = null;
		} else {
			iconImage = new Image(display, iconData);
		}

		Composite icon = new Composite(parent, SWT.BORDER);
		icon.setLayoutData(new RowData(48, 72));
		icon.setLayout(new GridLayout());
		// Button
		Button iconB = new Button(icon, SWT.FLAT);
		iconB.setImage(iconImage);
		// iconB.setLayoutData(new GridData(iconData.width, iconData.height));
		iconB.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		iconB.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				program.execute(path.toString());
			}

		});
		// Label
		Label iconL = new Label(icon, SWT.WRAP);
		iconL.setText(path.getFileName().toString());
		iconL.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	protected void createDefaultIcon(Composite parent, Path path) {
		Composite icon = new Composite(parent, SWT.NONE);
		icon.setLayout(new GridLayout());
		Label iconL = new Label(icon, SWT.NONE);
		iconL.setText(path.getFileName().toString());
		iconL.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
	}
}
