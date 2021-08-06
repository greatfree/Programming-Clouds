package org.greatfree.concurrency.threading.message;

import org.greatfree.message.container.Request;

// Created: 09/12/2019, Bing Li
public class IsAliveRequest extends Request
{
	private static final long serialVersionUID = -210413779451033051L;
	
	private String threadKey;

	public IsAliveRequest(String threadKey)
	{
		super(ATMMessageType.IS_ALIVE_REQUEST);
		this.threadKey = threadKey;
	}

	public String getThreadKey()
	{
		return this.threadKey;
	}
}

