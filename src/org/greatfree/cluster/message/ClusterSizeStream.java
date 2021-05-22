package org.greatfree.cluster.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 09/12/2020, Bing Li
public class ClusterSizeStream extends MessageStream<ClusterSizeRequest>
{

	public ClusterSizeStream(ObjectOutputStream out, Lock lock, ClusterSizeRequest message)
	{
		super(out, lock, message);
	}

}
