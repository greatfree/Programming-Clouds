package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/31/2018, Bing Li
public class MerchandisePollResponse extends ServerMessage
{
	private static final long serialVersionUID = -1375764771348765704L;
	
	private boolean isAvailable;

	public MerchandisePollResponse(boolean isDone)
	{
		super(BusinessMessageType.MERCHANDISE_POLL_RESPONSE);
		this.isAvailable = isDone;
	}

	public boolean isAvailable()
	{
		return this.isAvailable;
	}
}
