package org.greatfree.multicast.rp.child;

import org.greatfree.concurrency.NotifierPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/13/2018, Bing Li
class ChildAsyncMulticastReader
{
	// The actor to perform multicasting asynchronously. 09/10/2018, Bing Li
	private NotifierPool<RPMulticastRequest> actor;
	private ThreadPool pool;

	public ChildAsyncMulticastReader(ChildSyncMulticastor multicastor, ThreadPool pool)
	{
		this.actor = new NotifierPool.NotifierPoolBuilder<RPMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildReadActor(multicastor))
				.build();
		
		this.pool = pool;
	}

	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
	}
	
	public void read(RPMulticastRequest request)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.notify(request);
	}
}
