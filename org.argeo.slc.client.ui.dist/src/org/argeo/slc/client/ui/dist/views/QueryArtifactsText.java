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
package org.argeo.slc.client.ui.dist.views;

import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.jcr.SlcNames;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/** Query SLC Repo to get some artifacts with a JCR SQL 2 request. */
public class QueryArtifactsText extends AbstractQueryArtifactsView implements
		SlcNames {
	// private static final Log log =
	// LogFactory.getLog(QueryArtifactsText.class);
	public static final String ID = DistPlugin.PLUGIN_ID + ".queryArtifactsText";

	// widgets
	private Button executeBtn;
	private Text queryText;
	private SashForm sashForm;

	private Composite top, bottom;

	@Override
	public void createPartControl(Composite parent) {

		sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setSashWidth(4);
		// Enable the different parts to fill the whole page when the tab is
		// maximized
		sashForm.setLayout(new FillLayout());

		top = new Composite(sashForm, SWT.NONE);
		top.setLayout(new GridLayout(1, false));

		bottom = new Composite(sashForm, SWT.NONE);
		bottom.setLayout(new GridLayout(1, false));

		sashForm.setWeights(new int[] { 25, 75 });

		createQueryForm(top);
		createResultPart(bottom);
	}

	public void createQueryForm(Composite parent) {
		Label lbl;
		GridData gd;

		GridLayout gl = new GridLayout(2, false);
		gl.marginTop = 5;
		parent.setLayout(gl);

		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Enter a JCR:SQL2 Query");

		executeBtn = new Button(parent, SWT.PUSH);
		executeBtn.setText("Search");

		queryText = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 100;
		gd.horizontalSpan = 2;
		queryText.setLayoutData(gd);

		String query = generateSelectStatement() + generateFromStatement()
				+ generateWhereStatement();
		queryText.setText(query);

		Listener executeListener = new Listener() {
			private static final long serialVersionUID = -5028331930076117569L;

			public void handleEvent(Event event) {
				refreshQuery();
			}
		};
		executeBtn.addListener(SWT.Selection, executeListener);
	}

	public void refreshQuery() {
		String queryStr = queryText.getText();
		executeQuery(queryStr);
		bottom.layout();
		sashForm.layout();
	}

	private String generateWhereStatement() {
		StringBuffer sb = new StringBuffer(" where ");
		return sb.toString();
	}

	@Override
	public void setFocus() {
		executeBtn.setFocus();
	}
}