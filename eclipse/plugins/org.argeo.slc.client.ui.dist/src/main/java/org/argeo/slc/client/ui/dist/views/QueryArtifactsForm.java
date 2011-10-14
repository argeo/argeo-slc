package org.argeo.slc.client.ui.dist.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
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

/** Query SLC Repo to get some artifacts given some predefined parameters */
public class QueryArtifactsForm extends AbstractQueryArtifactsView implements
		SlcNames {
	private static final Log log = LogFactory.getLog(QueryArtifactsForm.class);
	public static final String ID = DistPlugin.ID + ".queryArtifactsForm";

	// widgets
	private Button executeBtn;
	private Text groupId;
	private Text artifactId;
	private Text version;
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

		// lbl = new Label(parent, SWT.SINGLE);
		// lbl.setText("Query by coordinates");
		// gd = new GridData();
		// gd.horizontalSpan = 2;
		// lbl.setLayoutData(gd);

		// Group ID
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Group ID");
		groupId = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		groupId.setLayoutData(gd);

		// Artifact ID
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Artifact ID");
		artifactId = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		artifactId.setLayoutData(gd);

		// Version
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Version");
		version = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		version.setLayoutData(gd);

		executeBtn = new Button(parent, SWT.PUSH);
		executeBtn.setText("Search");
		gd = new GridData();
		gd.horizontalSpan = 2;
		executeBtn.setLayoutData(gd);

		Listener executeListener = new Listener() {
			public void handleEvent(Event event) {
				refreshQuery();
			}
		};
		executeBtn.addListener(SWT.Selection, executeListener);
	}

	public void refreshQuery() {
		String queryStr = generateSelectStatement() + generateFromStatement()
				+ generateWhereStatement();
		executeQuery(queryStr);
		bottom.layout();
		sashForm.layout();
	}

	private String generateWhereStatement() {
		try {
			boolean hasFirstClause = false;
			StringBuffer sb = new StringBuffer(" where ");

			if (groupId.getText() != null
					&& !groupId.getText().trim().equals("")) {
				sb.append("[" + SLC_GROUP_ID + "] like '"
						+ groupId.getText().replace('*', '%') + "'");
				hasFirstClause = true;
			}

			if (artifactId.getText() != null
					&& !artifactId.getText().trim().equals("")) {
				if (hasFirstClause)
					sb.append(" AND ");
				sb.append("[" + SLC_ARTIFACT_ID + "] like '"
						+ artifactId.getText().replace('*', '%') + "'");
				hasFirstClause = true;
			}

			if (version.getText() != null
					&& !version.getText().trim().equals("")) {
				if (hasFirstClause)
					sb.append(" AND ");
				sb.append("[" + SLC_ARTIFACT_VERSION + "] like '"
						+ version.getText().replace('*', '%') + "'");
			}

			return sb.toString();
		} catch (Exception e) {
			throw new ArgeoException(
					"Cannot generate where statement to get artifacts", e);
		}
	}

	@Override
	public void setFocus() {
		executeBtn.setFocus();
	}
}