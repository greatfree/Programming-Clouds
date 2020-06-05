package org.greatfree.dip.cps.enterprise.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

/*
 * For the specific case, the class happens to be one instance for one client. 04/23/2020, Bing Li
 */

// Created: 04/21/2020, Bing Li
public class DeploySessionBeanNotification extends ServerMessage
{
	private static final long serialVersionUID = -5449223001366162866L;

	private String clientKey;
	private String className;
	private List<String> parameterTypes;
	private Object[] parameterValues;
	private byte[] classBytes;

	public DeploySessionBeanNotification(String clientKey, String className, List<String> parameterTypes, Object[] parameterValues, byte[] classBytes)
	{
		super(EnterpriseMessageType.DEPLOY_SESSION_BEAN_NOTIFICATION);
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

	public String getClassName()
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
