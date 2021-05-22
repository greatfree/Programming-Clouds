package org.greatfree.app.cps.terminal;

import org.greatfree.app.cps.message.MerchandiseRequest;
import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.app.cps.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 08/14/2018, Bing Li
public class MerchandiseRequestDBThreadCreator implements RequestQueueCreator<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestDBThread>
{

	@Override
	public MerchandiseRequestDBThread createInstance(int taskSize)
	{
		return new MerchandiseRequestDBThread(taskSize);
	}

}
