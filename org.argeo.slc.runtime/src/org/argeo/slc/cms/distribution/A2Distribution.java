package org.argeo.slc.cms.distribution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.argeo.api.a2.A2Branch;
import org.argeo.api.a2.A2Component;
import org.argeo.api.a2.A2Contribution;
import org.argeo.api.a2.A2Module;
import org.argeo.api.a2.A2Source;
import org.argeo.api.slc.CategoryNameVersion;
import org.argeo.api.slc.DefaultCategoryNameVersion;
import org.argeo.api.slc.NameVersion;
import org.argeo.api.slc.build.Distribution;
import org.argeo.api.slc.build.ModularDistribution;

public class A2Distribution implements ModularDistribution {
	private List<A2Source> a2Sources = new ArrayList<>();

	@Override
	public String getDistributionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<? extends NameVersion> nameVersions() {
		List<CategoryNameVersion> nameVersions = new ArrayList<>();
		for (A2Source a2Source : a2Sources) {
			for (A2Contribution a2Contribution : a2Source.listContributions(null)) {
				for (A2Component a2Component : a2Contribution.listComponents(null)) {
					for (A2Branch a2Branch : a2Component.listBranches(null)) {
						for (A2Module a2Module : a2Branch.listModules(null)) {
							CategoryNameVersion nameVersion = new DefaultCategoryNameVersion(a2Contribution.getId(),
									a2Component.getId(), a2Module.getVersion().toString());
							nameVersions.add(nameVersion);
						}
					}
				}
			}
		}
		return nameVersions.iterator();
	}

	@Override
	public Distribution getModuleDistribution(String moduleName, String moduleVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getModulesDescriptor(String descriptorType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<A2Source> getA2Sources() {
		return a2Sources;
	}

	
}
