package org.argeo.slc.client.ui.dist.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.jcr.SlcNames;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
		SlcNames {
	private static final Log log = LogFactory.getLog(QueryBundlesForm.class);
	public static final String ID = DistPlugin.ID + ".queryBundlesForm";

	// widgets
	private Button executeBtn;
	private Text symbolicName;
	private Text importedPackage;
	private Text exportedPackage;
	private SashForm sashForm;

	// shortcuts
	final static String SBA = "sba";
	final static String SIP = "sip";
	final static String SEP = "sep";

	private Composite top, bottom;

	@Override
	public void createPartControl(Composite parent) {

		sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setSashWidth(4);
		sashForm.setLayout(new GridLayout(1, false));

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
		lbl.setText("Bundle name: ");
		symbolicName = new Text(parent, SWT.SINGLE | SWT.BORDER);

		// imported package
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Imported package: ");
		importedPackage = new Text(parent, SWT.SINGLE | SWT.BORDER);

		// exported package
		lbl = new Label(parent, SWT.SINGLE);
		lbl.setText("Exported package: ");
		exportedPackage = new Text(parent, SWT.SINGLE | SWT.BORDER);

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
			sb.append("select " + SBA + ".* ");
			if (ipClause)
				sb.append(", " + SIP + ".* ");
			if (epClause)
				sb.append(", " + SEP + ".* ");

			sb.append(" from [");
			sb.append(SLC_BUNDLE_ARTIFACT);
			sb.append("] as " + SBA + " ");

			// join
			if (ipClause) {
				sb.append(" inner join [");
				sb.append(SLC_IMPORTED_PACKAGE);
				sb.append("] as " + SIP + " on isdescendantnode(" + SIP + ", "
						+ SBA + ") ");
			}

			if (epClause) {
				sb.append(" inner join [");
				sb.append(SLC_EXPORTED_PACKAGE);
				sb.append("] as " + SEP + " on isdescendantnode(" + SEP + ", "
						+ SBA + ") ");
			}

			// where
			sb.append(" where ");
			if (symbolicName.getText() != null
					&& !symbolicName.getText().trim().equals("")) {
				sb.append("sba.[" + SLC_SYMBOLIC_NAME + "] like '"
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

			if (log.isDebugEnabled())
				log.debug("Statement : " + sb.toString());

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