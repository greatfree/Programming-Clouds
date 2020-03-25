package org.greatfree.dip.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.DistributedList;
import org.greatfree.cache.distributed.FetchConfig;
import org.greatfree.cache.distributed.ListReplicateNotification;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.coordinator.evicting.EvictMyUKValueThread;
import org.greatfree.dip.cps.cache.coordinator.evicting.EvictMyUKValueThreadCreator;
import org.greatfree.dip.cps.cache.coordinator.postfetching.PostfetchMyUKValueThread;
import org.greatfree.dip.cps.cache.coordinator.postfetching.PostfetchMyUKValueThreadCreator;
import org.greatfree.dip.cps.cache.coordinator.prefetching.PrefetchMyUKValueThread;
import org.greatfree.dip.cps.cache.coordinator.prefetching.PrefetchMyUKValueThreadCreator;
import org.greatfree.dip.cps.cache.coordinator.replicating.ReplicateMyUKValueThread;
import org.greatfree.dip.cps.cache.coordinator.replicating.ReplicateMyUKValueThreadCreator;
import org.greatfree.dip.cps.cache.message.postfetch.FetchMyUKValueNotification;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValueByIndexResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValuesResponse;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyUKValuesResponse;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.cache.local.MyUKFactory;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2018, Bing Li
public class MyDistributedList
{
	private DistributedList<MyUKValue, MyUKFactory, ReplicateMyUKValueThread, ReplicateMyUKValueThreadCreator, FetchMyUKValueNotification, PrefetchMyUKValueThread, PrefetchMyUKValueThreadCreator, PostfetchMyUKValueThread, PostfetchMyUKValueThreadCreator, EvictMyUKValueThread, EvictMyUKValueThreadCreator> list;

	private MyDistributedList()
	{
	}
	
	private static MyDistributedList instance = new MyDistributedList();
	
	public static MyDistributedList MIDDLE()
	{
		if (instance == null)
		{
			instance = new MyDistributedList();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.list.shutdown();
	}
	
	public void init()
	{
		this.list = new DistributedList.DistributedListBuilder<MyUKValue, MyUKFactory, ReplicateMyUKValueThread, ReplicateMyUKValueThreadCreator, FetchMyUKValueNotification, PrefetchMyUKValueThread, PrefetchMyUKValueThreadCreator, PostfetchMyUKValueThread, PostfetchMyUKValueThreadCreator, EvictMyUKValueThread, EvictMyUKValueThreadCreator>()
				.factory(new MyUKFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.DISTRIBUTED_LIST_KEY)
				.cacheSize(TestCacheConfig.DISTRIBUTED_LIST_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.DISTRIBUTED_LIST_OFF_HEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.DISTRIBUTED_LIST_DISK_SIZE_IN_MB)
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.replicateCreator(new ReplicateMyUKValueThreadCreator())
				.prefetchCreator(new PrefetchMyUKValueThreadCreator())
				.postfetchCreator(new PostfetchMyUKValueThreadCreator())
				.evictCreator(new EvictMyUKValueThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.DISTRIBUTED_LIST_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.DISTRIBUTED_LIST_PREFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_LIST_POSTFETCHING_TIMEOUT)
				.build();
	}
	
	public void add(MyUKValue v)
	{
		this.list.add(new ListReplicateNotification<MyUKValue>(v));
	}
	
	public void addAll(List<MyUKValue> vs)
	{
		this.list.addAll(new ListReplicateNotification<MyUKValue>(this.list.getCacheKey(), vs));
	}
	
	public MyUKValue get(int index)
	{
		return this.list.get(new FetchMyUKValueNotification(this.list.getCacheKey(), index, this.list.getMemCacheSize(), this.list.getPrefetchCount()));
	}
	
	public List<MyUKValue> getTop(int endIndex) throws DistributedListFetchException
	{
		return this.list.getTop(new FetchMyUKValueNotification(this.list.getCacheKey(), 0, endIndex, this.list.getMemCacheSize(), this.list.getPrefetchCount()));
	}
	
	public List<MyUKValue> getRange(int startIndex, int endIndex) throws DistributedListFetchException
	{
		return this.list.getRange(new FetchMyUKValueNotification(this.list.getCacheKey(), startIndex, endIndex, this.list.getMemCacheSize(), this.list.getPrefetchCount()));
	}
	
	public void prefetch(FetchMyUKValueNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		PrefetchMyUKValuesResponse response = Coordinator.CPS().prefetchUK(notification.getPrefetchStartIndex(), notification.getPrefetchEndIndex());
		this.list.addAllLocally(response.getValues());
	}

	public void postfetch(FetchMyUKValueNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (notification.getType() == FetchConfig.FETCH_RESOURCE_BY_INDEX)
		{
			PostfetchMyUKValueByIndexResponse response = Coordinator.CPS().postfetchMyUK(notification.getResourceIndex());
			this.list.savePostfetchedData(notification.getKey(), response.getValue(), notification.isBlocking());
		}
		else if (notification.getType() == FetchConfig.FETCH_RESOURCES_BY_RANGE)
		{
			PostfetchMyUKValuesResponse response = Coordinator.CPS().postfetch(notification.getStartIndex(), notification.getEndIndex());
			this.list.savePostfetchedData(notification.getKey(), response.getValues(), notification.isBlocking());
		}
	
	}
}
