package org.greatfree.cache.distributed.container;

import java.io.IOException;

import org.greatfree.cache.StoreElement;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.TestCacheConfig;

// Created: 01/21/2018, Bing Li
public class DistributedMapContainer<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>>
{
	private DistributedMap<Value, Factory, MapRelicateThread<Value>, MapRelicateThreadCreator<Value>, ContainerMapPostfetchNotification, MapPostfetchThread, MapPostfetchThreadCreator, MapEvictThread<Value>, MapEvictThreadCreator<Value>> cache;

	public DistributedMapContainer(Factory factory)
	{
		this.cache = new DistributedMap.DistributedMapBuilder<Value, Factory, MapRelicateThread<Value>, MapRelicateThreadCreator<Value>, ContainerMapPostfetchNotification, MapPostfetchThread, MapPostfetchThreadCreator, MapEvictThread<Value>, MapEvictThreadCreator<Value>>()
				.factory(factory)
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.DISTRIBUTED_MAP_KEY)
				.cacheSize(TestCacheConfig.DISTRIBUTED_MAP_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.DISTRIBUTED_MAP_DISK_SIZE_IN_MB)
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.replicateCreator(new MapRelicateThreadCreator<Value>())
				.postfetchCreator(new MapPostfetchThreadCreator())
				.evictCreator(new MapEvictThreadCreator<Value>())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_MAP_POSTFETCH_TIMEOUT)
				.replicateTask(new MapReplicateTask<Value>())
				.postfetchTask(new MapPostfetchTask())
				.evictTask(new MapEvictedTask<Value>())
				.build();
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.cache.shutdown();
	}
}
