package org.greatfree.multicast.rp.root;

import java.util.Set;

import org.greatfree.concurrency.NotifierPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.multicast.root.ChildMulticastNotification;
import org.greatfree.multicast.root.ChildrenMulticastNotification;
import org.greatfree.multicast.root.NearestMulticastNotification;

// Created: 10/15/2018, Bing Li
class RootAsyncMulticastEventer
{
	private NotifierPool<MulticastNotification> actor;
	private NotifierPool<ChildrenMulticastNotification> childrenActor;
	private NotifierPool<ChildMulticastNotification> childActor;
	private NotifierPool<NearestMulticastNotification> nearestActor;
	private NotifierPool<MulticastNotification> randomActor;
	private ThreadPool pool;

	public RootAsyncMulticastEventer(RootSyncMulticastor multicastor, ThreadPool pool)
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
				.notifier(new RootEventActor(multicastor))
				.build();
		
		this.childrenActor = new NotifierPool.NotifierPoolBuilder<ChildrenMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildrenRootEventActor(multicastor))
				.build();
		
		this.childActor = new NotifierPool.NotifierPoolBuilder<ChildMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildRootEventActor(multicastor))
				.build();
		
		this.nearestActor = new NotifierPool.NotifierPoolBuilder<NearestMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new NearestRootEventActor(multicastor))
				.build();
		
		this.randomActor = new NotifierPool.NotifierPoolBuilder<MulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new RandomRootEventActor(multicastor))
				.build();
		
		this.pool = pool;
	}
	
	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
		this.childrenActor.dispose();
		this.childActor.dispose();
		this.nearestActor.dispose();
		this.randomActor.dispose();
	}
	
	public void asyncNotify(MulticastNotification notification)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.notify(notification);
	}
	
	public void asyncNotify(MulticastNotification notification, Set<String> childrenKeys)
	{
		if (!this.childrenActor.isReady())
		{
			this.pool.execute(this.childrenActor);
		}
		this.childrenActor.notify(new ChildrenMulticastNotification(notification, childrenKeys));
	}
	
	public void asyncNotify(MulticastNotification msg, String childKey)
	{
		if (!this.childActor.isReady())
		{
			this.pool.execute(this.childActor);
		}
		this.childActor.notify(new ChildMulticastNotification(msg, childKey));
	}
	
	public void asyncNearestNotify(String key, MulticastNotification msg)
	{
		if (!this.nearestActor.isReady())
		{
			this.pool.execute(this.nearestActor);
		}
		this.nearestActor.notify(new NearestMulticastNotification(key, msg));
	}

	public void asyncRandomNotify(MulticastNotification msg)
	{
		if (!this.randomActor.isReady())
		{
			this.pool.execute(this.randomActor);
		}
		this.randomActor.notify(msg);
	}
}
