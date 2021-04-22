package org.greatfree.framework.cps.enterprise.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.enterprise.message.EntityBeanRequest;
import org.greatfree.framework.cps.enterprise.message.EntityBeanResponse;
import org.greatfree.framework.cps.enterprise.message.EntityBeanStream;

// Created: 04/23/2020, Bing Li
class EntityBeanRequestThreadCreator implements RequestThreadCreatable<EntityBeanRequest, EntityBeanStream, EntityBeanResponse, EntityBeanRequestThread>
{

	@Override
	public EntityBeanRequestThread createRequestThreadInstance(int taskSize)
	{
		return new EntityBeanRequestThread(taskSize);
	}

}
