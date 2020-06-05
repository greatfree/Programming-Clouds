package org.greatfree.dip.cps.enterprise.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 04/21/2020, Bing Li
public class SessionBeanStream extends OutMessageStream<SessionBeanRequest>
{

	public SessionBeanStream(ObjectOutputStream out, Lock lock, SessionBeanRequest message)
	{
		super(out, lock, message);
	}

}
