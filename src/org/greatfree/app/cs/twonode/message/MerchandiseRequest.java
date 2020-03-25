package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/27/2018, Bing Li
public class MerchandiseRequest extends ServerMessage
{
	private static final long serialVersionUID = 1929544805496801945L;
	
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
