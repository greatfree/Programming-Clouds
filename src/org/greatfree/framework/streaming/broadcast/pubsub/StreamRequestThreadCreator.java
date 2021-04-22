package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.streaming.message.OutStream;
import org.greatfree.framework.streaming.message.StreamRequest;
import org.greatfree.framework.streaming.message.StreamResponse;

// Created: 03/21/2020, Bing Li
public class StreamRequestThreadCreator implements RequestThreadCreatable<StreamRequest, OutStream, StreamResponse, StreamRequestThread>
{

	@Override
	public StreamRequestThread createRequestThreadInstance(int taskSize)
	{
		return new StreamRequestThread(taskSize);
	}

}
