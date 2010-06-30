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

package org.argeo.slc;

/** Exception for unsupported features or actions. */
public class UnsupportedException extends SlcException {
	static final long serialVersionUID = 1l;

	/** Action not supported. */
	public UnsupportedException() {
		this("Action not supported");
	}

	/** Constructor with a message. */
	public UnsupportedException(String message) {
		super(message);
	}

	/**
	 * Constructor generating a message.
	 * 
	 * @param nature
	 *            the nature of the unsupported object
	 * @param obj
	 *            the object itself (its class name will be used in message)
	 */
	public UnsupportedException(String nature, Object obj) {
		super("Unsupported " + nature + ": "
				+ (obj != null ? obj.getClass() : "[object is null]"));
	}

	/**
	 * Constructor generating a message.
	 * 
	 * @param nature
	 *            the nature of the unsupported object
	 * @param clss
	 *            the class itself (will be used in message)
	 */
	public UnsupportedException(String nature, Class<?> clss) {
		super("Unsupported " + nature + ": " + clss);
	}

	/**
	 * Constructor generating a message.
	 * 
	 * @param nature
	 *            the nature of the unsupported object
	 * @param value
	 *            the problematic value itself
	 */
	public UnsupportedException(String nature, String value) {
		super("Unsupported " + nature + ": " + value);
	}

}
