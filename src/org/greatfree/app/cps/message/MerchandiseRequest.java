package org.greatfree.app.cps.message;

import org.greatfree.message.ServerMessage;

// Created: 08/14/2018, Bing Li
public class MerchandiseRequest extends ServerMessage
{
	private static final long serialVersionUID = -3888643167848513021L;
	
	private String query;

	public MerchandiseRequest(String query)
	{
		super(BusinessMessageType.MERCHANDISE_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
