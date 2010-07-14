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

package org.argeo.slc.core.execution;

import java.util.List;

public class RefSpecAttribute extends AbstractSpecAttribute implements
		Cloneable {
	private static final long serialVersionUID = -3427797452955753574L;
	private Class<?> targetClass;
	/** Read only. */
	private String targetClassName;
	private transient Object value = null;

	private List<RefValueChoice> choices = null;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
		this.targetClassName = targetClass.getName();
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	/** @return can be null */
	public List<RefValueChoice> getChoices() {
		return choices;
	}

	public void setChoices(List<RefValueChoice> choices) {
		this.choices = choices;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		RefSpecAttribute rsa = new RefSpecAttribute();
		rsa.setTargetClass(targetClass);
		rsa.setChoices(choices);
		return rsa;
	}

	@Override
	public String toString() {
		return "Ref spec attribute [" + targetClass + "]";
	}

}
