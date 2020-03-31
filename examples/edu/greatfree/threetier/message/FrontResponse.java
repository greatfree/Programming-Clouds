package edu.greatfree.threetier.message;

import org.greatfree.message.ServerMessage;

// Created: 07/06/2018, Bing Li
public class FrontResponse extends ServerMessage
{
	private static final long serialVersionUID = -3003432027242659348L;
	
	private String answer;

	public FrontResponse(String answer)
	{
		super(CPSMessageType.FRONT_RESPONSE);
		this.answer = answer;
	}

	public String getAnswer()
	{
		return this.answer;
	}
}
