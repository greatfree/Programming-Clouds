package org.greatfree.framework.cps.enterprise.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 04/21/2020, Bing Li
public class SessionBeanStream extends MessageStream<SessionBeanRequest>
{

	public SessionBeanStream(ObjectOutputStream out, Lock lock, SessionBeanRequest message)
	{
		super(out, lock, message);
	}

}
