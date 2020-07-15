package org.argeo.slc.client.ui.dist.views;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.client.ui.dist.DistPlugin;
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
public class QueryBundlesForm extends AbstractQueryArtifactsView implements
		SlcNames, SlcTypes {
	// private static final Log log = LogFactory.getLog(QueryBundlesForm.class);
	public static final String ID = DistPlugin.PLUGIN_ID + ".queryBundlesForm";

	// widgets
	private Button executeBtn;
	private Text symbolicName;
	private Text importedPackage;
	private Text exportedPackage;
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

		// Bundle Name
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Symbolic name");
		symbolicName = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		symbolicName.setLayoutData(gd);

		// imported package
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Imported package");
		importedPackage = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		importedPackage.setLayoutData(gd);

		// exported package
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Exported package");
		exportedPackage = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		exportedPackage.setLayoutData(gd);

		executeBtn = new Button(parent, SWT.PUSH);
		executeBtn.setText("Search");
		gd = new GridData();
		gd.horizontalSpan = 2;
		executeBtn.setLayoutData(gd);

		Listener executeListener = new Listener() {
			private static final long serialVersionUID = 6267263421349073712L;

			public void handleEvent(Event event) {
				refreshQuery();
			}
		};
		executeBtn.addListener(SWT.Selection, executeListener);
	}

	public void refreshQuery() {
		String queryStr = generateStatement();
		executeQuery(queryStr);
		bottom.layout();
		sashForm.layout();
	}

	private String generateStatement() {
		try {
			// shortcuts
			boolean hasFirstClause = false;
			boolean ipClause = importedPackage.getText() != null
					&& !importedPackage.getText().trim().equals("");
			boolean epClause = exportedPackage.getText() != null
					&& !exportedPackage.getText().trim().equals("");

			StringBuffer sb = new StringBuffer();
			// Select
			sb.append("select " + SBA + ".*, " + SAVB + ".* ");
			sb.append(" from " + SAVB);

			// join
			sb.append(" inner join ");
			sb.append(SBA);
			sb.append(" on isdescendantnode(" + SBA + ", " + SAVB + ") ");
			if (ipClause) {
				sb.append(" inner join ");
				sb.append(SIP);
				sb.append(" on isdescendantnode(" + SIP + ", " + SBA + ") ");
			}

			if (epClause) {
				sb.append(" inner join ");
				sb.append(SEP);
				sb.append(" on isdescendantnode(" + SEP + ", " + SBA + ") ");
			}

			// where
			sb.append(" where ");
			if (symbolicName.getText() != null
					&& !symbolicName.getText().trim().equals("")) {
				sb.append(SBA + ".[" + SLC_SYMBOLIC_NAME + "] like '"
						+ symbolicName.getText().replace('*', '%') + "'");
				hasFirstClause = true;
			}

			if (ipClause) {
				if (hasFirstClause)
					sb.append(" AND ");
				sb.append(SIP + ".[" + SLC_NAME + "] like '"
						+ importedPackage.getText().replace('*', '%') + "'");
				hasFirstClause = true;
			}

			if (epClause) {
				if (hasFirstClause)
					sb.append(" AND ");
				sb.append(SEP + ".[" + SLC_NAME + "] like '"
						+ exportedPackage.getText().replace('*', '%') + "'");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new SlcException(
					"Cannot generate where statement to get artifacts", e);
		}
	}

	@Override
	public void setFocus() {
		executeBtn.setFocus();
	}
}