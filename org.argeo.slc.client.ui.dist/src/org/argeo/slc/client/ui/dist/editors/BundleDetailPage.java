/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.client.ui.dist.editors;

import java.net.URL;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.build.License;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.utils.HyperlinkAdapter;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/** Show details for a given bundle. */
public class BundleDetailPage extends FormPage implements SlcNames {
	private final static Log log = LogFactory.getLog(BundleDetailPage.class);

	final static String PAGE_ID = "BundleDetailPage";

	// Business Objects
	private Node bundle;

	// This page widgets
	private FormToolkit tk;

	public BundleDetailPage(FormEditor formEditor, String title, Node bundle) {
		super(formEditor, PAGE_ID, title);
		this.bundle = bundle;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		// General settings for this page
		ScrolledForm form = managedForm.getForm();
		tk = managedForm.getToolkit();
		Composite body = form.getBody();

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 5;
		layout.marginRight = 15;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		try {
			form.setText(bundle.hasProperty(SlcNames.SLC_SYMBOLIC_NAME) ? bundle
					.getProperty(SlcNames.SLC_SYMBOLIC_NAME).getString() : "");
			form.setMessage(bundle
					.hasProperty(DistConstants.SLC_BUNDLE_DESCRIPTION) ? bundle
					.getProperty(DistConstants.SLC_BUNDLE_DESCRIPTION)
					.getString() : "", IMessageProvider.NONE);
		} catch (RepositoryException re) {
			throw new SlcException("Unable to get bundle name for node "
					+ bundle, re);
		}

		// Main layout
		Composite header = tk.createComposite(body);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		populateHeaderPart(header);

		Composite mavenSnipet = tk.createComposite(body);
		mavenSnipet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		populateMavenSnippetPart(mavenSnipet);
	}

	private void populateHeaderPart(Composite parent) {
		GridLayout layout = new GridLayout(6, false);
		// layout.marginWidth = layout.horizontalSpacing = layout.marginHeight =
		// 0;
		layout.horizontalSpacing = 10;
		parent.setLayout(layout);
		try {
			// 1st Line: Category, name version
			createLT(parent, "Category",
					bundle.hasProperty(SlcNames.SLC_GROUP_ID) ? bundle
							.getProperty(SlcNames.SLC_GROUP_ID).getString()
							: "");
			createLT(parent, "Name",
					bundle.hasProperty(SlcNames.SLC_ARTIFACT_ID) ? bundle
							.getProperty(SlcNames.SLC_ARTIFACT_ID).getString()
							: "");
			createLT(parent, "Version",
					bundle.hasProperty(SlcNames.SLC_ARTIFACT_VERSION) ? bundle
							.getProperty(SlcNames.SLC_ARTIFACT_VERSION)
							.getString() : "");

			// 3rd Line: Vendor, licence, sources
			createLT(
					parent,
					"Vendor",
					bundle.hasProperty(DistConstants.SLC_BUNDLE_VENDOR) ? bundle
							.getProperty(DistConstants.SLC_BUNDLE_VENDOR)
							.getString() : "N/A");

			createLicencesLink(parent, "Licence",
					DistConstants.SLC_BUNDLE_LICENCE);
			addSourceLink(parent);

			// 2nd Line: The Jar itself and the Manifest
			createJarLink(parent);
			createManifestLink(parent);

			// Last line
			createPomLink(parent);

		} catch (RepositoryException re) {
			throw new SlcException("Unable to get bundle name for node "
					+ bundle, re);
		}

	}

	private void populateMavenSnippetPart(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.horizontalSpacing = layout.horizontalSpacing = layout.marginHeight = 0;
		parent.setLayout(layout);

		Section section = tk.createSection(parent, Section.TITLE_BAR
				| Section.DESCRIPTION);
		section.setText("Maven");
		section.setDescription("Add the below tag to your Artifact pom dependencies");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Text snippetTxt = createMavenSnippet(section);
		section.setClient(snippetTxt);
	}

	// /////////////////////
	// HELPERS

	private Text createLT(Composite parent, String labelValue, String textValue) {
		Label label = tk.createLabel(parent, labelValue, SWT.RIGHT);
		// label.setFont(EclipseUiUtils.getBoldFont(parent));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		// Add a trailing space to workaround a display glitch in RAP 1.3
		Text text = new Text(parent, SWT.LEFT);
		text.setText(textValue + " ");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.setEditable(false);
		return text;
	}

	private void createLicencesLink(Composite parent, String label,
			String jcrPropName) throws RepositoryException {
		tk.createLabel(parent, label, SWT.NONE);
		if (bundle.hasProperty(jcrPropName)) {

			String licenceLinkVal = bundle.getProperty(jcrPropName).getString();

			// FIXME Hack until license generation is done cleanly
			// Problem is with description that contains a "," like
			// "Apache License, Version 2"
			String[] licenceVals;
			if (licenceLinkVal.contains("description="))
				licenceVals = new String[] { licenceLinkVal };
			else
				// multiple license, form non-regenerated manifests
				licenceVals = licenceLinkVal.split(", ");

			Composite body = tk.createComposite(parent);
			body.setLayout(new RowLayout(SWT.WRAP));

			for (final String value : licenceVals) {
				final License currLicense = parseLicenseString(value);

				Hyperlink link = tk.createHyperlink(body,
						currLicense.getName(), SWT.NONE);
				link.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						try {
							IWorkbenchBrowserSupport browserSupport = PlatformUI
									.getWorkbench().getBrowserSupport();
							IWebBrowser browser = browserSupport
									.createBrowser(
											IWorkbenchBrowserSupport.LOCATION_BAR
													| IWorkbenchBrowserSupport.NAVIGATION_BAR,
											"SLC Distribution browser",
											"SLC Distribution browser",
											"A tool tip");
							browser.openURL(new URL(currLicense.getUri()));
						} catch (Exception ex) {
							throw new SlcException("error opening browser", ex); //$NON-NLS-1$
						}
					}
				});
			}
		} else
			tk.createLabel(parent, "N/A", SWT.NONE);
	}

	// TODO this must be moved to a better place once the standard has been
	// defined
	// Enable licence encoding in a single JCR Value
	private final static String LICENSE_SEPARATOR = ";";
	// The human readable name of the licence
	private final static String LICENSE_NAME = "description";
	// A link on the internet with some more info on this licence
	private final static String LICENSE_LINK = "link";

	private License parseLicenseString(String licenseStr) {
		String uri = null, name = null, link = null, text = null;
		// TODO enhance this
		String[] values = licenseStr.split(LICENSE_SEPARATOR);
		for (String value : values) {
			if (value.startsWith(LICENSE_NAME))
				name = value.substring(LICENSE_NAME.length() + 1); // +1 for the
																	// '='
			else if (value.startsWith(LICENSE_LINK))
				link = value.substring(LICENSE_LINK.length() + 1);
			else if (uri == null)
				uri = value;
			// TODO manage text
		}
		return new SimpleLicense(name, uri, link, text);
	}

	class SimpleLicense implements License {
		private final String name;
		private final String uri;
		private final String link;
		private final String text;

		public SimpleLicense(String name, String uri, String link, String text) {
			if (uri == null)
				throw new SlcException(
						"Cannot manage a licence with a null URI ");
			this.uri = uri;

			this.name = name;
			this.link = link;
			this.text = text;
		}

		public String getUri() {
			return uri;
		}

		public String getText() {
			return text;
		}

		public String getName() {
			return name != null ? name : uri;
		}

		public String getLink() {
			return link;
		}
	}

	private void createJarLink(Composite parent) throws RepositoryException {
		Label label = tk.createLabel(parent, "Jar", SWT.RIGHT);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		Composite body = tk.createComposite(parent);
		RowLayout rl = new RowLayout(SWT.HORIZONTAL);
		rl.spacing = 6;
		body.setLayout(rl);
		body.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Hyperlink jarLink = tk
				.createHyperlink(body, bundle.getName(), SWT.NONE);
		jarLink.addHyperlinkListener(new OpenFileLinkListener(bundle.getPath()));

		// Corresponding check sums

		String name = bundle.getName() + ".md5";
		if (bundle.getParent().hasNode(name)) {
			Node md5 = bundle.getParent().getNode(name);
			Hyperlink md5Link = tk.createHyperlink(body, "MD5", SWT.NONE);
			md5Link.addHyperlinkListener(new OpenFileLinkListener(md5.getPath()));
		}

		name = bundle.getName() + ".sha1";
		if (bundle.getParent().hasNode(name)) {
			Node sha1 = bundle.getParent().getNode(name);
			Hyperlink sha1Link = tk.createHyperlink(body, "SHA1", SWT.NONE);
			sha1Link.addHyperlinkListener(new OpenFileLinkListener(sha1
					.getPath()));
		}
	}

	private void createPomLink(Composite parent) throws RepositoryException {
		Label label = tk.createLabel(parent, "Pom", SWT.RIGHT);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		String name = bundle.getName().substring(0,
				bundle.getName().length() - "jar".length())
				+ "pom";

		if (bundle.getParent().hasNode(name)) {
			Node pom = bundle.getParent().getNode(name);

			Composite body = tk.createComposite(parent);
			RowLayout rl = new RowLayout(SWT.HORIZONTAL);
			rl.spacing = 6;
			body.setLayout(rl);
			body.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					3, 1));

			Hyperlink pomLink = tk.createHyperlink(body, "pom.xml", SWT.NONE);
			pomLink.addHyperlinkListener(new OpenFileLinkListener(pom.getPath()));

			// Corresponding check sums
			name = pom.getName() + ".md5";
			if (pom.getParent().hasNode(name)) {
				Node md5 = pom.getParent().getNode(name);
				Hyperlink md5Link = tk.createHyperlink(body, "MD5", SWT.NONE);
				md5Link.addHyperlinkListener(new OpenFileLinkListener(md5
						.getPath()));
			}

			name = pom.getName() + ".sha1";
			if (pom.getParent().hasNode(name)) {
				Node sha1 = pom.getParent().getNode(name);
				Hyperlink sha1Link = tk.createHyperlink(body, "SHA1", SWT.NONE);
				sha1Link.addHyperlinkListener(new OpenFileLinkListener(sha1
						.getPath()));
			}
		} else
			tk.createLabel(parent, "N/A", SWT.NONE);
	}

	private void createManifestLink(Composite parent)
			throws RepositoryException {
		tk.createLabel(parent, "Manifest", SWT.NONE);
		// Hyperlink link =
		// TODO fix this when file download has been implemented for the
		// manifest
		tk.createHyperlink(parent, "MANIFEST.MF", SWT.NONE);
		// link.addHyperlinkListener(new
		// OpenFileLinkListener(bundle.getPath()));
	}

	// private void createHyperlink(Composite parent, String label,
	// String jcrPropName) throws RepositoryException {
	// tk.createLabel(parent, label, SWT.NONE);
	// if (bundle.hasProperty(jcrPropName)) {
	// final Hyperlink link = tk.createHyperlink(parent, bundle
	// .getProperty(jcrPropName).getString(), SWT.NONE);
	// link.addHyperlinkListener(new AbstractHyperlinkListener() {
	// @Override
	// public void linkActivated(HyperlinkEvent e) {
	// try {
	// IWorkbenchBrowserSupport browserSupport = PlatformUI
	// .getWorkbench().getBrowserSupport();
	// IWebBrowser browser = browserSupport
	// .createBrowser(
	// IWorkbenchBrowserSupport.LOCATION_BAR
	// | IWorkbenchBrowserSupport.NAVIGATION_BAR,
	// "SLC Distribution browser",
	// "SLC Distribution browser",
	// "A tool tip");
	// browser.openURL(new URL(link.getText()));
	// } catch (Exception ex) {
	//						throw new SlcException("error opening browser", ex); //$NON-NLS-1$
	// }
	// }
	// });
	// } else
	// tk.createLabel(parent, "N/A", SWT.NONE);
	// }

	// helper to check if sources are available
	private void addSourceLink(Composite parent) {
		try {
			String srcPath = RepoUtils.relatedPdeSourcePath(
					RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH, bundle);
			if (!bundle.getSession().nodeExists(srcPath)) {
				createLT(parent, "Sources", "N/A");
			} else {
				final Node sourcesNode = bundle.getSession().getNode(srcPath);

				String srcName = null;
				if (sourcesNode.hasProperty(SlcNames.SLC_SYMBOLIC_NAME))
					srcName = sourcesNode.getProperty(
							SlcNames.SLC_SYMBOLIC_NAME).getString();
				else
					srcName = sourcesNode.getName();
				Label label = tk.createLabel(parent, "Sources", SWT.RIGHT);
				label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
						false));
				final Hyperlink link = tk.createHyperlink(parent, srcName,
						SWT.NONE);
				link.addHyperlinkListener(new OpenFileLinkListener(sourcesNode
						.getPath()));

				// {
				// @Override
				// public void linkActivated(HyperlinkEvent e) {
				// try {
				// ModuleEditorInput editorInput = (ModuleEditorInput)
				// getEditorInput();
				// Map<String, String> params = new HashMap<String, String>();
				// params.put(OpenJcrFile.PARAM_REPO_NODE_PATH,
				// editorInput.getRepoNodePath());
				// params.put(OpenJcrFile.PARAM_REPO_URI,
				// editorInput.getUri());
				// params.put(OpenJcrFile.PARAM_WORKSPACE_NAME,
				// editorInput.getWorkspaceName());
				// params.put(OpenJcrFile.PARAM_FILE_PATH,
				// );
				// CommandUtils.callCommand(OpenJcrFile.ID, params);
				// } catch (Exception ex) {
				//							throw new SlcException("error opening browser", ex); //$NON-NLS-1$
				// }
				// }
				// });

			}
		} catch (RepositoryException e) {
			throw new SlcException("Unable to configure sources link for "
					+ bundle, e);
		}
	}

	private class OpenFileLinkListener extends HyperlinkAdapter {
		final private String path;

		public OpenFileLinkListener(String path) {
			this.path = path;
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			log.warn("File download must be implemented. Cannot provide access to "
					+ path);

			// try {
			// ModuleEditorInput editorInput = (ModuleEditorInput)
			// getEditorInput();
			// Map<String, String> params = new HashMap<String, String>();
			// params.put(OpenJcrFile.PARAM_REPO_NODE_PATH,
			// editorInput.getRepoNodePath());
			// params.put(OpenJcrFile.PARAM_REPO_URI, editorInput.getUri());
			// params.put(OpenJcrFile.PARAM_WORKSPACE_NAME,
			// editorInput.getWorkspaceName());
			// params.put(OpenJcrFile.PARAM_FILE_PATH, path);
			//
			// String cmdId = (new OpenJcrFileCmdId()).getCmdId();
			// if (log.isTraceEnabled())
			// log.debug("Retrieved openFile Cmd ID: " + cmdId);
			// CommandUtils.callCommand(cmdId, params);
			// } catch (Exception ex) {
			//				throw new SlcException("error opening browser", ex); //$NON-NLS-1$
			// }
		}
	}

	/** Creates a text area with corresponding maven snippet */
	private Text createMavenSnippet(Composite parent) {
		Text mavenSnippet = new Text(parent, SWT.MULTI | SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 100;
		mavenSnippet.setLayoutData(gd);
		mavenSnippet.setText(generateXmlSnippet());
		return mavenSnippet;
	}

	private String generateXmlSnippet() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<dependency>\n");
			sb.append("\t<groupId>");
			sb.append(bundle.getProperty(SLC_GROUP_ID).getString());
			sb.append("</groupId>\n");
			sb.append("\t<artifactId>");
			sb.append(bundle.getProperty(SLC_ARTIFACT_ID).getString());
			sb.append("</artifactId>\n");
			sb.append("\t<version>");
			sb.append(bundle.getProperty(SLC_ARTIFACT_VERSION).getString());
			sb.append("</version>\n");
			sb.append("</dependency>");
			return sb.toString();
		} catch (RepositoryException re) {
			throw new SlcException(
					"unexpected error while generating maven snippet");
		}
	}
}