package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.store.DistributedStackStore;
import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictStackThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictStackThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchStackThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchStackThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchStackThread;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchStackThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateStackThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateStackThreadCreator;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.data.MyStoreDataFactory;
import org.greatfree.framework.cps.cache.message.FetchStackNotification;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.util.UtilConfig;

// Created: 08/07/2018, Bing Li
public class MyDistributedStackStore
{
	private DistributedStackStore<MyStoreData, MyStoreDataFactory, StackKeyCreator, ReplicateStackThread, ReplicateStackThreadCreator, FetchStackNotification, PrefetchStackThread, PrefetchStackThreadCreator, PostfetchStackThread, PostfetchStackThreadCreator, EvictStackThread, EvictStackThreadCreator> store;
//	private DistributedStackStore<MyStoreData, MyStoreDataFactory, StackKeyCreator, ReplicateStackThread, ReplicateStackThreadCreator, FetchStackNotification, PrefetchStackThread, PrefetchStackThreadCreator, EvictStackThread, EvictStackThreadCreator> store;
	
//	private MyStoreData NO_DATA = null;

	private MyDistributedStackStore()
	{
	}
	
	private static MyDistributedStackStore instance = new MyDistributedStackStore();

	public static MyDistributedStackStore MIDDLESTORE()
	{
		if (instance == null)
		{
			instance = new MyDistributedStackStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.store.dispose();
	}

	public void init()
	{
//		this.store = new DistributedStackStore.DistributedStackStoreBuilder<MyStoreData, MyStoreDataFactory, StackKeyCreator, ReplicateStackThread, ReplicateStackThreadCreator, FetchStackNotification, PrefetchStackThread, PrefetchStackThreadCreator, EvictStackThread, EvictStackThreadCreator>()
		this.store = new DistributedStackStore.DistributedStackStoreBuilder<MyStoreData, MyStoreDataFactory, StackKeyCreator, ReplicateStackThread, ReplicateStackThreadCreator, FetchStackNotification, PrefetchStackThread, PrefetchStackThreadCreator, PostfetchStackThread, PostfetchStackThreadCreator, EvictStackThread, EvictStackThreadCreator>()
				.storeKey(TestCacheConfig.DISTRIBUTED_STACK_STORE_KEY)
				.factory(new MyStoreDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeSize(TestCacheConfig.DISTRIBUTED_STACK_STORE_SIZE)
				.cacheSize(TestCacheConfig.DISTRIBUTED_STACK_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.DISTRIBUTED_STACK_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(TestCacheConfig.DISTRIBUTED_STACK_STORE_DISK_SIZE)
				.keyCreator(new StackKeyCreator())
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.replicateThreadCreator(new ReplicateStackThreadCreator())
				.prefetchThreadCreator(new PrefetchStackThreadCreator())
				.postfetchThreadCreator(new PostfetchStackThreadCreator())
				.evictCreator(new EvictStackThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxTaskSize(5)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.DISTRIBUTED_CACHE_STORE_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.DISTRIBUTED_STACK_PREFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_STACK_STORE_POSTFETCH_TIMEOUT)
				.build();
		
	}
	
	public void push(MyStoreData v)
	{
//		this.store.push(stackKey, pointing);
		this.store.push(v);
	}
	
	public void pushAll(String stackKey, List<MyStoreData> values)
	{
//		this.store.pushAll(new PointingReplicateNotification<MyCachePointing>(stackKey, pointings));
		this.store.pushAll(stackKey, values);
	}
	
	public MyStoreData pop(String stackKey)
	{
		return this.store.pop(new FetchStackNotification(stackKey, this.store.getPrefetchCount(), false));
	}
	
	public List<MyStoreData> popAll(String stackKey, int count) throws DistributedListFetchException
	{
		return this.store.popAll(new FetchStackNotification(stackKey, count, this.store.getPrefetchCount(), false));
	}
	
	public MyStoreData peek(String stackKey)
	{
		return this.store.peek(new FetchStackNotification(stackKey, this.store.getPrefetchCount(), true));
	}
	
	public List<MyStoreData> peekAll(String stackKey, int count) throws DistributedListFetchException
	{
		return this.store.peekAll(new FetchStackNotification(stackKey, count, this.store.getPrefetchCount(), true));
	}
	
	public List<MyStoreData> peekRange(String stackKey, int startIndex, int endIndex)
	{
		try
		{
			return this.store.peekRange(new FetchStackNotification(stackKey, startIndex, endIndex, this.store.getPrefetchCount()));
		}
		catch (IndexOutOfRangeException e)
		{
			return null;
		}
	}
	
	public MyStoreData get(String stackKey, int index)
	{
		try
		{
			return this.store.get(new FetchStackNotification(stackKey, index, this.store.getPrefetchCount()));
		}
		catch (IndexOutOfRangeException e)
		{
			return null;
		}
	}

	public void prefetch(FetchStackNotification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		PopMyStoreDataResponse response;
		if (!notification.isPeeking())
		{
			response = Coordinator.CPS().pop(notification.getCacheKey(), notification.getPrefetchCount());
		}
		else
		{
			if (notification.getStartIndex() != UtilConfig.NO_INDEX)
			{
				System.out.println("MyDistributedStackStore-prefetch(): stackKey = " + notification.getCacheKey() + ", fetchStartIndex = " + notification.getFetchStartIndex() + ", fetchEndIndex = " + notification.getFetchEndIndex());
				response = Coordinator.CPS().peekStack(notification.getCacheKey(), notification.getFetchStartIndex(), notification.getFetchEndIndex());
			}
			else
			{
				response = Coordinator.CPS().peekStack(notification.getCacheKey(), notification.getPrefetchCount());
			}
		}
		if (response.getData() != null)
		{
			this.store.pushAllBottomLocally(notification.getCacheKey(), response.getData());
		}
	}
	
	public void postfetch(FetchStackNotification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		PopMyStoreDataResponse response;
		if (!notification.isPeeking())
		{
//			PopMyStoreDataResponse response = Coordinator.CPS().pop(notification.getCacheKey(), notification.getKickOffCount());
//			response = Coordinator.CPS().pop(notification.getCacheKey(), notification.getPostfetchCount(), false);
			response = Coordinator.CPS().pop(notification.getCacheKey(), notification.getPostfetchCount());
		}
		else
		{
			if (notification.getStartIndex() != UtilConfig.NO_INDEX)
			{
				System.out.println("MyDistributedStackStore-postfetch(): stackKey = " + notification.getCacheKey() + ", fetchStartIndex = " + notification.getFetchStartIndex() + ", fetchEndIndex = " + notification.getFetchEndIndex());
				response = Coordinator.CPS().peekStack(notification.getCacheKey(), notification.getFetchStartIndex(), notification.getFetchEndIndex());
			}
			else
			{
				response = Coordinator.CPS().peekStack(notification.getCacheKey(), notification.getPostfetchCount());
			}
		}
			/*
			if (notification.getKickOffCount() <= UtilConfig.ONE)
			{
				if (response.getData().size() > 0)
				{
					this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getData().get(0));
				}
				else
				{
					this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), NO_DATA);
				}
			}
			else
			{
				this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getData());
			}
			*/
			// Since data in stack needs to be popped up, it is not necessary to save the stack. 08/09/2018, Bing Li
//			this.store.pushAllLocally(notification.getCacheKey(), response.getData());
		this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), notification.getPostfetchCount(), response.getData(), notification.isBlocking());
	}
}
