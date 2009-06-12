package org.argeo.slc.example;

import java.io.File;

import org.argeo.slc.test.TestData;

public class ExampleTestData implements TestData {
	private File inputFile;
	private File reachedFile;
	private File expectedFile;

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public File getReachedFile() {
		return reachedFile;
	}

	public void setReachedFile(File reachedFile) {
		this.reachedFile = reachedFile;
	}

	public File getExpectedFile() {
		return expectedFile;
	}

	public void setExpectedFile(File expectedFile) {
		this.expectedFile = expectedFile;
	}

}
