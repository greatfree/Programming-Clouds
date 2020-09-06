package org.greatfree.dsf.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.store.DistributedQueueStore;
import org.greatfree.cache.factory.QueueKeyCreator;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.TestCacheConfig;
import org.greatfree.dsf.cps.cache.coordinator.evicting.EvictQueueThread;
import org.greatfree.dsf.cps.cache.coordinator.evicting.EvictQueueThreadCreator;
import org.greatfree.dsf.cps.cache.coordinator.postfetching.PostfetchQueueThread;
import org.greatfree.dsf.cps.cache.coordinator.postfetching.PostfetchQueueThreadCreator;
import org.greatfree.dsf.cps.cache.coordinator.prefetching.PrefetchQueueThread;
import org.greatfree.dsf.cps.cache.coordinator.prefetching.PrefetchQueueThreadCreator;
import org.greatfree.dsf.cps.cache.coordinator.replicating.ReplicateQueueThread;
import org.greatfree.dsf.cps.cache.coordinator.replicating.ReplicateQueueThreadCreator;
import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.data.MyStoreDataFactory;
import org.greatfree.dsf.cps.cache.message.FetchQueueNotification;
import org.greatfree.dsf.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.UtilConfig;

// Created: 08/13/2018, Bing Li
public class MyDistributedQueueStore
{
	private DistributedQueueStore<MyStoreData, MyStoreDataFactory, QueueKeyCreator, ReplicateQueueThread, ReplicateQueueThreadCreator, FetchQueueNotification, PrefetchQueueThread, PrefetchQueueThreadCreator, PostfetchQueueThread, PostfetchQueueThreadCreator, EvictQueueThread, EvictQueueThreadCreator> store;

	private MyDistributedQueueStore()
	{
	}
	
	private static MyDistributedQueueStore instance = new MyDistributedQueueStore();

	public static MyDistributedQueueStore MIDDLESTORE()
	{
		if (instance == null)
		{
			instance = new MyDistributedQueueStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.store.dispose();
	}

	public void init()
	{
		this.store = new DistributedQueueStore.DistributedQueueStoreBuilder<MyStoreData, MyStoreDataFactory, QueueKeyCreator, ReplicateQueueThread, ReplicateQueueThreadCreator, FetchQueueNotification, PrefetchQueueThread, PrefetchQueueThreadCreator, PostfetchQueueThread, PostfetchQueueThreadCreator, EvictQueueThread, EvictQueueThreadCreator>()
				.storeKey(TestCacheConfig.DISTRIBUTED_QUEUE_STORE_KEY)
				.factory(new MyStoreDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeSize(TestCacheConfig.DISTRIBUTED_QUEUE_STORE_SIZE)
				.cacheSize(TestCacheConfig.DISTRIBUTED_QUEUE_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.DISTRIBUTED_QUEUE_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(TestCacheConfig.DISTRIBUTED_QUEUE_STORE_DISK_SIZE)
				.keyCreator(new QueueKeyCreator())
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.replicateThreadCreator(new ReplicateQueueThreadCreator())
				.prefetchThreadCreator(new PrefetchQueueThreadCreator())
				.postfetchThreadCreator(new PostfetchQueueThreadCreator())
				.evictCreator(new EvictQueueThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.DISTRIBUTED_CACHE_STORE_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.DISTRIBUTED_QUEUE_PREFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_QUEUE_STORE_POSTFETCH_TIMEOUT)
				.build();
	}
	
	public void enqueue(MyStoreData v)
	{
		this.store.enqueue(v);
	}
	
	public void enqueueAll(String queueKey, List<MyStoreData> values)
	{
		this.store.enqueueAll(queueKey, values);
	}
	
	public MyStoreData dequeue(String queueKey)
	{
		return this.store.dequeue(new FetchQueueNotification(queueKey, this.store.getPrefetchCount(), false));
	}
	
	public List<MyStoreData> dequeueAll(String queueKey, int count) throws DistributedListFetchException
	{
		return this.store.dequeueAll(new FetchQueueNotification(queueKey, count, this.store.getPrefetchCount(), false));
	}
	
	public MyStoreData peek(String queueKey)
	{
		return this.store.peek(new FetchQueueNotification(queueKey, this.store.getPrefetchCount(), true));
	}
	
	public List<MyStoreData> peekAll(String queueKey, int count) throws DistributedListFetchException
	{
		return this.store.peekAll(new FetchQueueNotification(queueKey, count, this.store.getPrefetchCount(), true));
	}
	
	public List<MyStoreData> peekRange(String queueKey, int startIndex, int endIndex)
	{
		try
		{
			return this.store.peekRange(new FetchQueueNotification(queueKey, startIndex, endIndex, this.store.getPrefetchCount()));
		}
		catch (IndexOutOfRangeException e)
		{
			return null;
		}
	}
	
	public MyStoreData get(String queueKey, int index)
	{
		try
		{
			return this.store.get(new FetchQueueNotification(queueKey, index, this.store.getPrefetchCount()));
		}
		catch (IndexOutOfRangeException e)
		{
			return null;
		}
	}
	
	public void prefetch(FetchQueueNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		DequeueMyStoreDataResponse response;
		if (!notification.isPeeking())
		{
			response = Coordinator.CPS().dequeue(notification.getCacheKey(), notification.getPrefetchCount());
		}
		else
		{
			if (notification.getStartIndex() != UtilConfig.NO_INDEX)
			{
				response = Coordinator.CPS().peekQueue(notification.getCacheKey(), notification.getFetchStartIndex(), notification.getFetchEndIndex());
			}
			else
			{
				response = Coordinator.CPS().peekQueue(notification.getCacheKey(), notification.getPrefetchCount());
			}
		}
		int index = 0;
		System.out.println("=================================");
		for (MyStoreData entry : response.getData())
		{
			System.out.println(index++ + ") key = " + entry.getKey() + ", value = " + entry.getValue());
		}
		System.out.println("=================================");
		System.out.println("MyDistributedQueueStore-prefetch(): " + response.getData().size() + " data is prefetched ...");
		this.store.enqueueLocally(notification.getCacheKey(), response.getData());
	}
	
	public void postfetch(FetchQueueNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		DequeueMyStoreDataResponse response;
		if (!notification.isPeeking())
		{
			response = Coordinator.CPS().dequeue(notification.getCacheKey(), notification.getPostfetchCount());
		}
		else
		{
			if (notification.getStartIndex() != UtilConfig.NO_INDEX)
			{
				response = Coordinator.CPS().peekQueue(notification.getCacheKey(), notification.getFetchStartIndex(), notification.getFetchEndIndex());
			}
			else
			{
				response = Coordinator.CPS().peekQueue(notification.getCacheKey(), notification.getPostfetchCount());
			}
		}
		int index = 0;
		System.out.println("=================================");
		for (MyStoreData entry : response.getData())
		{
			System.out.println(index++ + ") key = " + entry.getKey() + ", value = " + entry.getValue());
		}
		System.out.println("=================================");
		System.out.println("MyDistributedQueueStore-postfetch(): " + response.getData().size() + " data is postfetched ...");
		this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), notification.getPostfetchCount(), response.getData(), notification.isBlocking());
	}
	
}
