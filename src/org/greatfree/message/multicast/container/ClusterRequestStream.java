package org.greatfree.message.multicast.container;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 09/23/2018, Bing Li
public class ClusterRequestStream extends MessageStream<ClusterRequest>
{

	public ClusterRequestStream(ObjectOutputStream out, Lock lock, ClusterRequest message)
	{
		super(out, lock, message);
	}

}
