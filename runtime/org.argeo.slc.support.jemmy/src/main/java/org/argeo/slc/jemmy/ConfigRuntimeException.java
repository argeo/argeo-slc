package org.argeo.slc.jemmy;

public class ConfigRuntimeException extends UIRuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ConfigRuntimeException(){
		super();
	}
	public ConfigRuntimeException(String s){
		super(s);
	}
	public ConfigRuntimeException(Throwable t){
		super(t);
	}
	public ConfigRuntimeException(String s, Throwable t){
		super(s,t);
	}
}
