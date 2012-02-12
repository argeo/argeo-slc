package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.SlcException;

/** Conditional execution */
public class If implements Runnable {
	private Boolean bool;
	private Runnable then;
	private Runnable els;

	public void run() {
		if (bool == null)
			throw new SlcException("No condition set");

		if (bool) {
			if (then != null)
				then.run();
		} else {
			if (els != null)
				els.run();
		}

	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}

	public void setThen(Runnable then) {
		this.then = then;
	}

	public void setEls(Runnable els) {
		this.els = els;
	}

}
