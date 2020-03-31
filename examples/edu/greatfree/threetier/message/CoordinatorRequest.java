package edu.greatfree.threetier.message;

import org.greatfree.message.ServerMessage;

// Created: 07/07/2018, Bing Li
public class CoordinatorRequest extends ServerMessage
{
	private static final long serialVersionUID = -8124364073971327623L;
	
	private String query;

	public CoordinatorRequest(String query)
	{
		super(CPSMessageType.COORDINATOR_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
