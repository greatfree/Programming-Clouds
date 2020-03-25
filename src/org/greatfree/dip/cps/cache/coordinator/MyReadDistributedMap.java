package org.greatfree.dip.cps.cache.coordinator;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.distributed.ReadDistributedMap;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.coordinator.evicting.EvictMyDataThread;
import org.greatfree.dip.cps.cache.coordinator.evicting.EvictMyDataThreadCreator;
import org.greatfree.dip.cps.cache.coordinator.postfetching.PostfetchMyDataForPDMThread;
import org.greatfree.dip.cps.cache.coordinator.postfetching.PostfetchMyDataForPDMThreadCreator;
import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.data.MyDataFactory;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyDataByKeysResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyDataNotification;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyDataResponse;
import org.greatfree.exceptions.DistributedMapFetchException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 07/21/2018, Bing Li
public class MyReadDistributedMap
{
	private ReadDistributedMap<MyData, MyDataFactory, PostfetchMyDataNotification, PostfetchMyDataForPDMThread, PostfetchMyDataForPDMThreadCreator, EvictMyDataThread, EvictMyDataThreadCreator> cache;

	private MyReadDistributedMap()
	{
	}
	
	private static MyReadDistributedMap instance = new MyReadDistributedMap();
	
	public static MyReadDistributedMap MIDDLE()
	{
		if (instance == null)
		{
			instance = new MyReadDistributedMap();
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
		this.cache = new ReadDistributedMap.ReadDistributedMapBuilder<MyData, MyDataFactory, PostfetchMyDataNotification, PostfetchMyDataForPDMThread, PostfetchMyDataForPDMThreadCreator, EvictMyDataThread, EvictMyDataThreadCreator>()
				.factory(new MyDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.POST_DISTRIBUTED_MAP_KEY)
				.cacheSize(TestCacheConfig.POST_DISTRIBUTED_MAP_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.POST_DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.POST_DISTRIBUTED_MAP_DISK_SIZE_IN_MB)
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.postfetchCreator(new PostfetchMyDataForPDMThreadCreator())
				.evictCreator(new EvictMyDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.postfetchTimeout(TestCacheConfig.POST_DISTRIBUTED_MAP_POSTFETCH_TIMEOUT)
				.build();
	}
	
	public void put(String myDataKey, MyData data)
	{
		this.cache.putLocally(myDataKey, data);
	}
	
	public void putAll(Map<String, MyData> data)
	{
		this.cache.putAllLocally(data);
	}
	
	public MyData get(String myDataKey)
	{
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
			PostfetchMyDataByKeysResponse response = Coordinator.CPS().postfetchMyData(notification.getResourceKeys());
			/*
			if (response != null)
			{
				this.cache.savePosfetchedData(notification.getKey(), response.getData());
			}
			*/
//			System.out.println("PostfetchMyDataThread: resourceKey = " + notification.getResourceKey());
			this.cache.savePosfetchedData(notification.getKey(), response.getData(), notification.isBlocking());
		}
		else
		{
			PostfetchMyDataResponse response = Coordinator.CPS().postfetchMyData(notification.getResourceKey());
			/*
			if (response != null)
			{
				this.cache.savePostfetchedData(notification.getKey(), response.getMyData().getKey(), response.getMyData());
			}
			*/
			/*
			if (response != null)
			{
				System.out.println("MyPostDistributedMap-postfetch(): PostfetchMyDataResponse is NOT null");
				if (response.getMyData() != null)
				{
					System.out.println("MyPostDistributedMap-postfetch(): PostfetchMyDataResponse is NOT null; data is NOT null");
				}
				else
				{
					System.out.println("MyPostDistributedMap-postfetch(): PostfetchMyDataResponse is NOT null; data is null");
				}
			}
			else
			{
				System.out.println("MyPostDistributedMap-postfetch(): PostfetchMyDataResponse is null");
			}
			*/
			this.cache.savePostfetchedData(notification.getKey(), notification.getResourceKey(), response.getMyData(), notification.isBlocking());
		}
	}
}
