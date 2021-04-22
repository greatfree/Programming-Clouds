package org.greatfree.testing.cache.enhanced;

import java.util.Calendar;

import org.greatfree.cache.distributed.DistributedMap;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyDataThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyDataThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyDataForDMThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyDataForDMThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyDataThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyDataThreadCreator;
import org.greatfree.framework.cps.cache.data.MyData;
import org.greatfree.framework.cps.cache.data.MyDataFactory;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataNotification;

// Created: 12/24/2018, Bing Li
public class DistributedMapLocalWriting
{

	public static void main(String[] args) throws InterruptedException
	{
		DistributedMap<MyData, MyDataFactory, ReplicateMyDataThread, ReplicateMyDataThreadCreator, PostfetchMyDataNotification, PostfetchMyDataForDMThread, PostfetchMyDataForDMThreadCreator, EvictMyDataThread, EvictMyDataThreadCreator> cache;
		cache = new DistributedMap.DistributedMapBuilder<MyData, MyDataFactory, ReplicateMyDataThread, ReplicateMyDataThreadCreator, PostfetchMyDataNotification, PostfetchMyDataForDMThread, PostfetchMyDataForDMThreadCreator, EvictMyDataThread, EvictMyDataThreadCreator>()
				.factory(new MyDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.DISTRIBUTED_MAP_KEY)
				.cacheSize(TestCacheConfig.DISTRIBUTED_MAP_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.DISTRIBUTED_MAP_DISK_SIZE_IN_MB)
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.replicateCreator(new ReplicateMyDataThreadCreator())
				.postfetchCreator(new PostfetchMyDataForDMThreadCreator())
				.evictCreator(new EvictMyDataThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_MAP_POSTFETCH_TIMEOUT)
				.build();
		
		MyData entry;
		for (int i = 0; i < 100; i++)
		{
			entry = new MyData("key" + i, i, Calendar.getInstance().getTime());
			cache.putLocally(entry.getKey(), entry);
		}

		MyData obj = cache.getLocally("key" + 999);
		
		if (obj != null)
		{
			System.out.println(obj.getKey() + ", " + obj.getNumber() + ", " + obj.getTime());
		}
		else
		{
			System.out.println("obj is NULL");
		}
		
		cache.shutdown();
	}

}
