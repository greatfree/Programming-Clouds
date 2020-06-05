package edu.greatfree.container.pe.message;

import org.greatfree.message.ServerMessage;

// Created: 06/05/2020, Bing Li
public class PollChatResponse extends ServerMessage
{
	private static final long serialVersionUID = -6231716388277269275L;
	
	private String message;

	public PollChatResponse(String message)
	{
		super(PEChatApplicationID.POLL_CHAT_RESPONSE);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
