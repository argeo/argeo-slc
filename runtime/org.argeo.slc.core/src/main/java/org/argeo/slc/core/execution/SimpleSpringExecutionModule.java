package org.argeo.slc.core.execution;

public class SimpleSpringExecutionModule extends AbstractSpringExecutionModule {

	private String name;
	private String version;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

}
