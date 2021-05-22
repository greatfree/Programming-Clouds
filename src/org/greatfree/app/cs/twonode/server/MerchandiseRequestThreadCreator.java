package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.MerchandiseRequest;
import org.greatfree.app.cs.twonode.message.MerchandiseResponse;
import org.greatfree.app.cs.twonode.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 07/27/2018, Bing Li
class MerchandiseRequestThreadCreator implements RequestQueueCreator<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread>
{

	@Override
	public MerchandiseRequestThread createInstance(int taskSize)
	{
		return new MerchandiseRequestThread(taskSize);
	}

}
