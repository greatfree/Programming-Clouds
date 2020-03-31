package edu.greatfree.threetier.message;

import org.greatfree.message.ServerMessage;

// Created: 07/07/2018, Bing Li
public class CoordinatorResponse extends ServerMessage
{
	private static final long serialVersionUID = -4788343637780703374L;
	
	private String answer;

	public CoordinatorResponse(String answer)
	{
		super(CPSMessageType.COORDINATOR_RESPONSE);
		this.answer = answer;
	}

	public String getAnswer()
	{
		return this.answer;
	}
}
