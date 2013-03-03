package org.argeo.slc.core.execution;

import java.net.URI;
import java.util.List;

import junit.framework.TestCase;

public class DefaultAgentCliTest extends TestCase {
	public void testArgsToUris() {
		String[] args = { "org.argeo.slc.demo.minimal", "HelloWorld/WithVar",
				"--testKey", "555" };
		List<URI> uris = DefaultAgentCli.asURIs(args);
		assertEquals(1, uris.size());
		assertEquals(
				"flow:/org.argeo.slc.demo.minimal/HelloWorld/WithVar?testKey=555",
				uris.get(0).toString());
	}
}
