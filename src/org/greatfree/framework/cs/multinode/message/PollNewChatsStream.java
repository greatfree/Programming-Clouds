package org.greatfree.framework.cs.multinode.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 04/23/2017, Bing Li
public class PollNewChatsStream extends MessageStream<PollNewChatsRequest>
{

	public PollNewChatsStream(ObjectOutputStream out, Lock lock, PollNewChatsRequest message)
	{
		super(out, lock, message);
	}

}
