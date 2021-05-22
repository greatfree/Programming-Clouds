package org.greatfree.framework.cps.enterprise.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.enterprise.message.EntityBeanRequest;
import org.greatfree.framework.cps.enterprise.message.EntityBeanResponse;
import org.greatfree.framework.cps.enterprise.message.EntityBeanStream;

// Created: 04/23/2020, Bing Li
class EntityBeanRequestThreadCreator implements RequestQueueCreator<EntityBeanRequest, EntityBeanStream, EntityBeanResponse, EntityBeanRequestThread>
{

	@Override
	public EntityBeanRequestThread createInstance(int taskSize)
	{
		return new EntityBeanRequestThread(taskSize);
	}

}
