package edu.greatfree.tncs.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import edu.greatfree.tncs.message.MerchandiseRequest;
import edu.greatfree.tncs.message.MerchandiseResponse;
import edu.greatfree.tncs.message.MerchandiseStream;

// Created: 05/01/2019, Bing Li
class MerchandiseRequestThreadCreator implements RequestThreadCreatable<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread>
{

	@Override
	public MerchandiseRequestThread createRequestThreadInstance(int taskSize)
	{
		return new MerchandiseRequestThread(taskSize);
	}

}
