package com.gupao.protocol;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvokeProtocol implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2775929686749827128L;

	private String className;
	
	private String methodName;
	
	private Class<?> [] params;
	
	private Object[] values;
	
	
}
