package org.greatfree.dip.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.streaming.message.OutStream;
import org.greatfree.dip.streaming.message.StreamRequest;
import org.greatfree.dip.streaming.message.StreamResponse;

// Created: 03/21/2020, Bing Li
public class StreamRequestThreadCreator implements RequestThreadCreatable<StreamRequest, OutStream, StreamResponse, StreamRequestThread>
{

	@Override
	public StreamRequestThread createRequestThreadInstance(int taskSize)
	{
		return new StreamRequestThread(taskSize);
	}

}
