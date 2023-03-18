package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.store.DistributedReadStackStore;
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
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchReadStackThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchReadStackThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchReadStackThread;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchReadStackThreadCreator;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.data.MyStoreDataFactory;
import org.greatfree.framework.cps.cache.message.FetchStackNotification;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.util.UtilConfig;

// Created: 08/23/2018, Bing Li
public class MyDistributedReadStackStore
{
	private DistributedReadStackStore<MyStoreData, MyStoreDataFactory, StackKeyCreator, FetchStackNotification, PrefetchReadStackThread, PrefetchReadStackThreadCreator, PostfetchReadStackThread, PostfetchReadStackThreadCreator, EvictStackThread, EvictStackThreadCreator> store;

	private MyDistributedReadStackStore()
	{
	}
	
	private static MyDistributedReadStackStore instance = new MyDistributedReadStackStore();

	public static MyDistributedReadStackStore MIDDLESTORE()
	{
		if (instance == null)
		{
			instance = new MyDistributedReadStackStore();
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
		this.store = new DistributedReadStackStore.DistributedReadStackStoreBuilder<MyStoreData, MyStoreDataFactory, StackKeyCreator, FetchStackNotification, PrefetchReadStackThread, PrefetchReadStackThreadCreator, PostfetchReadStackThread, PostfetchReadStackThreadCreator, EvictStackThread, EvictStackThreadCreator>()
				.storeKey(TestCacheConfig.DISTRIBUTED_READ_STACK_STORE_KEY)
				.factory(new MyStoreDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeSize(TestCacheConfig.DISTRIBUTED_READ_STACK_STORE_SIZE)
				.cacheSize(TestCacheConfig.DISTRIBUTED_READ_STACK_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.DISTRIBUTED_READ_STACK_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(TestCacheConfig.DISTRIBUTED_READ_STACK_STORE_DISK_SIZE)
				.keyCreator(new StackKeyCreator())
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.prefetchThreadCreator(new PrefetchReadStackThreadCreator())
				.postfetchThreadCreator(new PostfetchReadStackThreadCreator())
				.evictCreator(new EvictStackThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.DISTRIBUTED_CACHE_STORE_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.DISTRIBUTED_READ_STACK_PREFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_READ_STACK_STORE_POSTFETCH_TIMEOUT)
				.build();
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
			response = Coordinator.CPS().pop(notification.getCacheKey(), notification.getPostfetchCount());
		}
		else
		{
			if (notification.getStartIndex() != UtilConfig.NO_INDEX)
			{
				response = Coordinator.CPS().peekStack(notification.getCacheKey(), notification.getFetchStartIndex(), notification.getFetchEndIndex());
			}
			else
			{
				response = Coordinator.CPS().peekStack(notification.getCacheKey(), notification.getPostfetchCount());
			}
		}
		this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), notification.getPostfetchCount(), response.getData(), notification.isBlocking());
	}
}
