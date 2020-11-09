package org.argeo.slc.jsch;

import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Session;

public class JschMultiTasks extends AbstractJschTask {
	private List<AbstractJschTask> tasks = new ArrayList<AbstractJschTask>();

	@Override
	protected void run(Session session) {
		for (AbstractJschTask task : tasks) {
			task.setSshTarget(getSshTarget());
			task.run(session);
		}
	}

	public void setTasks(List<AbstractJschTask> tasks) {
		this.tasks = tasks;
	}

	public List<AbstractJschTask> getTasks() {
		return tasks;
	}

}
