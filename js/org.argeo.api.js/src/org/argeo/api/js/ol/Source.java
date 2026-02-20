package org.argeo.api.js.ol;

public class Source extends AbstractOlObject {

	public Source(Object... args) {
		super(args);
	}

	public void refresh() {
		executeMethod(getMethodName());
	}
}
