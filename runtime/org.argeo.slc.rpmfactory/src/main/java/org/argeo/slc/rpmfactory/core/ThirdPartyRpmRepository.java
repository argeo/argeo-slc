package org.argeo.slc.rpmfactory.core;

/**
 * A repository of third party RPMs used for the build. RPM used by the builds
 * will be cached within the system.
 */
public class ThirdPartyRpmRepository extends AbstractRpmRepository {
	private String yumConf;

	public String getYumConf() {
		return yumConf;
	}

	public void setYumConf(String yumConf) {
		this.yumConf = yumConf;
	}

}
