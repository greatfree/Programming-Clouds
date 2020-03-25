package org.greatfree.concurrency.threading.message;

import org.greatfree.message.ServerMessage;

// Created: 09/12/2019, Bing Li
public class IsAliveResponse extends ServerMessage
{
	private static final long serialVersionUID = 141929997292579053L;
	
	private boolean isAlive;

	public IsAliveResponse(boolean isAlive)
	{
		super(ThreadingMessageType.IS_ALIVE_RESPONSE);
		this.isAlive = isAlive;
	}

	public boolean isAlive()
	{
		return this.isAlive;
	}
}
