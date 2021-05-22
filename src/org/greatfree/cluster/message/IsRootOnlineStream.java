package org.greatfree.cluster.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 10/09/2018, Bing Li
public class IsRootOnlineStream extends MessageStream<IsRootOnlineRequest>
{

	public IsRootOnlineStream(ObjectOutputStream out, Lock lock, IsRootOnlineRequest message)
	{
		super(out, lock, message);
	}

}
