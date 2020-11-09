package org.argeo.slc.client.ui.editors;

import org.argeo.slc.client.ui.ClientUiPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class ProcessEditorInputFactory implements IElementFactory {
	public final static String ID = ClientUiPlugin.ID
			+ ".processEditorInputFactory";

	public IAdaptable createElement(IMemento memento) {
		String path = memento.getString("processPath");
		return new ProcessEditorInput(path);
	}

}
