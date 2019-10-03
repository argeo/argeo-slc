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
package org.argeo.slc.client.ui.editors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.query.Query;

import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.execution.ExecutionStep;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ProcessLogPage extends FormPage {
	public final static String ID = "processLogPage";

	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	/** Where the log is displayed. */
	private Text text;
	/**
	 * Stores logs received before the text was shown. TODO : rather store in in
	 * JCR and reads it from there.
	 */
	private StringBuffer beforeTextInit = new StringBuffer("");

	private Node processNode;
	/**
	 * optimization field: we compute once the length of the path to slc:log so
	 * that we can easily substring the relative path of logs.
	 */
	//private Integer logPathLength;

	public ProcessLogPage(FormEditor editor, Node processNode) {
		super(editor, ID, "Log");
		this.processNode = processNode;

		EventListener listener = new LogListener(editor.getSite().getPage()
				.getWorkbenchWindow().getWorkbench().getDisplay());

		try {
			String logBasePath = processNode.getPath() + '/' + SlcNames.SLC_LOG;
			//logPathLength = logBasePath.length();

			Workspace ws = processNode.getSession().getWorkspace();

			String statement = "SELECT * FROM ["
					+ SlcTypes.SLC_LOG_ENTRY
					+ "] as logEntry"
					+ " WHERE ISDESCENDANTNODE('"
					+ logBasePath
					+ "')"
					+ " ORDER BY logEntry.[slc:timestamp] ASC, NAME(logEntry) ASC";
			StringBuffer buf = new StringBuffer("");
			NodeIterator it = ws.getQueryManager()
					.createQuery(statement, Query.JCR_SQL2).execute()
					.getNodes();
			while (it.hasNext())
				appendLogEntry(buf, it.nextNode());
			beforeTextInit = new StringBuffer(buf.toString());
			// text.setText(buf.toString());
			ws.getObservationManager().addEventListener(listener,
					Event.NODE_ADDED, logBasePath, true, null, null, false);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register listener", e);
		}
	}

	@Override
	public synchronized void createPartControl(Composite parent) {
		// bypass createFormContent
		FormToolkit tk = getEditor().getToolkit();
		// parent.setLayout(new FillLayout());
		text = tk.createText(parent, "", SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		text.setEditable(false);

		// transfer the existing buffer the first time
		if (beforeTextInit.length() > 0) {
			text.append(beforeTextInit.toString());
			// clear buffer
			beforeTextInit.setLength(0);
		}

	}

	// @Override
	// protected synchronized void createFormContent(IManagedForm mf) {
	// ScrolledForm form = mf.getForm();
	// form.setExpandHorizontal(true);
	// form.setExpandVertical(true);
	// // form.setText("Log");
	// FillLayout mainLayout = new FillLayout();
	// form.getBody().setLayout(mainLayout);
	//
	// FormToolkit tk = getManagedForm().getToolkit();
	// text = tk.createText(form.getBody(), "", SWT.MULTI | SWT.H_SCROLL
	// | SWT.V_SCROLL);
	// text.setEditable(false);
	// // transfer the existing buffer the first time
	// if (beforeTextInit.length() > 0) {
	// text.append(beforeTextInit.toString());
	// // clear buffer
	// beforeTextInit.setLength(0);
	// }
	// }

	protected void appendLogEntry(StringBuffer buf, Node logEntry)
			throws RepositoryException {
		// +1 in order to remove the first slash
//		String relPath = logEntry.getPath().substring(logPathLength + 1);
		//System.out.println("relPath=" + relPath);
//		int firstSlashIndex = relPath.indexOf('/');
//		int lastSlashIndex = relPath.lastIndexOf('/');
//		String thread = relPath.substring(0, firstSlashIndex);
//		String location = relPath.substring(firstSlashIndex, lastSlashIndex);

		// String date = dateFormat.format(logEntry
		// .getProperty(SlcNames.SLC_TIMESTAMP).getDate().getTime());
		String date = logEntry.getProperty(SlcNames.SLC_TIMESTAMP).getString();
		buf.append(date).append(' ');
		String type = logEntry.getPrimaryNodeType().getName().substring(7);
		buf.append(type).append('\t');
		// buf.append(thread).append('\t');
		// buf.append(location).append('\t');
		buf.append(logEntry.getProperty(SlcNames.SLC_MESSAGE).getString());
		buf.append('\n');

	}

	/** @deprecated */
	public synchronized void addSteps(List<ExecutionStep> steps) {
		final StringBuffer buf = new StringBuffer("");
		for (ExecutionStep step : steps) {
			buf.append(dateFormat.format(step.getTimestamp()));
			buf.append(' ');
			if (step.getType().equals(ExecutionStep.PHASE_START)) {
				buf.append("## START ").append(step.getLog());
				buf.append('\n');
			} else if (step.getType().equals(ExecutionStep.PHASE_END)) {
				buf.append("## END   ").append(step.getLog());
				buf.append("\n");
			} else {
				buf.append(step.getLog());
			}
		}

		if (text != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					text.append(buf.toString());
				}
			});
		} else
			beforeTextInit.append(buf);
	}

	@Override
	public Control getPartControl() {
		return text;
	}

	@Override
	public void setFocus() {
		if (text != null)
			text.setFocus();
	}

	/** JCR event listener notifying when new nodes are added */
	private class LogListener extends AsyncUiEventListener {

		public LogListener(Display display) {
			super(display);
		}

		@Override
		protected void onEventInUiThread(List<Event> events)
				throws RepositoryException {
			// since we use batch save, order is not guaranteed
			// so we need to reorder, according to log line number for the time
			// being
			SortedMap<Long, Node> nodes = new TreeMap<Long, Node>();

			for (Event evt : events) {
				Node newNode = ProcessLogPage.this.processNode.getSession()
						.getNode(evt.getPath());
				if (newNode.isNodeType(SlcTypes.SLC_LOG_ENTRY)) {
					nodes.put(Long.parseLong(newNode.getName()), newNode);
				}
			}

			StringBuffer buf = new StringBuffer("");
			for (Node logEntry : nodes.values()) {
				appendLogEntry(buf, logEntry);
			}

			if (text != null)
				text.append(buf.toString());
			else
				beforeTextInit.append(buf);
		}
	}
}
