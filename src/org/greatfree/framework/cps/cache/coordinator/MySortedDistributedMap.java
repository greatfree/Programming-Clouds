package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.cache.distributed.SortedDistributedMap;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyPointingThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyPointingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyPointingMapThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyPointingMapThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyPointingThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyPointingThreadCreator;
import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.data.MyPointingFactory;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingMapNotification;
import org.greatfree.util.UtilConfig;

// Created: 07/19/2018, Bing Li
public class MySortedDistributedMap
{
	private SortedDistributedMap<MyPointing, MyPointingFactory, DescendantListPointingComparator<MyPointing>, ReplicateMyPointingThread, ReplicateMyPointingThreadCreator, PostfetchMyPointingMapNotification, PostfetchMyPointingMapThread, PostfetchMyPointingMapThreadCreator, EvictMyPointingThread, EvictMyPointingThreadCreator> cache;

	private MySortedDistributedMap()
	{
	}
	
	private static MySortedDistributedMap instance = new MySortedDistributedMap();
	
	public static MySortedDistributedMap MIDDLE()
	{
		if (instance == null)
		{
			instance = new MySortedDistributedMap();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException, IOException
	{
		this.cache.shutdown();
	}
	
	public void init()
	{
		this.cache = new SortedDistributedMap.SortedDistributedMapBuilder<MyPointing, MyPointingFactory, DescendantListPointingComparator<MyPointing>, ReplicateMyPointingThread, ReplicateMyPointingThreadCreator, PostfetchMyPointingMapNotification, PostfetchMyPointingMapThread, PostfetchMyPointingMapThreadCreator, EvictMyPointingThread, EvictMyPointingThreadCreator>()
				.factory(new MyPointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.SORTED_DISTRIBUTED_MAP_KEY)
				.cacheSize(TestCacheConfig.SORTED_DISTRIBUTED_MAP_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_MAP_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_MAP_DISK_SIZE_IN_MB)
				.comp(new DescendantListPointingComparator<MyPointing>())
				.sortSize(TestCacheConfig.SORTED_DISTRIBUTED_MAP_SORT_SIZE)
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.replicateCreator(new ReplicateMyPointingThreadCreator())
				.postfetchCreator(new PostfetchMyPointingMapThreadCreator())
				.evictCreator(new EvictMyPointingThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.threadPool(SharedThreadPool.SHARED().getPool())
				.postfetchTimeout(TestCacheConfig.SORTED_DISTRIBUTED_MAP_POSTFETCHING_TIMEOUT)
				.build();
	}
	
	public void put(MyPointing pointing)
	{
		this.cache.put(new PointingReplicateNotification<MyPointing>(pointing));
	}
	
	public void putAll(List<MyPointing> pointings)
	{
		this.cache.putAll(new PointingReplicateNotification<MyPointing>(this.cache.getCacheKey(), pointings));
	}

	/*
	public void save(String key, MyPointing pointing)
	{
		this.cache.put(pointing);
		this.cache.signal(key);
	}
	
	public void save(String key, List<MyPointing> pointings)
	{
		this.cache.savePostfetchedData(key, pointings);
		this.cache.putAll(pointings);
		this.cache.signal(key);
	}
	*/
	
	public MyPointing getPointing(String key)
	{
		return this.cache.get(new PostfetchMyPointingMapNotification(key));
	}
	
	public MyPointing getMinPointing()
	{
//		String minKey = this.cache.getMinimumKey();
//		System.out.println("MyPointingDistributedMap-getMinPointing(): minKey = " + minKey);
//		return this.cache.get(new PostfetchMyPointingMapNotification(minKey));
		return this.cache.getMinPointing(new PostfetchMyPointingMapNotification());
	}
	
	public MyPointing getMaxPointing()
	{
//		String maxKey = this.cache.getMaximumKey();
		return this.cache.get(new PostfetchMyPointingMapNotification(this.cache.getMaximumKey()));
	}

	public void postfetch(PostfetchMyPointingMapNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (!notification.getResourceKey().equals(UtilConfig.EMPTY_STRING))
		{
			System.out.println("MyPointingDistributedMap-getMinPointing(): postfetching ... resourceKey = " + notification.getResourceKey());
			PostfetchMyPointingByKeyResponse pResponse = Coordinator.CPS().postfetchMyPointing(notification.getResourceKey());
			System.out.println("MyPointingDistributedMap-getMinPointing(): postfetched ... resourceKey = " + notification.getResourceKey());
			/*
			if (pResponse.getPointing() != null)
			{
				this.cache.savePostfetchedData(notification.getKey(), pResponse.getPointing());
			}
			*/
			this.cache.savePostfetchedData(notification.getKey(), pResponse.getPointing(), notification.isBlocking());
		}
		else
		{
			PostfetchMinMyPointingResponse response = Coordinator.CPS().postfetchMinMyPointing();
			this.cache.savePostfetchedData(notification.getKey(), response.getPointing(), notification.isBlocking());
		}
	}
}
