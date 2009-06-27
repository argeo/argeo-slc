package org.argeo.slc.web.mvc.provisioning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.web.HttpRequestHandler;

/** An Eclipse update site, serving site.xml features/* and plugins/*. */
public class EclipseUpdateSiteHandler implements HttpRequestHandler {
	private FileProvider provider;
	private String baseVersion = "0.9.4-SNAPSHOT";

	private EclipseUpdateSite updateSite;

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		StringTokenizer stS = new StringTokenizer(path, "/");
		String distribution = stS.nextToken();

		StringBuffer buf = new StringBuffer("");
		buf.append("<site>");

		List<EclipseUpdateSiteCategory> usedCategories = new ArrayList<EclipseUpdateSiteCategory>();
		for (EclipseUpdateSiteFeature feature : updateSite.getFeatures()) {
			Resource bundle = provider.getBundle(distribution, feature
					.getName(), baseVersion);
			JarInputStream in = new JarInputStream(bundle.getInputStream());
			Manifest mf = in.getManifest();
			String version = mf.getMainAttributes().getValue("Bundle-Version");
			String featureId = feature.getName();
			buf.append("<feature");
			buf.append(" url=\"features/").append(featureId).append('_')
					.append(version).append(".jar\"");
			buf.append(" id=\"").append(featureId).append("\"");
			buf.append(" version=\"").append(version).append("\"");
			buf.append(">\n");

			for (EclipseUpdateSiteCategory category : feature.getCategories()) {
				usedCategories.add(category);
				buf.append("  <category name=\"").append(category.getName())
						.append("\"/>\n");
			}
			buf.append("</feature>\n\n");
		}

		for (EclipseUpdateSiteCategory category : usedCategories) {
			buf.append("<category-def");
			buf.append(" name=\"").append(category.getName()).append("\"");
			buf.append(" label=\"").append(category.getLabel()).append("\"");
			buf.append(">\n");
			buf.append("  <description>").append(category.getDescription())
					.append("</decription>\n");
			buf.append("</category-def>\n\n");
		}

		buf.append("</site>");

		response.getWriter().print(buf.toString());
	}

	public void setProvider(FileProvider provider) {
		this.provider = provider;
	}

	public void setBaseVersion(String baseVersion) {
		this.baseVersion = baseVersion;
	}

	public void setUpdateSite(EclipseUpdateSite updateSite) {
		this.updateSite = updateSite;
	}

}
