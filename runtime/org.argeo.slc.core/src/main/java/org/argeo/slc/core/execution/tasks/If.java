package org.argeo.slc.core.execution.tasks;

import org.argeo.slc.SlcException;

/** Conditional execution */
public class If implements Runnable {
	private Boolean is;
	private Boolean not;
	private Runnable then;
	private Runnable els;

	public void run() {
		if (is == null && not == null)
			throw new SlcException("No condition set");
		if (is != null && not != null)
			throw new SlcException("Both is and not cannot be set");

		boolean bool = (is != null ? is : !not);
		if (bool) {
			if (then != null)
				then.run();
		} else {
			if (els != null)
				els.run();
		}

	}

	public void setIs(Boolean bool) {
		this.is = bool;
	}

	public void setThen(Runnable then) {
		this.then = then;
	}

	public void setEls(Runnable els) {
		this.els = els;
	}

	public Boolean getNot() {
		return not;
	}

	public void setNot(Boolean not) {
		this.not = not;
	}

}
