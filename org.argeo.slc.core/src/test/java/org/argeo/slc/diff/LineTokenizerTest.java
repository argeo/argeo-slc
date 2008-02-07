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
