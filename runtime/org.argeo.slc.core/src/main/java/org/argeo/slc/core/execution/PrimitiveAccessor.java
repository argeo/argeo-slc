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
package org.argeo.slc.core.execution;

/** Abstraction of access to primitive values */
public interface PrimitiveAccessor {
	public final static String TYPE_STRING = "string";
	/**
	 * As of Argeo 1, passwords are NOT stored encrypted, just hidden in the UI,
	 * but stored in plain text in JCR. Use keyring instead.
	 */
	public final static String TYPE_PASSWORD = "password";
	public final static String TYPE_INTEGER = "integer";
	public final static String TYPE_LONG = "long";
	public final static String TYPE_FLOAT = "float";
	public final static String TYPE_DOUBLE = "double";
	public final static String TYPE_BOOLEAN = "boolean";

	public String getType();

	public Object getValue();

	public void setValue(Object value);
}
