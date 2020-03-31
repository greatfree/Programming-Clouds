package edu.greatfree.threetier.message;

import org.greatfree.message.ServerMessage;

// Created: 07/06/2018, Bing Li
public class FrontRequest extends ServerMessage
{
	private static final long serialVersionUID = 6948861906677454044L;
	
	private String query;

	public FrontRequest(String query)
	{
		super(CPSMessageType.FRONT_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
