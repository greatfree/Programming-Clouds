package org.greatfree.dsf.cps.enterprise.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 04/22/2020, Bing Li
public class EntityBeanStream extends OutMessageStream<EntityBeanRequest>
{

	public EntityBeanStream(ObjectOutputStream out, Lock lock, EntityBeanRequest message)
	{
		super(out, lock, message);
	}

}
