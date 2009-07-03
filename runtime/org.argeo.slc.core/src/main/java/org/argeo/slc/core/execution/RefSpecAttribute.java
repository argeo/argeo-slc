package org.argeo.slc.core.execution;

import java.util.List;

public class RefSpecAttribute extends AbstractSpecAttribute implements
		Cloneable {
	private Class<?> targetClass;
	/** Read only. */
	private String targetClassName;
	private Object value = null;

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
