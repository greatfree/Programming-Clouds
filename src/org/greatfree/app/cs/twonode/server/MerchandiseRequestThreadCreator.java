package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.MerchandiseRequest;
import org.greatfree.app.cs.twonode.message.MerchandiseResponse;
import org.greatfree.app.cs.twonode.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 07/27/2018, Bing Li
class MerchandiseRequestThreadCreator implements RequestThreadCreatable<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread>
{

	@Override
	public MerchandiseRequestThread createRequestThreadInstance(int taskSize)
	{
		return new MerchandiseRequestThread(taskSize);
	}

}
