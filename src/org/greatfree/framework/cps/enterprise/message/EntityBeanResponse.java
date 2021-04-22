package org.greatfree.framework.cps.enterprise.message;

import org.greatfree.message.ServerMessage;

// Created: 04/23/2020, Bing Li
public class EntityBeanResponse extends ServerMessage
{
	private static final long serialVersionUID = -2258469488514873710L;
	
	private Object returnValue;

	public EntityBeanResponse(Object value)
	{
		super(EnterpriseMessageType.ENTITY_BEAN_RESPONSE);
		this.returnValue = value;
	}
	
	public Object getReturnValue()
	{
		return this.returnValue;
	}
}
