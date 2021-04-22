package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.cache.distributed.store.DistributedMapStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedMapFetchException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyDistributedStoreDataThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyDistributedStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyDistributedStoreDataThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyDistributedStoreDatarThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyDistributedStoreDataThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyDistributedStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.data.MyStoreDataFactory;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMuchMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataResponse;
import org.greatfree.util.UtilConfig;

// Created: 08/24/2018, Bing Li
public class MyDistributedMapStore
{
	private DistributedMapStore<MyStoreData, MyStoreDataFactory, StoreKeyCreator, ReplicateMyDistributedStoreDataThread, ReplicateMyDistributedStoreDataThreadCreator, PostfetchMyStoreDataNotification, PostfetchMyDistributedStoreDataThread, PostfetchMyDistributedStoreDatarThreadCreator, EvictMyDistributedStoreDataThread, EvictMyDistributedStoreDataThreadCreator> store;

	private MyDistributedMapStore()
	{
	}
	
	private static MyDistributedMapStore instance = new MyDistributedMapStore();
	
	public static MyDistributedMapStore MIDDLE()
	{
		if (instance == null)
		{
			instance = new MyDistributedMapStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.store.shutdown();
	}
	
	public void init()
	{
		this.store = new DistributedMapStore.DistributedMapStoreBuilder<MyStoreData, MyStoreDataFactory, StoreKeyCreator, ReplicateMyDistributedStoreDataThread, ReplicateMyDistributedStoreDataThreadCreator, PostfetchMyStoreDataNotification, PostfetchMyDistributedStoreDataThread, PostfetchMyDistributedStoreDatarThreadCreator, EvictMyDistributedStoreDataThread, EvictMyDistributedStoreDataThreadCreator>()
				.factory(new MyStoreDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeKey(TestCacheConfig.DISTRIBUTED_MAP_STORE_KEY)
				.totalStoreSize(TestCacheConfig.DISTRIBUTED_MAP_STORE_SIZE)
				.cacheSize(TestCacheConfig.DISTRIBUTED_MAP_STORE_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.DISTRIBUTED_MAP_STORE_OFF_HEAP_SIZE_IN_MB)
				.keyCreator(new StoreKeyCreator())
				.diskSizeInMB(TestCacheConfig.DISTRIBUTED_MAP_STORE_DISK_SIZE_IN_MB)
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.replicateCreator(new ReplicateMyDistributedStoreDataThreadCreator())
				.postfetchCreator(new PostfetchMyDistributedStoreDatarThreadCreator())
				.evictCreator(new EvictMyDistributedStoreDataThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_MAP_STORE_POSTFETCH_TIMEOUT)
				.build();
	}
	
	public void put(String mapKey, MyStoreData data)
	{
		this.store.put(new MapReplicateNotification<MyStoreData>(mapKey, data));
	}
	
	public void putAll(String mapKey, Map<String, MyStoreData> data)
	{
		this.store.putAll(new MapReplicateNotification<MyStoreData>(mapKey, data));
	}
	
	public MyStoreData getData(String mapKey, String dataKey)
	{
		return this.store.get(new PostfetchMyStoreDataNotification(mapKey, dataKey));
	}
	
	public Map<String, MyStoreData> getData(String mapKey, Set<String> dataKeys) throws DistributedMapFetchException
	{
		return this.store.getValues(new PostfetchMyStoreDataNotification(mapKey, dataKeys));
	}
	
	public Set<String> getDataKeys(String mapKey)
	{
		return this.store.getKeys(new PostfetchMyStoreDataNotification(mapKey));
	}

	/*
	 * It is not reasonable to get all of the value keys of the distributed cache. So only the frequently-used ones are returned. 08/25/2018, Bing Li
	 */
	public Set<String> getValueKeys(String mapKey)
	{
		return this.store.getKeys(new PostfetchMyStoreDataNotification(mapKey, UtilConfig.NO_KEY));
	}
	
	public void postfetch(PostfetchMyStoreDataNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (notification.getResourceKeys() != null)
		{
			PostfetchMuchMyStoreDataResponse response = Coordinator.CPS().postfetchMyStoreData(notification.getMapKey(), notification.getResourceKeys());
			System.out.println("MyDistributedMapStore-postfetch(): responded data size = " + response.getData().size());
			this.store.savePosfetchedData(notification.getKey(), notification.getMapKey(), response.getData(), notification.isBlocking());
		}
		else if (!notification.getResourceKey().equals(UtilConfig.NO_KEY))
		{
			PostfetchMyStoreDataResponse response = Coordinator.CPS().postfetchMyStoreData(notification.getMapKey(), notification.getResourceKey());
			this.store.savePostfetchedData(notification.getKey(), notification.getMapKey(), response.getData(), notification.isBlocking());
		}
		else
		{
			PostfetchMyStoreDataKeysResponse response = Coordinator.CPS().postfetchMyStoreDataKeys(notification.getMapKey(), this.store.getCacheSize());
			this.store.savePosfetchedData(notification.getKey(), notification.getMapKey(), response.getData(), notification.isBlocking());
		}
	}
	
}
