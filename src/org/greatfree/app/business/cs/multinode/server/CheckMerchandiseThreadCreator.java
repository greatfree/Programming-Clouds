package org.greatfree.app.business.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckMerchandiseRequest;
import org.greatfree.chat.message.cs.business.CheckMerchandiseResponse;
import org.greatfree.chat.message.cs.business.CheckMerchandiseStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 12/05/2017, Bing Li
public class CheckMerchandiseThreadCreator implements RequestQueueCreator<CheckMerchandiseRequest, CheckMerchandiseStream, CheckMerchandiseResponse, CheckMerchandiseThread>
{

	@Override
	public CheckMerchandiseThread createInstance(int taskSize)
	{
		return new CheckMerchandiseThread(taskSize);
	}

}
