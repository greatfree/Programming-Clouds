package org.greatfree.dsf.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/18/2020, Bing Li
public class SubscribeStreamResponse extends ServerMessage
{
	private static final long serialVersionUID = -7631021742019876213L;
	
	private boolean isSucceeded;

	public SubscribeStreamResponse(boolean isSucceeded)
	{
		super(StreamMessageType.SUBSCRIBE_STREAM_RESPONSE);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
