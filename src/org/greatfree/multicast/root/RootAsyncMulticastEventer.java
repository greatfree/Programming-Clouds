package org.greatfree.multicast.root;

import java.util.Set;

import org.greatfree.concurrency.AsyncPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/14/2018, Bing Li
class RootAsyncMulticastEventer
{
	private AsyncPool<MulticastNotification> actor;
	private AsyncPool<ChildrenMulticastNotification> childrenActor;
	private AsyncPool<ChildMulticastNotification> childActor;
	private AsyncPool<NearestMulticastNotification> nearestActor;
	private AsyncPool<MulticastNotification> randomActor;
	private AsyncPool<RandomChildrenMulticastNotification> randomChildrenActor;
	private ThreadPool pool;

	public RootAsyncMulticastEventer(RootSyncMulticastor multicastor, ThreadPool pool)
	{
		this.actor = new AsyncPool.ActorPoolBuilder<MulticastNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new RootEventActor(multicastor))
				.build();

		this.childrenActor = new AsyncPool.ActorPoolBuilder<ChildrenMulticastNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new ChildrenRootEventActor(multicastor))
				.build();
		
		this.childActor = new AsyncPool.ActorPoolBuilder<ChildMulticastNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new ChildRootEventActor(multicastor))
				.build();
		
		this.nearestActor = new AsyncPool.ActorPoolBuilder<NearestMulticastNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new NearestRootEventActor(multicastor))
				.build();
		
		this.randomActor = new AsyncPool.ActorPoolBuilder<MulticastNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new RandomRootEventActor(multicastor))
				.build();

		this.randomChildrenActor = new AsyncPool.ActorPoolBuilder<RandomChildrenMulticastNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new RandomChildrenRootEventActor(multicastor))
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
		this.randomChildrenActor.dispose();
	}
	
	public void asyncNotify(MulticastNotification notification)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.perform(notification);
	}
	
	public void asyncNotify(MulticastNotification notification, Set<String> childrenKeys)
	{
		if (!this.childrenActor.isReady())
		{
			this.pool.execute(this.childrenActor);
		}
		this.childrenActor.perform(new ChildrenMulticastNotification(notification, childrenKeys));
	}
	
	public void asyncNotify(MulticastNotification msg, String childKey)
	{
		if (!this.childActor.isReady())
		{
			this.pool.execute(this.childActor);
		}
		this.childActor.perform(new ChildMulticastNotification(msg, childKey));
	}
	
	public void asyncNearestNotify(String key, MulticastNotification msg)
	{
		if (!this.nearestActor.isReady())
		{
			this.pool.execute(this.nearestActor);
		}
		this.nearestActor.perform(new NearestMulticastNotification(key, msg));
	}

	public void asyncRandomNotify(MulticastNotification msg)
	{
		if (!this.randomActor.isReady())
		{
			this.pool.execute(this.randomActor);
		}
		this.randomActor.perform(msg);
	}
	
	public void asyncNotifyWithinNChildren(MulticastNotification notification, int childrenSize)
	{
		if (!this.randomChildrenActor.isReady())
		{
			this.pool.execute(this.randomChildrenActor);
		}
		this.randomChildrenActor.perform(new RandomChildrenMulticastNotification(childrenSize, notification));
	}
}
