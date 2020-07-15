package org.argeo.cms.ui.workbench.jcr;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import org.argeo.cms.CmsException;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.eclipse.ui.fs.SimpleFsBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Browse the node file system. */
public class NodeFsBrowserView extends ViewPart {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".nodeFsBrowserView";

	private FileSystemProvider nodeFileSystemProvider;

	@Override
	public void createPartControl(Composite parent) {
		try {
			URI uri = new URI("node:///");
			FileSystem fileSystem = nodeFileSystemProvider.getFileSystem(uri);
			if (fileSystem == null)
				fileSystem = nodeFileSystemProvider.newFileSystem(uri, null);
			Path nodePath = fileSystem.getPath("~");
			SimpleFsBrowser browser = new SimpleFsBrowser(parent, SWT.NO_FOCUS);
			browser.setInput(nodePath);
		} catch (Exception e) {
			throw new CmsException("Cannot open file system browser", e);
		}
	}

	@Override
	public void setFocus() {
	}

	public void setNodeFileSystemProvider(FileSystemProvider nodeFileSystemProvider) {
		this.nodeFileSystemProvider = nodeFileSystemProvider;
	}
}
