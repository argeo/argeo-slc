package org.argeo.slc.jemmytest;

import java.io.File;
import java.util.UUID;

import junit.framework.TestCase;

import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.drivers.FileDriver;

public class DetachedTest extends TestCase {
	public void testSendRequest() throws Exception {
		FileDriver client = new FileDriver();
		File baseDir = new File("local/detached");
		baseDir.mkdirs();
		client.setBaseDir(baseDir);

		DetachedRequest request = new DetachedRequest();
		request.setUuid(UUID.randomUUID().toString());
		request.setRef("jemmyTest");

		client.sendRequest(request);
	}
}
