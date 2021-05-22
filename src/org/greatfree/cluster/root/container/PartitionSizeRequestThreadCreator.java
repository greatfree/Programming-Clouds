package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.PartitionSizeRequest;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.cluster.message.PartitionSizeStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 09/09/2020, Bing Li
class PartitionSizeRequestThreadCreator implements RequestQueueCreator<PartitionSizeRequest, PartitionSizeStream, PartitionSizeResponse, PartitionSizeRequestThread>
{

	@Override
	public PartitionSizeRequestThread createInstance(int taskSize)
	{
		return new PartitionSizeRequestThread(taskSize);
	}

}
