package org.greatfree.framework.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/20/2020, Bing Li
public class StreamRequest extends ServerMessage
{
	private static final long serialVersionUID = -2768260849754928501L;

	public StreamRequest()
	{
		super(StreamMessageType.STREAM_REQUEST);
	}

}
