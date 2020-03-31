package ca.dp.tncs.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.dp.tncs.message.MerchandiseRequest;
import ca.dp.tncs.message.MerchandiseResponse;
import ca.dp.tncs.message.MerchandiseStream;

// Created: 02/22/2020, Bing Li
class MerchandiseRequestThreadCreator implements RequestThreadCreatable<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread>
{

	@Override
	public MerchandiseRequestThread createRequestThreadInstance(int taskSize)
	{
		return new MerchandiseRequestThread(taskSize);
	}

}
