package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.FetchConfig;
import org.greatfree.cache.distributed.store.SortedDistributedReadListStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyCachePointingThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyCachePointingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyCacheReadPointingThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyCacheReadPointingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyCacheReadPointingsThread;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyCacheReadPointingsThreadCreator;
import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.data.MyCachePointingFactory;
import org.greatfree.framework.cps.cache.message.FetchMyCachePointingNotification;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;

// Created: 08/05/2018, Bing Li
public class MySortedDistributedReadCacheStore
{
	private SortedDistributedReadListStore<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, FetchMyCachePointingNotification, PrefetchMyCacheReadPointingsThread, PrefetchMyCacheReadPointingsThreadCreator, PostfetchMyCacheReadPointingThread, PostfetchMyCacheReadPointingThreadCreator, EvictMyCachePointingThread, EvictMyCachePointingThreadCreator> store;

	private MySortedDistributedReadCacheStore()
	{
	}
	
	private static MySortedDistributedReadCacheStore instance = new MySortedDistributedReadCacheStore();

	public static MySortedDistributedReadCacheStore MIDDLESTORE()
	{
		if (instance == null)
		{
			instance = new MySortedDistributedReadCacheStore();
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
		this.store = new SortedDistributedReadListStore.SortedDistributedReadListStoreBuilder<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, FetchMyCachePointingNotification, PrefetchMyCacheReadPointingsThread, PrefetchMyCacheReadPointingsThreadCreator, PostfetchMyCacheReadPointingThread, PostfetchMyCacheReadPointingThreadCreator, EvictMyCachePointingThread, EvictMyCachePointingThreadCreator>()
				.storeKey(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_STORE_KEY)
				.factory(new MyCachePointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeSize(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_STORE_SIZE)
				.cacheSize(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_STORE_DISK_SIZE)
				.descendantComparator(new DescendantListPointingComparator<MyCachePointing>())
				.sortSize(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_STORE_SORT_SIZE)
				.keyCreator(new StoreKeyCreator())
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.prefetchCreator(new PrefetchMyCacheReadPointingsThreadCreator())
				.postfetchCreator(new PostfetchMyCacheReadPointingThreadCreator())
				.evictCreator(new EvictMyCachePointingThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.DISTRIBUTED_CACHE_STORE_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_STORE_PREFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.SORTED_DISTRIBUTED_READ_CACHE_STORE_POSTFETCH_TIMEOUT)
				.build();
	}
	
	public boolean isCacheExisted(String mapKey)
	{
		return this.store.isCacheExisted(new FetchMyCachePointingNotification(mapKey, 0, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}

	public List<MyCachePointing> getTop(String mapKey, int endIndex) throws DistributedListFetchException
	{
		return this.store.getTop(new FetchMyCachePointingNotification(mapKey, 0, endIndex, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}

	public List<MyCachePointing> getRange(String mapKey, int startIndex, int endIndex) throws DistributedListFetchException
	{
		return this.store.getRange(new FetchMyCachePointingNotification(mapKey, startIndex, endIndex, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public void prefetch(FetchMyCachePointingNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		PrefetchMyCachePointingsResponse response = Coordinator.CPS().prefetchPointings(notification.getCacheKey(), notification.getPrefetchStartIndex(), notification.getPrefetchEndIndex());
		this.store.putAllLocally(notification.getCacheKey(), response.getPointings());
	}

	public void postfetch(FetchMyCachePointingNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (notification.getType() == FetchConfig.FETCH_RESOURCE_BY_INDEX)
		{
			System.out.println("1.1) MyPointingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", index = " + notification.getResourceIndex()); 
			PostfetchMyCachePointingByIndexResponse response = Coordinator.CPS().postfetchMyCachePointing(notification.getCacheKey(), notification.getResourceIndex());
			System.out.println("1.2) MyPointingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", index = " + notification.getResourceIndex());
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointing(), notification.isBlocking());
		}
		else if (notification.getType() == FetchConfig.POSTFETCH_RESOURCE_BY_KEY)
		{
			System.out.println("2.1) MyPointingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", key = " + notification.getResourceKey()); 
			PostfetchMyCachePointingByKeyResponse response = Coordinator.CPS().postfetchMyCachePointing(notification.getCacheKey(), notification.getResourceKey());
			System.out.println("2.2) MyPointingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", key = " + notification.getResourceKey());
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointing(), notification.isBlocking());
		}
		else if (notification.getType() == FetchConfig.FETCH_RESOURCES_BY_RANGE)
		{
			PostfetchMyCachePointingsResponse response = Coordinator.CPS().postfetchMyCachePointings(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointings(), notification.isBlocking());
		}
	}
}
