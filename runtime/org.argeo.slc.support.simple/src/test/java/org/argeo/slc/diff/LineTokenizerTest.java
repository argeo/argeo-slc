/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.diff;

import java.util.List;

import junit.framework.TestCase;

public class LineTokenizerTest extends TestCase {
	public void testSimple() throws Exception {
		testAndAssert("a,b,c", new String[] { "a", "b", "c" });
		testAndAssert("hello,bonjour,hallo,priviet", new String[] { "hello",
				"bonjour", "hallo", "priviet" });
	}

	public void testTricky() throws Exception {
		testAndAssert("alone", new String[] { "alone" });
		testAndAssert("", new String[] { "" });

		testAndAssert(",hello,bonjour,hallo,priviet", new String[] { "",
				"hello", "bonjour", "hallo", "priviet" });
		testAndAssert("hello,bonjour,,hallo,priviet", new String[] { "hello",
				"bonjour", "", "hallo", "priviet" });
		testAndAssert("hello,bonjour,hallo,priviet,", new String[] { "hello",
				"bonjour", "hallo", "priviet", "" });
		testAndAssert(",hello,,bonjour,hallo,,,,priviet,", new String[] { "",
				"hello", "", "bonjour", "hallo", "", "", "", "priviet", "" });

		testAndAssert(",,,", new String[] { "", "", "", "" });
	}

	public void testComplex() throws Exception {
		testAndAssert("a#b#c", '#', "", new String[] { "a", "b", "c" });
		testAndAssert("hello!bonjour!hallo!priviet", '!', "", new String[] {
				"hello", "bonjour", "hallo", "priviet" });

		testAndAssert("hello,,bonjour,,hallo,priviet", ',', "<EMPTY>",
				new String[] { "hello", "<EMPTY>", "bonjour", "<EMPTY>",
						"hallo", "priviet" });
	}

	private void testAndAssert(String str, String[] expected) {
		testAndAssert(str, ',', "", expected);
	}

	private void testAndAssert(String str, Character sep, String noValueStr,
			String[] expected) {
		List<String> res = LineTokenizer.tokenize(str, sep, noValueStr);
		assertEquals("Size", expected.length, res.size());
		for (int i = 0; i < res.size(); i++) {
			String token = res.get(i);
			assertEquals("Value@" + i, expected[i], token);
		}
	}
}
