package org.greatfree.app.cps.terminal;

import org.greatfree.app.cps.message.MerchandiseRequest;
import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.app.cps.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 08/14/2018, Bing Li
public class MerchandiseRequestDBThreadCreator implements RequestThreadCreatable<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestDBThread>
{

	@Override
	public MerchandiseRequestDBThread createRequestThreadInstance(int taskSize)
	{
		return new MerchandiseRequestDBThread(taskSize);
	}

}
