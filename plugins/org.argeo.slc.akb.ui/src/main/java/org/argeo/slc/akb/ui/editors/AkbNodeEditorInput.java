package org.argeo.slc.akb.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Editor input for all editors that display info on a given AKB JCR Node.
 * 
 * Relies on the Jcr ID,
 */
public class AkbNodeEditorInput implements IEditorInput {

	private final String envJcrId;
	// Only null if current node is a environment (active or template)
	private final String jcrId;

	/**
	 * the Env Jcr ID cannot be null, JcrId can only be null if current node is
	 * an environment (active or template)
	 */
	public AkbNodeEditorInput(String envJcrId, String jcrId) {
		this.envJcrId = envJcrId;
		this.jcrId = jcrId;
	}

	public String getIdentifier() {
		if (jcrId == null)
			return envJcrId;
		else
			return jcrId;
	}

	public String getEnvIdentifier() {
		return envJcrId;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return jcrId;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Display and edit information about a given AKB Jcr Node";
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public int hashCode() {
		return jcrId.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AkbNodeEditorInput other = (AkbNodeEditorInput) obj;
		if (!jcrId.equals(other.getIdentifier())
				|| !envJcrId.equals(other.getEnvIdentifier()))
			return false;
		return true;
	}
}