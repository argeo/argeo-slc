/*
 * Copyright (C) 2007-2012 Argeo GmbH
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

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a string as a vector of strings according to a separator, dealing
 * properly with missing values. This is intended to be used instead of the
 * standard StringTokenizer, which does not deal well with empty values.
 * Contrary to the StringTokenizer the provided String is parsed in the
 * constructor and the values stored as a property. This should therefore not be
 * used to parse long strings. No reference to the argument passed in
 * constructor is kept.
 */
public class LineTokenizer {
	private final List<String> tokens;

	/** Complete constructor. */
	public LineTokenizer(String stringToParse, Character separator,
			String noValueString) {
		this.tokens = parse(stringToParse, separator, noValueString);
	}

	/**
	 * Parse the string as a vector of strings. Can be overridden in order to
	 * provide another implementation.
	 */
	protected List<String> parse(final String stringToParse,
			final char separator, final String noValueString) {
		// Init
		final int NULL = -1;
		List<String> res = new ArrayList<String>();
		final char[] array = stringToParse.toCharArray();
		int lastSeparatorIndex = NULL;

		// Loop on chars
		for (int currIndex = 0; currIndex < array.length; currIndex++) {
			char c = array[currIndex];
			if (c == separator) {
				if (currIndex == 0) {
					// first char is a separator
					res.add(new String(noValueString));
					lastSeparatorIndex = 0;
				} else if (lastSeparatorIndex == NULL) {
					// first separator found
					res.add(new String(array, 0, currIndex));
					lastSeparatorIndex = currIndex;
				} else if (lastSeparatorIndex != NULL
						&& (lastSeparatorIndex == (currIndex - 1))) {
					// consecutive separators
					res.add(new String(noValueString));
					lastSeparatorIndex = currIndex;
				} else {
					// simple case
					res.add(new String(array, lastSeparatorIndex + 1, currIndex
							- lastSeparatorIndex - 1));
					lastSeparatorIndex = currIndex;
				}
			}
		}

		// Finalize
		if (lastSeparatorIndex == NULL) {
			// no separator found
			res.add(new String(stringToParse));
		} else if (lastSeparatorIndex == (array.length - 1)) {
			// last char is a separator
			res.add(new String(noValueString));
		} else {
			// last token
			res.add(new String(array, lastSeparatorIndex + 1, array.length
					- lastSeparatorIndex - 1));
		}
		return res;
	}

	/** The tokens. */
	public List<String> getTokens() {
		return tokens;
	}

	/** Parse */
	public static List<String> tokenize(String stringToParse,
			Character separator, String noValueString) {
		LineTokenizer lt = new LineTokenizer(stringToParse, separator,
				noValueString);
		return lt.getTokens();
	}

	/** Parse, using the empty string as no value string. */
	public static List<String> tokenize(String stringToParse,
			Character separator) {
		return tokenize(stringToParse, separator, "");
	}

}
