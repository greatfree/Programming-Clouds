package org.greatfree.dsf.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.streaming.message.OutStream;
import org.greatfree.dsf.streaming.message.StreamRequest;
import org.greatfree.dsf.streaming.message.StreamResponse;

// Created: 03/21/2020, Bing Li
public class StreamRequestThreadCreator implements RequestThreadCreatable<StreamRequest, OutStream, StreamResponse, StreamRequestThread>
{

	@Override
	public StreamRequestThread createRequestThreadInstance(int taskSize)
	{
		return new StreamRequestThread(taskSize);
	}

}
