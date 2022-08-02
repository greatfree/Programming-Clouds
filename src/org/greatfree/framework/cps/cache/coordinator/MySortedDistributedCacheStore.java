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
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyCachePointingThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyCachePointingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyCachePointingThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyCachePointingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyCachePointingsThread;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyCachePointingsThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyCachePointingThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyCachePointingThreadCreator;
import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.data.MyCachePointingFactory;
import org.greatfree.framework.cps.cache.message.FetchMyCachePointingNotification;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;

// Created: 07/22/2018, Bing Li
public class MySortedDistributedCacheStore
{
	private SortedDistributedListStore<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, ReplicateMyCachePointingThread, ReplicateMyCachePointingThreadCreator, FetchMyCachePointingNotification, PrefetchMyCachePointingsThread, PrefetchMyCachePointingsThreadCreator, PostfetchMyCachePointingThread, PostfetchMyCachePointingThreadCreator, EvictMyCachePointingThread, EvictMyCachePointingThreadCreator> store;
	
	/*
	 * If the option is true, the cache interacts with SortedTerminalMapStore. 09/04/2018, Bing Li
	 * 
	 * if the option is false, the cache interacts with SortedTerminalListStore. 09/04/2018, Bing Li
	 * 
	 */
	private boolean isTerminalMap;
	
	private MySortedDistributedCacheStore()
	{
	}
	
	private static MySortedDistributedCacheStore instance = new MySortedDistributedCacheStore();

	public static MySortedDistributedCacheStore MIDDLESTORE()
	{
		if (instance == null)
		{
			instance = new MySortedDistributedCacheStore();
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
		this.store = new SortedDistributedListStore.SortedDistributedListStoreBuilder<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, ReplicateMyCachePointingThread, ReplicateMyCachePointingThreadCreator, FetchMyCachePointingNotification, PrefetchMyCachePointingsThread, PrefetchMyCachePointingsThreadCreator, PostfetchMyCachePointingThread, PostfetchMyCachePointingThreadCreator, EvictMyCachePointingThread, EvictMyCachePointingThreadCreator>()
				.storeKey(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_STORE_KEY)
				.factory(new MyCachePointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeSize(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_STORE_SIZE)
				.cacheSize(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_STORE_DISK_SIZE)
				.descendantComparator(new DescendantListPointingComparator<MyCachePointing>())
				.sortSize(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_STORE_SORT_SIZE)
				.keyCreator(new StoreKeyCreator())
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.replicateThreadCreator(new ReplicateMyCachePointingThreadCreator())
				.prefetchThreadCreator(new PrefetchMyCachePointingsThreadCreator())
				.postfetchThreadCreator(new PostfetchMyCachePointingThreadCreator())
				.evictCreator(new EvictMyCachePointingThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.DISTRIBUTED_CACHE_STORE_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_STORE_PREFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.SORTED_DISTRIBUTED_CACHE_STORE_POSTFETCH_TIMEOUT)
				.build();
		
//		this.isTerminalMap = true;
		this.isTerminalMap = false;
	}
	
	public boolean isTerminalMap()
	{
		return this.isTerminalMap;
	}

	public void put(String mapKey, MyCachePointing pointing)
	{
		this.store.put(new PointingReplicateNotification<MyCachePointing>(mapKey, pointing));
	}
	
	public void putAll(String mapKey, List<MyCachePointing> pointings)
	{
		this.store.putAll(new PointingReplicateNotification<MyCachePointing>(mapKey, pointings));
	}
	
	public boolean containsKey(String mapKey, String resourceKey)
	{
		return this.store.containsKey(new FetchMyCachePointingNotification(mapKey, resourceKey));
	}
	
	public MyCachePointing getMax(String mapKey)
	{
		return this.store.getMaxValue(new FetchMyCachePointingNotification(mapKey, 0, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public MyCachePointing get(String mapKey, String resourceKey)
	{
		return this.store.getValueByKey(new FetchMyCachePointingNotification(mapKey, resourceKey));
	}
	
	public boolean isCacheExisted(String mapKey)
	{
		return this.store.isListExisted(new FetchMyCachePointingNotification(mapKey, 0, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public boolean isCacheEmpty(String mapKey)
	{
		return this.store.isCacheEmpty(new FetchMyCachePointingNotification(mapKey, 0, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	public MyCachePointing get(String mapKey, int index)
	{
		return this.store.getValueByIndex(new FetchMyCachePointingNotification(mapKey, index, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
//	public List<MyCachePointing> getTop(String mapKey, int endIndex)
	public List<MyCachePointing> getTop(String mapKey, int endIndex) throws DistributedListFetchException
	{
		return this.store.getTop(new FetchMyCachePointingNotification(mapKey, 0, endIndex, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
//	public List<MyCachePointing> getRange(String mapKey, int startIndex, int endIndex)
	public List<MyCachePointing> getRange(String mapKey, int startIndex, int endIndex) throws DistributedListFetchException
	{
		return this.store.getRange(new FetchMyCachePointingNotification(mapKey, startIndex, endIndex, this.store.getCacheSize(mapKey), this.store.getPrefetchCount()));
	}
	
	/*
	 * Prefetching got a bug. Data should be prefetched from the currentSize, not from endIndex + 1! I need to test all of the prefetching of the distributed caches. 07/27/2018, Bing Li
	 */
	
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
			/*
			if (response.getPointing() != null)
			{
				System.out.println("1.3) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
//				this.store.put(notification.getCacheKey(), response.getPointing());
				this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointing());
				System.out.println("1.4) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
			}
			*/
//			System.out.println("1.3) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
//			this.store.put(notification.getCacheKey(), response.getPointing());
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointing(), notification.isBlocking());
//			System.out.println("1.4) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
		}
		else if (notification.getType() == FetchConfig.POSTFETCH_RESOURCE_BY_KEY)
		{
			System.out.println("2.1) MyPointingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", key = " + notification.getResourceKey()); 
			PostfetchMyCachePointingByKeyResponse response = Coordinator.CPS().postfetchMyCachePointing(notification.getCacheKey(), notification.getResourceKey());
			System.out.println("2.2) MyPointingDistributedCacheStore-postfetch(): cache key = " + notification.getCacheKey() + ", key = " + notification.getResourceKey());
			/*
			if (response.getPointing() != null)
			{
//				this.store.put(notification.getCacheKey(), response.getPointing());
				System.out.println("2.3) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
				this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointing());
				System.out.println("2.4) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
			}
			*/
//			System.out.println("2.3) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointing(), notification.isBlocking());
//			System.out.println("2.4) MyPointingDistributedCacheStore-postfetch(): cache key = " + response.getPointing().getCacheKey() + ", points = " + response.getPointing().getPoints()); 
		}
		else if (notification.getType() == FetchConfig.FETCH_RESOURCES_BY_RANGE)
		{
			PostfetchMyCachePointingsResponse response = Coordinator.CPS().postfetchMyCachePointings(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
			/*
			if (response.getPointings() != null)
			{
//				this.store.putAll(notification.getCacheKey(), response.getPointings());
				this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointings());
			}
			*/
			this.store.savePostfetchedData(notification.getKey(), notification.getCacheKey(), response.getPointings(), notification.isBlocking());
		}
	}
}
