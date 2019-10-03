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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/** Show the details for a given bundle. */
public class ModularDistVersionDetailPage extends FormPage implements SlcNames {

	final static String PAGE_ID = "ModularDistVersionDetailPage";

	// Business Objects
	private Node modularDistVersion;

	// This page widgets
	private FormToolkit tk;

	public ModularDistVersionDetailPage(FormEditor formEditor, String title,
			Node modularDistVersion) {
		super(formEditor, PAGE_ID, title);
		this.modularDistVersion = modularDistVersion;
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
			form.setText(modularDistVersion.hasProperty(SLC_NAME) ? modularDistVersion
					.getProperty(SLC_NAME).getString() : "");
		} catch (RepositoryException re) {
			throw new SlcException("Unable to get slc:name for node "
					+ modularDistVersion, re);
		}

		// Main layout
		Composite mavenSnipet = tk.createComposite(body);
		mavenSnipet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		populateMavenSnippetPart(mavenSnipet);
	}

	private void populateMavenSnippetPart(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.horizontalSpacing = layout.horizontalSpacing = layout.marginHeight = 0;
		parent.setLayout(layout);

		Section section = tk.createSection(parent, Section.TITLE_BAR
				| Section.DESCRIPTION);
		section.setText("Maven");
		section.setDescription("In order to rely on the versions defined by this distribution, "
				+ "add the below tag to the dependency management of your parent pom.");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Text snippetTxt = createMavenSnippet(section);
		section.setClient(snippetTxt);
	}

	// /////////////////////
	// HELPERS
	/** Creates a text area with corresponding maven snippet */
	private Text createMavenSnippet(Composite parent) {
		Text mavenSnippet = new Text(parent, SWT.MULTI | SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 100;
		mavenSnippet.setLayoutData(gd);
		mavenSnippet.setText(generateXmlSnippet());
		mavenSnippet.setEditable(false);
		return mavenSnippet;
	}

	private String generateXmlSnippet() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<dependency>\n");
			sb.append("\t<groupId>");
			sb.append(modularDistVersion.getProperty(SLC_GROUP_ID).getString());
			sb.append("</groupId>\n");
			sb.append("\t<artifactId>");
			sb.append(modularDistVersion.getProperty(SLC_ARTIFACT_ID)
					.getString());
			sb.append("</artifactId>\n");
			sb.append("\t<version>");
			sb.append(modularDistVersion.getProperty(SLC_ARTIFACT_VERSION)
					.getString());
			sb.append("</version>\n");
			sb.append("\t<type>pom</type>\n");
			sb.append("\t<scope>import</scope>\n");
			sb.append("</dependency>");
			return sb.toString();
		} catch (RepositoryException re) {
			throw new SlcException(
					"unexpected error while generating maven snippet");
		}
	}
}