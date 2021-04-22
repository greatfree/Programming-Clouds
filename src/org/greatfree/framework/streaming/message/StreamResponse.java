package org.greatfree.framework.streaming.message;

import java.util.List;

import org.greatfree.framework.streaming.RegisteredStream;
import org.greatfree.message.ServerMessage;

// Created: 03/20/2020. Bing Li
public class StreamResponse extends ServerMessage
{
	private static final long serialVersionUID = -1907173566458910989L;
	
	private List<RegisteredStream> streams;

	public StreamResponse(List<RegisteredStream> streams)
	{
		super(StreamMessageType.STREAM_RESPONSE);
		this.streams = streams;
	}

	public List<RegisteredStream> getStreams()
	{
		return this.streams;
	}
}
