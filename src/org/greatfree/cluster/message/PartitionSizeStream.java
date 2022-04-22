package org.greatfree.cluster.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 09/09/2020, Bing Li
public class PartitionSizeStream extends MessageStream<PartitionSizeRequest>
{

	public PartitionSizeStream(ObjectOutputStream out, Lock lock, PartitionSizeRequest message)
	{
		super(out, lock, message);
	}

}
