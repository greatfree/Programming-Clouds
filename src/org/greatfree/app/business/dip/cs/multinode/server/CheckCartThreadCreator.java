package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckCartRequest;
import org.greatfree.chat.message.cs.business.CheckCartResponse;
import org.greatfree.chat.message.cs.business.CheckCartStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 12/11/2017, Bing Li
public class CheckCartThreadCreator implements RequestQueueCreator<CheckCartRequest, CheckCartStream, CheckCartResponse, CheckCartThread>
{

	@Override
	public CheckCartThread createInstance(int taskSize)
	{
		return new CheckCartThread(taskSize);
	}

}
