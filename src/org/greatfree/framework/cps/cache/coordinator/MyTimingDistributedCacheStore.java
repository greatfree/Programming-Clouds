package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.FetchConfig;
import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.cache.distributed.store.SortedDistributedListStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyCacheTimingThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyCacheTimingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyCacheTimingThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyCacheTimingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyCacheTimingsThread;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyCacheTimingsThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyCacheTimingThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyCacheTimingThreadCreator;
import org.greatfree.framework.cps.cache.data.MyCacheTiming;
import org.greatfree.framework.cps.cache.data.MyCacheTimingFactory;
import org.greatfree.framework.cps.cache.message.FetchMyCacheTimingNotification;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;

// Created: 08/19/2018, Bing Li
public class MyTimingDistributedCacheStore
{
//	private TimingDistributedCacheStore<MyCacheTiming, MyCacheTimingFactory, StoreKeyCreator, DescendantListTimingComparator<MyCacheTiming>, ReplicateMyCacheTimingThread, ReplicateMyCacheTimingThreadCreator, FetchMyCacheTimingNotification, PrefetchMyCacheTimingsThread, PrefetchMyCacheTimingsThreadCreator, PostfetchMyCacheTimingThread, PostfetchMyCacheTimingThreadCreator, EvictMyCacheTimingThread, EvictMyCacheTimingThreadCreator> store;

	private SortedDistributedListStore<MyCacheTiming, MyCacheTimingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCacheTiming>, ReplicateMyCacheTimingThread, ReplicateMyCacheTimingThreadCreator, FetchMyCacheTimingNotification, PrefetchMyCacheTimingsThread, PrefetchMyCacheTimingsThreadCreator, PostfetchMyCacheTimingThread, PostfetchMyCacheTimingThreadCreator, EvictMyCacheTimingThread, EvictMyCacheTimingThreadCreator> store;

	private MyTimingDistributedCacheStore()
	{
	}
	
	private static MyTimingDistributedCacheStore instance = new MyTimingDistributedCacheStore();

	public static MyTimingDistributedCacheStore MIDDLESTORE()
	{
		if (instance == null)
		{
			instance = new MyTimingDistributedCacheStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.store.shutdown();
	}


	public void init()
	{
		this.store = new SortedDistributedListStore.SortedDistributedListStoreBuilder<MyCacheTiming, MyCacheTimingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCacheTiming>, ReplicateMyCacheTimingThread, ReplicateMyCacheTimingThreadCreator, FetchMyCacheTimingNotification, PrefetchMyCacheTimingsThread, PrefetchMyCacheTimingsThreadCreator, PostfetchMyCacheTimingThread, PostfetchMyCacheTimingThreadCreator, EvictMyCacheTimingThread, EvictMyCacheTimingThreadCreator>()
				.storeKey(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_STORE_KEY)
				.factory(new MyCacheTimingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeSize(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_STORE_SIZE)
				.cacheSize(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_STORE_DISK_SIZE)
				.descendantComparator(new DescendantListPointingComparator<MyCacheTiming>())
				.sortSize(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_STORE_SORT_SIZE)
				.keyCreator(new StoreKeyCreator())
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.replicateThreadCreator(new ReplicateMyCacheTimingThreadCreator())
				.prefetchThreadCreator(new PrefetchMyCacheTimingsThreadCreator())
				.postfetchThreadCreator(new PostfetchMyCacheTimingThreadCreator())
				.evictCreator(new EvictMyCacheTimingThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.DISTRIBUTED_CACHE_STORE_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_STORE_PREFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.TIMING_DISTRIBUTED_CACHE_STORE_POSTFETCH_TIMEOUT)
				.build();
	}

	public void put(String mapKey, MyCacheTiming timing)
	{
		this.store.put(new PointingReplicateNotification<MyCacheTiming>(mapKey, timing));
	}
	
	public void putAll(String mapKey, List<MyCacheTiming> timings)
	{
		this.store.putAll(new PointingReplicateNotification<MyCacheTiming>(mapKey, timings));
	}
	
	public boolean containsKey(String mapKey, String resourceKey)
	{
		return this.store.containsKey(new FetchMyCacheTimingNotification(mapKey, resourceKey));
	}
	
	public MyCacheTiming getMax(String mapKey)
	{
		return this.store.getMaxValue(new FetchMyCacheTimingNotification(mapKey, 0, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public MyCacheTiming get(String mapKey, String resourceKey)
	{
		return this.store.getValueByKey(new FetchMyCacheTimingNotification(mapKey, resourceKey));
	}
	
	public boolean isCacheExisted(String mapKey)
	{
		return this.store.isListExisted(new FetchMyCacheTimingNotification(mapKey, 0, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public boolean isCacheEmpty(String mapKey)
	{
		return this.store.isCacheEmpty(new FetchMyCacheTimingNotification(mapKey, 0, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public MyCacheTiming get(String mapKey, int index)
	{
		return this.store.getValueByIndex(new FetchMyCacheTimingNotification(mapKey, index, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public List<MyCacheTiming> getTop(String mapKey, int endIndex) throws DistributedListFetchException
	{
		return this.store.getTop(new FetchMyCacheTimingNotification(mapKey, 0, endIndex, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public List<MyCacheTiming> getRange(String mapKey, int startIndex, int endIndex) throws DistributedListFetchException
	{
		return this.store.getRange(new FetchMyCacheTimingNotification(mapKey, startIndex, endIndex, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	/*
	 * Prefetching got a bug. Data should be prefetched from the currentSize, not from endIndex + 1! I need to test all of the prefetching of the distributed caches. 07/27/2018, Bing Li
	 */
	
	public void prefetch(FetchMyCacheTimingNotification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		PrefetchMyCachePointingsResponse response = Coordinator.CPS().prefetchTimings(notification.getCacheKey(), notification.getPrefetchStartIndex(), notification.getPrefetchEndIndex());
		this.store.putAllLocally(notification.getCacheKey(), response.getTimings());
	}

	public void postfetch(FetchMyCacheTimingNotification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		if (notification.getType() == FetchConfig.FETCH_RESOURCE_BY_INDEX)
		{
			System.out.println("1.1) MyTimingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", index = " + notification.getResourceIndex()); 
			PostfetchMyCachePointingByIndexResponse response = Coordinator.CPS().postfetchMyCacheTiming(notification.getCacheKey(), notification.getResourceIndex());
			System.out.println("1.2) MyTimingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", index = " + notification.getResourceIndex());
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getTiming(), notification.isBlocking());
		}
		else if (notification.getType() == FetchConfig.POSTFETCH_RESOURCE_BY_KEY)
		{
			System.out.println("2.1) MyTimingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", key = " + notification.getResourceKey()); 
			PostfetchMyCachePointingByKeyResponse response = Coordinator.CPS().postfetchMyCacheTiming(notification.getCacheKey(), notification.getResourceKey());
			System.out.println("2.2) MyTimingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", key = " + notification.getResourceKey());
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getTiming(), notification.isBlocking());
		}
		else if (notification.getType() == FetchConfig.FETCH_RESOURCES_BY_RANGE)
		{
			PostfetchMyCachePointingsResponse response = Coordinator.CPS().postfetchMyCacheTimings(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getTimings(), notification.isBlocking());
		}
	}
}
