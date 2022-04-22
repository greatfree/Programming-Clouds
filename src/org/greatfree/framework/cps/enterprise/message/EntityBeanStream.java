package org.greatfree.framework.cps.enterprise.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 04/22/2020, Bing Li
public class EntityBeanStream extends MessageStream<EntityBeanRequest>
{

	public EntityBeanStream(ObjectOutputStream out, Lock lock, EntityBeanRequest message)
	{
		super(out, lock, message);
	}

}
