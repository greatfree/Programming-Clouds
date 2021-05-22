package org.greatfree.framework.cps.enterprise.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.enterprise.message.SessionBeanRequest;
import org.greatfree.framework.cps.enterprise.message.SessionBeanResponse;
import org.greatfree.framework.cps.enterprise.message.SessionBeanStream;

// Created: 04/22/2020, Bing Li
class SessionBeanRequestThreadCreator implements RequestQueueCreator<SessionBeanRequest, SessionBeanStream, SessionBeanResponse, SessionBeanRequestThread>
{

	@Override
	public SessionBeanRequestThread createInstance(int taskSize)
	{
		return new SessionBeanRequestThread(taskSize);
	}

}
