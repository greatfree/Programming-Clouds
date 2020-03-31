package edu.greatfree.cs.multinode.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 04/24/2017, Bing Li
public class PollNewSessionsStream extends OutMessageStream<PollNewSessionsRequest>
{

	public PollNewSessionsStream(ObjectOutputStream out, Lock lock, PollNewSessionsRequest message)
	{
		super(out, lock, message);
	}

}
