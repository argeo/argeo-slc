package org.argeo.slc.cms.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CmsSmokeTest {

	public static void main(String[] args) throws IOException {
		Path instanceData;
		if (args.length > 0) {
			instanceData = Paths.get(args[0]);
		} else {
			instanceData = Files.createTempDirectory("cms-test");
		}

	}

}
