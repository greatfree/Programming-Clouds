package org.greatfree.multicast.rp.child;

import org.greatfree.concurrency.NotifierPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 10/14/2018, Bing Li
class ChildAsyncMulticastEventer
{
	// The actor to perform multicasting asynchronously. 09/10/2018, Bing Li
	private NotifierPool<MulticastNotification> actor;
	
	private ThreadPool pool;

	public ChildAsyncMulticastEventer(ChildSyncMulticastor multicastor, ThreadPool pool)
	{
		this.actor = new NotifierPool.NotifierPoolBuilder<MulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildEventActor(multicastor))
				.build();
		
		this.pool = pool;
	}
	
	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
	}
	
	public void notify(MulticastNotification notification)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.notify(notification);
	}
}
