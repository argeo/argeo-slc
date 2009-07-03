package org.argeo.slc.core.execution;

public class RefValue extends AbstractExecutionValue {
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
