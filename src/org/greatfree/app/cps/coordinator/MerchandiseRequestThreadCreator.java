package org.greatfree.app.cps.coordinator;

import org.greatfree.app.cps.message.MerchandiseRequest;
import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.app.cps.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 08/14/2018, Bing Li
public class MerchandiseRequestThreadCreator implements RequestQueueCreator<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread>
{

	@Override
	public MerchandiseRequestThread createInstance(int taskSize)
	{
		return new MerchandiseRequestThread(taskSize);
	}

}
