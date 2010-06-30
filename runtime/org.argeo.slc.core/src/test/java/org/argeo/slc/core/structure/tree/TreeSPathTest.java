/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.core.structure.tree;

import junit.framework.TestCase;

public class TreeSPathTest extends TestCase {

	public void testNew() {
		TreeSPath path = new TreeSPath("/test");
		assertEquals("test", path.getName());
		assertNull(path.getParent());

		path = new TreeSPath("/root/subdir");
		assertEquals("subdir", path.getName());
		assertEquals(new TreeSPath("/root"), path.getParent());
	}

	public void testEquals() {
		TreeSPath path1 = new TreeSPath("/test");
		TreeSPath path2 = new TreeSPath("/test");
		assertEquals(path1, path2);

		path1 = new TreeSPath("/test/subdir/anotherdir");
		path2 = new TreeSPath("/test/subdir/anotherdir");
		assertEquals(path1, path2);

		path1 = new TreeSPath("/test/subdir/anotherd");
		path2 = new TreeSPath("/test/subdir/anotherdir");
		assertNotSame(path1, path2);

		path1 = new TreeSPath("/test/subdir");
		path2 = new TreeSPath("/test/subdir/anotherdir");
		assertNotSame(path1, path2);

		path1 = new TreeSPath("/test/subd/anotherdir");
		path2 = new TreeSPath("/test/subdir/anotherdir");
		assertNotSame(path1, path2);
	}

	public void testCheckFormat() {
		try {
			new TreeSPath("hello");
			fail("Bad format should be rejected");
		} catch (Exception e) {
			// exception expected
		}

		try {
			new TreeSPath("/");
			fail("Bad format should be rejected");
		} catch (Exception e) {
			// exception expected
		}

		assertEquals(new TreeSPath("/test"), new TreeSPath("/test/"));
		assertEquals(new TreeSPath("/test/dir"), new TreeSPath(
				"//test///dir////"));
	}
}
