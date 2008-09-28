package org.argeo.slc.detached;

import java.io.File;
import java.util.UUID;

import junit.framework.TestCase;

import org.argeo.slc.autoui.DetachedStepRequest;
import org.argeo.slc.autoui.drivers.FileDriver;

public class DetachedTest extends TestCase {
	public void testSendRequest() throws Exception {
		FileDriver client = new FileDriver();
		File requestDir = new File("local/detachedRequests");
		requestDir.mkdirs();
		client.setRequestDir(requestDir);

		DetachedStepRequest request = new DetachedStepRequest();
		request.setUuid(UUID.randomUUID().toString());
		request.setStepRef("jemmyTest");

		client.sendRequest(request);
	}
}
