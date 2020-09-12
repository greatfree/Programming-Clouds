package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.PartitionSizeRequest;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.cluster.message.PartitionSizeStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 09/09/2020, Bing Li
class PartitionSizeRequestThreadCreator implements RequestThreadCreatable<PartitionSizeRequest, PartitionSizeStream, PartitionSizeResponse, PartitionSizeRequestThread>
{

	@Override
	public PartitionSizeRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PartitionSizeRequestThread(taskSize);
	}

}
