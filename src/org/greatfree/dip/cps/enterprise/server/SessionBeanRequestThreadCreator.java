package org.greatfree.dip.cps.enterprise.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.enterprise.message.SessionBeanRequest;
import org.greatfree.dip.cps.enterprise.message.SessionBeanResponse;
import org.greatfree.dip.cps.enterprise.message.SessionBeanStream;

// Created: 04/22/2020, Bing Li
class SessionBeanRequestThreadCreator implements RequestThreadCreatable<SessionBeanRequest, SessionBeanStream, SessionBeanResponse, SessionBeanRequestThread>
{

	@Override
	public SessionBeanRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SessionBeanRequestThread(taskSize);
	}

}
