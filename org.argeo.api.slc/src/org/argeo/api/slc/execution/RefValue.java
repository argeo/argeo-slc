package org.argeo.api.slc.execution;

/** Reference value to be used by an execution */
public class RefValue extends AbstractExecutionValue {
	private static final long serialVersionUID = -8951231456757181687L;
	private String ref;
	private String type;

	public RefValue() {
	}

	public RefValue(String ref) {
		super();
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Ref Value [" + type + "=" + ref + "]";
	}

}
