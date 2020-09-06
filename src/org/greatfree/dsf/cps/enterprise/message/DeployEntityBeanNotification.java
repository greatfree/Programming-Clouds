package org.greatfree.dsf.cps.enterprise.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 04/21/2020, Bing Lis
public class DeployEntityBeanNotification extends ServerMessage
{
	private static final long serialVersionUID = 3885039537236772065L;

	private String clientKey;
	private String className;
	private List<String> parameterTypes;
	private Object[] parameterValues;
	private byte[] classBytes;

	public DeployEntityBeanNotification(String clientKey, String className, List<String> parameterTypes, Object[] parameterValues, byte[] classBytes)
	{
		super(EnterpriseMessageType.DEPLOY_ENTITY_BEAN_NOTIFICATION);
		this.clientKey = clientKey;
		this.className = className;
		this.parameterTypes = parameterTypes;
		this.parameterValues = parameterValues;
		this.classBytes = classBytes;
	}
	
	public String getClientKey()
	{
		return this.clientKey;
	}

	public String geClasstName()
	{
		return this.className;
	}
	
	public List<String> getParameterTypes()
	{
		return this.parameterTypes;
	}
	
	public Object[] getParameterValues()
	{
		return this.parameterValues;
	}
	
	public byte[] getClassBytes()
	{
		return this.classBytes;
	}
}
