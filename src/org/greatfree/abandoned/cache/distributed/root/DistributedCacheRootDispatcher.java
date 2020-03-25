package org.greatfree.abandoned.cache.distributed.root;

import org.greatfree.client.OutMessageStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

/*
 * The dispatcher is used to serve for the distributed persistable map. When it is necessary to get data from the map, the value that is retrieved from the cluster is received by the dispatcher. 07/03/2017, Bing Li
 */

// Created: 07/03/2017, Bing Li
public class DistributedCacheRootDispatcher extends ServerDispatcher<ServerMessage>
{

//	public DistributedCacheRootDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public DistributedCacheRootDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// TODO Auto-generated method stub
		
	}

}
