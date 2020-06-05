package org.greatfree.dip.cps.enterprise.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 04/23/2020, Bing Li
public class EntityBeanRequest extends ServerMessage
{
	private static final long serialVersionUID = 8634855724156219837L;

	private String clientKey;
	private String className;
	private String methodName;

	private List<String> parameterTypes;
	private Object[] parameterValues;

	public EntityBeanRequest(String clientKey, String className, String methodName, List<String> pTypes, Object[] pValues)
	{
		super(EnterpriseMessageType.ENTITY_BEAN_REQUEST);
		this.clientKey = clientKey;
		this.className = className;
		this.methodName = methodName;
		this.parameterTypes = pTypes;
		this.parameterValues = pValues;
	}
	
	public String getClientKey()
	{
		return this.clientKey;
	}

	public String getClassName()
	{
		return this.className;
	}
	
	public String getMethodName()
	{
		return this.methodName;
	}
	
	public List<String> getParameterTypes()
	{
		return this.parameterTypes;
	}
	
	public Object[] getParameterValues()
	{
		return this.parameterValues;
	}
}
