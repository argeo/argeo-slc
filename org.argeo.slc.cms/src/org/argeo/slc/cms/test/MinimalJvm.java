package org.argeo.slc.cms.test;

/**
 * A program doing nothing and loading no classes, to be used as a baseline for
 * memory usage of the JVM.
 */
public class MinimalJvm {

	synchronized void sleep() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new MinimalJvm().sleep();
	}

}
