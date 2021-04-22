package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.store.SortedPrefetchListStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.StoreOverflowException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyPointingListStoreThread;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyPointingListStoreThreadCreator;
import org.greatfree.framework.cps.cache.data.MyCachePointing;
import org.greatfree.framework.cps.cache.data.MyCachePointingFactory;
import org.greatfree.framework.cps.cache.message.PrefetchMyCachePointingListStoreNotification;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;

// Created: 08/03/2018, Bing Li
public class MySortedPrefetchListStore
{
	private SortedPrefetchListStore<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, PrefetchMyCachePointingListStoreNotification, PrefetchMyPointingListStoreThread, PrefetchMyPointingListStoreThreadCreator> store;
	
	private MySortedPrefetchListStore()
	{
	}
	
	private static MySortedPrefetchListStore instance = new MySortedPrefetchListStore();
	
	public static MySortedPrefetchListStore MIDDLE()
	{
		if (instance == null)
		{
			instance = new MySortedPrefetchListStore();
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
		this.store = new SortedPrefetchListStore.PointingPrefetchListStoreBuilder<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, PrefetchMyCachePointingListStoreNotification, PrefetchMyPointingListStoreThread, PrefetchMyPointingListStoreThreadCreator>()
				.storeKey(TestCacheConfig.SORTED_PREFETCH_LIST_STORE_KEY)
				.factory(new MyCachePointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeSize(TestCacheConfig.SORTED_PREFETCH_LIST_STORE_SIZE)
				.cacheSize(TestCacheConfig.SORTED_PREFETCH_LIST_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_PREFETCH_LIST_STORE_OFFHEAP_SIZE)
				.diskSizeInMB(TestCacheConfig.SORTED_PREFETCH_LIST_STORE_DISK_SIZE)
				.descendantComparator(new DescendantListPointingComparator<MyCachePointing>())
				.sortSize(TestCacheConfig.SORTED_PREFETCH_LIST_STORE_SORT_SIZE)
				.keyCreator(new StoreKeyCreator())
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.threadCreator(new PrefetchMyPointingListStoreThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.SORTED_PREFETCH_LIST_STORE_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.SORTED_PREFETCH_LIST_STORE_PREFETCHING_SIZE)
				.build();
	}
	
	public void add(String listKey, MyCachePointing pointing) throws StoreOverflowException
	{
		this.store.addLocally(listKey, pointing);
	}
	
	public void addAll(String listKey, List<MyCachePointing> pointings) throws StoreOverflowException
	{
		this.store.addAllLocally(listKey, pointings);
	}

	public List<MyCachePointing> getRange(String listKey, int startIndex, int endIndex)
	{
		return this.store.getRange(new PrefetchMyCachePointingListStoreNotification(listKey, startIndex, endIndex, this.store.getSize(listKey), this.store.getPrefetchCount()));
	}
	
	public List<MyCachePointing> getTop(String listKey, int endIndex)
	{
		return this.store.getRange(new PrefetchMyCachePointingListStoreNotification(listKey, 0, endIndex, this.store.getSize(listKey), this.store.getPrefetchCount()));
	}

	public MyCachePointing get(String listKey, int index)
	{
		System.out.println("MyPointingPrefetchListStore-get(): cache current size = " + this.store.getSize(listKey));
		return this.store.get(new PrefetchMyCachePointingListStoreNotification(listKey, index, this.store.getSize(listKey), this.store.getPrefetchCount()));
	}

	public void prefetch(PrefetchMyCachePointingListStoreNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		PrefetchMyCachePointingsResponse response = Coordinator.CPS().prefetchPointings(notification.getCacheKey(), notification.getPrefetchStartIndex(), notification.getPrefetchEndIndex());
		System.out.println("MyPointingPrefetchListStore-prefetch(): cache key " + notification.getCacheKey() + ", pointings' size = " + response.getPointings().size());
		this.store.addAllLocally(notification.getCacheKey(), response.getPointings());
	}
}
