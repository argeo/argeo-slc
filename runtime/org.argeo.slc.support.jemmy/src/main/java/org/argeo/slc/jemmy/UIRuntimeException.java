package org.argeo.slc.jemmy;

public class UIRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UIRuntimeException(){
		super();
	}
	public UIRuntimeException(String s){
		super(s);
	}
	public UIRuntimeException(Throwable t){
		super(t);
	}
	public UIRuntimeException(String s, Throwable t){
		super(s,t);
	}
}
