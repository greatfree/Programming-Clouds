package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.streaming.message.OutStream;
import org.greatfree.framework.streaming.message.StreamRequest;
import org.greatfree.framework.streaming.message.StreamResponse;

// Created: 03/21/2020, Bing Li
public class StreamRequestThreadCreator implements RequestQueueCreator<StreamRequest, OutStream, StreamResponse, StreamRequestThread>
{

	@Override
	public StreamRequestThread createInstance(int taskSize)
	{
		return new StreamRequestThread(taskSize);
	}

}
