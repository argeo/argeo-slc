package org.argeo.slc.maven;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

public class MavenToUrl implements FactoryBean {
	private List<MavenFile> mavenFiles;
	private String baseUrl;

	public List<String> asUrls() {
		List<String> urls = new ArrayList<String>();
		for (MavenFile mf : mavenFiles)
			urls.add(convertToUrl(mf));
		return urls;
	}

	public String convertToUrl(MavenFile mf) {
		return baseUrl + mf.getGroupId().replace('.', '/') + '/'
				+ mf.getArtifactId() + '/' + mf.getVersion() + '/'
				+ mf.getArtifactId() + '-' + mf.getVersion() + '.'
				+ mf.getType();
	}

	public void setMavenFiles(List<MavenFile> mavenFiles) {
		this.mavenFiles = mavenFiles;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Object getObject() throws Exception {
		return asUrls();
	}

	public Class getObjectType() {
		return List.class;
	}

	public boolean isSingleton() {
		return false;
	}

}
