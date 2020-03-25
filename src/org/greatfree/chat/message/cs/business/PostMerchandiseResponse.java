package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/04/2017, Bing Li
public class PostMerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = -982609361173667151L;
	
	private boolean isDone;

	public PostMerchandiseResponse(boolean isDone)
	{
		super(BusinessMessageType.POST_MERCHANDISE_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
