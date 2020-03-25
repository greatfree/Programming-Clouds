package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.MerchandisePollRequest;
import org.greatfree.app.cs.twonode.message.MerchandisePollResponse;
import org.greatfree.app.cs.twonode.message.MerchandisePollStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 07/31/2018, Bing Li
class MerchandisePollRequestThreadCreator implements RequestThreadCreatable<MerchandisePollRequest, MerchandisePollStream, MerchandisePollResponse, MerchandisePollRequestThread>
{

	@Override
	public MerchandisePollRequestThread createRequestThreadInstance(int taskSize)
	{
		return new MerchandisePollRequestThread(taskSize);
	}

}
