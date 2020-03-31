package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

// Created: 03/30/2020, Bing Li
public class DResponse extends ServerMessage
{
	private static final long serialVersionUID = -2134097974430321751L;
	
	private boolean isSucceeded;

	public DResponse(boolean isSucceeded)
	{
		super(ApplicationID.D_RESPONSE);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
