package io.xdag.xdagwallet.util;

public class Parameter {
	
	private final String value;
	private final Boolean required;
		
	/**
	 * Constructor.
	 * 
	 * @param value The value.
	 * @param required A boolean indicating if the parameter is required.
	 */
	
	public Parameter(String value, Boolean required) {
		super();
		this.value = value;
		this.required = required;
	}

	/**
	 * Gets the parameter value.
	 * 
	 * @return the parameter value.
	 */
	
	public String getValue() {
		return value;
	}
	
	/**
	 * Gets a boolean indicating if the parameter is required.
	 * 
	 * @return a boolean indicating if the parameter is required
	 */
	
	public Boolean isRequired() {
		return required;
	}
	
}
