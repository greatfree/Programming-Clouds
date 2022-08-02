package org.greatfree.framework.cps.cache.coordinator;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.distributed.DistributedMap;
import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedMapFetchException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyDataThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyDataThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyDataForDMThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyDataForDMThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyDataThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyDataThreadCreator;
import org.greatfree.framework.cps.cache.data.MyData;
import org.greatfree.framework.cps.cache.data.MyDataFactory;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataByKeysResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataNotification;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataResponse;

// Created: 07/08/2018, Bing Li
public class MyDistributedMap
{
	private DistributedMap<MyData, MyDataFactory, ReplicateMyDataThread, ReplicateMyDataThreadCreator, PostfetchMyDataNotification, PostfetchMyDataForDMThread, PostfetchMyDataForDMThreadCreator, EvictMyDataThread, EvictMyDataThreadCreator> cache;

//	private CopyOnWriteArrayList<String> totalKeys;
//	private Map<String, String> totalKeys;

	private MyDistributedMap()
	{
	}
	
	private static MyDistributedMap instance = new MyDistributedMap();
	
	public static MyDistributedMap MIDDLE()
	{
		if (instance == null)
		{
			instance = new MyDistributedMap();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.cache.shutdown();
	}
	
	public void init()
	{
		this.cache = new DistributedMap.DistributedMapBuilder<MyData, MyDataFactory, ReplicateMyDataThread, ReplicateMyDataThreadCreator, PostfetchMyDataNotification, PostfetchMyDataForDMThread, PostfetchMyDataForDMThreadCreator, EvictMyDataThread, EvictMyDataThreadCreator>()
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
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.postfetchTimeout(TestCacheConfig.DISTRIBUTED_MAP_POSTFETCH_TIMEOUT)
				.build();
		
//		this.totalKeys = new CopyOnWriteArrayList<String>();
//		this.totalKeys = new ConcurrentHashMap<String, String>();
	}

	/*
	public void save(String key, String myDataKey, MyData data)
	{
		this.cache.put(myDataKey, data);
		this.cache.signal(key);
	}
	*/
	
	public void put(String myDataKey, MyData data)
	{
		this.cache.put(new MapReplicateNotification<MyData>(myDataKey, data));
	}
	
	public void putAll(Map<String, MyData> data)
	{
		this.cache.putAll(new MapReplicateNotification<MyData>(data));
//		this.totalKeys.addAll(data.keySet());
//		System.out.println("MyDistributedMap-putAll(): the total count: " + this.totalKeys.size());
	}

	/*
	public void addKeys(Set<String> keys)
	{
//		this.totalKeys.addAll(keys);
		for (String key : keys)
		{
			if (!this.totalKeys.containsKey(key))
			{
				this.totalKeys.put(key, Tools.generateUniqueKey());
			}
		}
		System.out.println("MyDistributedMap-addKeys(): the total count: " + this.totalKeys.size());
	}
	*/
	
	public MyData get(String myDataKey)
	{
		System.out.println("MyCache-get(): key = " + myDataKey);
		return this.cache.get(new PostfetchMyDataNotification(myDataKey));
	}
	
	public Map<String, MyData> get(Set<String> myDataKeys) throws DistributedMapFetchException
	{
		return this.cache.getValues(new PostfetchMyDataNotification(myDataKeys));
	}
	
	public void postfetch(PostfetchMyDataNotification notification) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (notification.getResourceKeys() != null)
		{
			System.out.println("MyDistributedMap-postfetch(): getResourceKeys size = " + notification.getResourceKeys().size());
			PostfetchMyDataByKeysResponse response = Coordinator.CPS().postfetchMyData(notification.getResourceKeys());
			/*
			if (response != null)
			{
				this.cache.savePosfetchedData(notification.getKey(), response.getData());
			}
			*/
			System.out.println("MyDistributedMap-postfetch(): data size = " + response.getData().size());
			this.cache.savePosfetchedData(notification.getKey(), response.getData(), notification.isBlocking());
		}
		else
		{
//			System.out.println("PostfetchMyDataThread: resourceKey = " + notification.getResourceKey());
			PostfetchMyDataResponse response = Coordinator.CPS().postfetchMyData(notification.getResourceKey());
			/*
			if (response != null)
			{
//				MyDistributedMap.MIDDLE().save(notification.getKey(), response.getMyData().getKey(), response.getMyData());
				this.cache.savePostfetchedData(notification.getKey(), response.getMyData().getKey(), response.getMyData());
			}
			*/
			this.cache.savePostfetchedData(notification.getKey(), response.getMyData().getKey(), response.getMyData(), notification.isBlocking());
		}
	}
}
