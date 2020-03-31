package edu.greatfree.cs.multinode.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 04/23/2017, Bing Li
public class PollNewChatsStream extends OutMessageStream<PollNewChatsRequest>
{

	public PollNewChatsStream(ObjectOutputStream out, Lock lock, PollNewChatsRequest message)
	{
		super(out, lock, message);
	}

}
