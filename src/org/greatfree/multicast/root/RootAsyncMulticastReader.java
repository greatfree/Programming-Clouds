package org.greatfree.multicast.root;

import org.greatfree.concurrency.NotifierPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/15/2018, Bing Li
final class RootAsyncMulticastReader
{
	private NotifierPool<MulticastRequest> actor;
	private NotifierPool<SizeMulticastRequest> sizeActor;
	private NotifierPool<ChildrenMulticastRequest> childrenActor;
	private NotifierPool<ChildrenSizeMulticastRequest> childrenSizeActor;
	
	private NotifierPool<NearestKeysMulticastRequest> nearestKeysActor;
	private NotifierPool<NearestKeyMulticastRequest> nearestKeyActor;
	private NotifierPool<MulticastRequest> randomActor;
	private NotifierPool<ChildKeyMulticastRequest> childKeyActor;
	
	private NotifierPool<RandomChildrenMulticastRequest> randomChildrenActor;
	
	private ThreadPool pool;
	
	public RootAsyncMulticastReader(RootSyncMulticastor multicastor, ThreadPool pool)
	{
		this.actor = new NotifierPool.NotifierPoolBuilder<MulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new RootReadActor(multicastor))
				.build();

		this.sizeActor = new NotifierPool.NotifierPoolBuilder<SizeMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new SizeRootReadActor(multicastor))
				.build();

		this.childrenActor = new NotifierPool.NotifierPoolBuilder<ChildrenMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildrenRootReadActor(multicastor))
				.build();

		this.childrenSizeActor = new NotifierPool.NotifierPoolBuilder<ChildrenSizeMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildrenSizeRootReadActor(multicastor))
				.build();

		this.nearestKeysActor = new NotifierPool.NotifierPoolBuilder<NearestKeysMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new NearestKeysRootReadActor(multicastor))
				.build();

		this.nearestKeyActor = new NotifierPool.NotifierPoolBuilder<NearestKeyMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new NearestKeyRootReadActor(multicastor))
				.build();

		this.randomActor = new NotifierPool.NotifierPoolBuilder<MulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new RandomRootReadActor(multicastor))
				.build();

		this.childKeyActor = new NotifierPool.NotifierPoolBuilder<ChildKeyMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildKeyRootReadActor(multicastor))
				.build();

		this.randomChildrenActor = new NotifierPool.NotifierPoolBuilder<RandomChildrenMulticastRequest>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new RandomChildrenRootReadActor(multicastor))
				.build();

		this.pool = pool;
	}
	
	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
		this.sizeActor.dispose();
		this.childrenActor.dispose();
		this.childrenSizeActor.dispose();
		this.nearestKeysActor.dispose();
		this.nearestKeyActor.dispose();
		this.randomActor.dispose();
		this.childKeyActor.dispose();
		this.randomChildrenActor.dispose();
	}

	public void asyncRead(MulticastRequest request)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.notify(request);
	}
	
	public void asyncRead(SizeMulticastRequest request)
	{
		if (!this.sizeActor.isReady())
		{
			this.pool.execute(this.sizeActor);
		}
		this.sizeActor.notify(request);
	}
	
	public void asyncRead(ChildrenMulticastRequest request)
	{
		if (!this.childrenActor.isReady())
		{
			this.pool.execute(this.childrenActor);
		}
		this.childrenActor.notify(request);
	}

	public void asyncRead(ChildrenSizeMulticastRequest request)
	{
		if (!this.childrenSizeActor.isReady())
		{
			this.pool.execute(this.childrenSizeActor);
		}
		this.childrenSizeActor.notify(request);
	}
	
	public void asyncRead(NearestKeysMulticastRequest request)
	{
		if (!this.nearestKeysActor.isReady())
		{
			this.pool.execute(this.nearestKeysActor);
		}
		this.nearestKeysActor.notify(request);
	}
	
	public void asyncRead(NearestKeyMulticastRequest request)
	{
		if (!this.nearestKeyActor.isReady())
		{
			this.pool.execute(this.nearestKeyActor);
		}
		this.nearestKeyActor.notify(request);
	}
	
	public void asyncRandomRead(MulticastRequest request)
	{
		if (!this.randomActor.isReady())
		{
			this.pool.execute(this.randomActor);
		}
		this.randomActor.notify(request);
	}
	
	public void asyncRead(ChildKeyMulticastRequest request)
	{
		if (!this.childKeyActor.isReady())
		{
			this.pool.execute(this.childKeyActor);
		}
		this.childKeyActor.notify(request);
	}
	
	public void asynRead(RandomChildrenMulticastRequest request)
	{
		if (!this.randomChildrenActor.isReady())
		{
			this.pool.execute(this.randomChildrenActor);
		}
		this.randomChildrenActor.notify(request);
	}
}
