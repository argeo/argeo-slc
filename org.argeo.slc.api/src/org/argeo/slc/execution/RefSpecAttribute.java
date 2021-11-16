package org.argeo.slc.execution;

import java.util.List;

/** A spec attribute whose value is a reference to a full fledged object. */
public class RefSpecAttribute extends AbstractSpecAttribute implements
		Cloneable {
	private static final long serialVersionUID = -3427797452955753574L;
	private transient Class<?> targetClass = String.class;
	/** Read only. */
	private String targetClassName;
	private transient Object value = null;

	/** List to be chosen from */
	private List<RefValueChoice> choices = null;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/** Default is {@link String} */
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
		return "Ref spec attribute [" + targetClass + "]"
				+ (value != null ? "=" + value : "");
	}

}
