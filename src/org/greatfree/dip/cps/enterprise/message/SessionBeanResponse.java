package org.greatfree.dip.cps.enterprise.message;

import org.greatfree.message.ServerMessage;

// Created: 04/21/2020, Bing Li
public class SessionBeanResponse extends ServerMessage
{
	private static final long serialVersionUID = -9073610123367963271L;
	
	private Object returnValue;

	public SessionBeanResponse(Object value)
	{
		super(EnterpriseMessageType.SESSION_BEAN_RESPONSE);
		this.returnValue = value;
	}
	
	public Object getReturnValue()
	{
		return this.returnValue;
	}
}
