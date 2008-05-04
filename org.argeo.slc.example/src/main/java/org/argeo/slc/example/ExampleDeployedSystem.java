package org.argeo.slc.example;

import org.argeo.slc.core.build.Distribution;
import org.argeo.slc.core.deploy.DeployedSystem;
import org.argeo.slc.example.appli.ExampleAppli;

/** Example deployed sytem. */
public class ExampleDeployedSystem implements DeployedSystem {
	private String deployedSystemId;
	private int skipFreq = 2;

	public String getDeployedSystemId() {
		return deployedSystemId.toString();
	}

	/** Sets deployed system id. */
	public void setDeployedSystemId(String deployedSystemId) {
		this.deployedSystemId = deployedSystemId;
	}

	/** Creates an instance of the example appli. */
	public ExampleAppli getExampleAppliInstance() {
		ExampleAppli appli = new ExampleAppli();
		appli.setSkipFreq(skipFreq);
		return appli;
	}

	/** Sets the frequency used to skip lines. */
	public void setSkipFreq(int skipFreq) {
		this.skipFreq = skipFreq;
	}

	public Distribution getDistribution() {
		// TODO Auto-generated method stub
		return null;
	}

}
