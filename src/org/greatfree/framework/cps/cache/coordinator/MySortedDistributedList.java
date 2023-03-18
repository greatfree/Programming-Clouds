package org.greatfree.framework.cps.cache.coordinator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.distributed.FetchConfig;
import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.cache.distributed.SortedDistributedList;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyPointingThread;
import org.greatfree.framework.cps.cache.coordinator.evicting.EvictMyPointingThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyPointingListThread;
import org.greatfree.framework.cps.cache.coordinator.postfetching.PostfetchMyPointingListThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyPointingListThread;
import org.greatfree.framework.cps.cache.coordinator.prefetching.PrefetchMyPointingListThreadCreator;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyPointingThread;
import org.greatfree.framework.cps.cache.coordinator.replicating.ReplicateMyPointingThreadCreator;
import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.data.MyPointingFactory;
import org.greatfree.framework.cps.cache.message.FetchMyPointingListNotification;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyPointingsResponse;
import org.greatfree.util.Time;

// Created: 07/11/2018, Bing Li
public class MySortedDistributedList
{
	private SortedDistributedList<MyPointing, MyPointingFactory, DescendantListPointingComparator<MyPointing>, ReplicateMyPointingThread, ReplicateMyPointingThreadCreator, FetchMyPointingListNotification, PrefetchMyPointingListThread, PrefetchMyPointingListThreadCreator, PostfetchMyPointingListThread, PostfetchMyPointingListThreadCreator, EvictMyPointingThread, EvictMyPointingThreadCreator> cache;
	
	private AtomicInteger totalCount;
	
	private MySortedDistributedList()
	{
	}
	
	private static MySortedDistributedList instance = new MySortedDistributedList();
	
	public static MySortedDistributedList MIDDLE()
	{
		if (instance == null)
		{
			instance = new MySortedDistributedList();
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
		this.cache = new SortedDistributedList.SortedDistributedListBuilder<MyPointing, MyPointingFactory, DescendantListPointingComparator<MyPointing>, ReplicateMyPointingThread, ReplicateMyPointingThreadCreator, FetchMyPointingListNotification, PrefetchMyPointingListThread, PrefetchMyPointingListThreadCreator, PostfetchMyPointingListThread, PostfetchMyPointingListThreadCreator, EvictMyPointingThread, EvictMyPointingThreadCreator>()
				.factory(new MyPointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.SORTED_DISTRIBUTED_LIST_KEY)
				.cacheSize(TestCacheConfig.SORTED_DISTRIBUTED_LIST_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_LIST_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.SORTED_DISTRIBUTED_LIST_DISK_SIZE_IN_MB)
				.comp(new DescendantListPointingComparator<MyPointing>())
				.sortSize(TestCacheConfig.SORTED_DISTRIBUTED_LIST_SORT_SIZE)
				.poolSize(TestCacheConfig.CACHE_THREAD_POOL_SIZE)
				.replicateCreator(new ReplicateMyPointingThreadCreator())
				.prefetchCreator(new PrefetchMyPointingListThreadCreator())
				.postfetchCreator(new PostfetchMyPointingListThreadCreator())
				.evictCreator(new EvictMyPointingThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.PERIOD().getScheduler())
				.prefetchThresholdSize(TestCacheConfig.SORTED_DISTRIBUTED_LIST_PREFETCH_THRESHOLD_SIZE)
				.threadPool(SharedThreadPool.SHARED().getPool())
				.prefetchCount(TestCacheConfig.SORTED_DISTRIBUTED_LIST_PREFETCHING_SIZE)
//				.postfetchCount(TestCacheConfig.POINTING_DISTRIBUTED_LIST_POSTFETCHING_SIZE)
				.postfetchTimeout(TestCacheConfig.SORTED_DISTRIBUTED_LIST_POSTFETCHING_TIMEOUT)
				.build();
		
		this.totalCount = new AtomicInteger(0);
	}
	
	public void add(MyPointing pointing)
	{
		System.out.println(this.totalCount.getAndIncrement() + ") MyPointingDistributedList-add(): key = " + pointing.getKey() + ", points = " + pointing.getPoints() + ", description = " + pointing.getDescription());
		this.cache.add(new PointingReplicateNotification<MyPointing>(pointing));
	}
	
	public void addAll(List<MyPointing> pointings)
	{
		/*
		for (MyPointing entry : pointings)
		{
			System.out.println("MyPointingDistributedList-addAll(): key = " + entry.getKey() + ", points = " + entry.getPoints() + ", description = " + entry.getDescription());
		}
		System.out.println("MyPointingDistributedList-addAll(): pointings size = " + pointings.size());
		*/
		this.cache.addAll(new PointingReplicateNotification<MyPointing>(this.cache.getCacheKey(), pointings));
	}
	
	/*
	public void prefetch(List<MyPointing> pointings)
	{
		this.cache.addAll(pointings);
	}

	public void save(String key, MyPointing pointing)
	{
		this.cache.add(pointing);
		this.cache.signal(key);
	}
	
	public void save(String key, List<MyPointing> pointings)
	{
		this.cache.savePostfetchedData(key, pointings);
		this.cache.addAll(pointings);
		this.cache.signal(key);
	}
	*/
	
	public MyPointing get(int index)
	{
		System.out.println("MyPointingDistributedList-get(): index = " + index);
//		return this.cache.get(new FetchMyPointingNotification(this.cache.getCacheKey(), index, this.cache.getSize(), this.cache.getPrefetchCount(), this.cache.getPostfetchCount()));
		Date startTime = Calendar.getInstance().getTime();
//		return this.cache.get(new FetchMyPointingListNotification(this.cache.getCacheKey(), index, this.cache.getMemCacheSize(), this.cache.getPrefetchCount()));
		MyPointing v = this.cache.get(new FetchMyPointingListNotification(this.cache.getCacheKey(), index, this.cache.getMemCacheSize(), this.cache.getPrefetchCount()));
		Date endTime = Calendar.getInstance().getTime();
		if (v != null)
		{
			System.out.println("MyPointingDistributedList-get(): v = " + v.getPoints() + " took " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms ...");
		}
		else
		{
			System.out.println("MyPointingDistributedList-get(): v is NULL took " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms ...");
		}
		return v;
	}
	
//	public List<MyPointing> getTop(int endIndex)
	public List<MyPointing> getTop(int endIndex) throws DistributedListFetchException
	{
		System.out.println("MyPointingDistributedList-getTop(): endIndex = " + endIndex);
//		return this.cache.getTop(new FetchMyPointingNotification(this.cache.getCacheKey(), 0, endIndex, this.cache.getSize(), this.cache.getPrefetchCount(), this.cache.getPostfetchCount()));
		return this.cache.getTop(new FetchMyPointingListNotification(this.cache.getCacheKey(), 0, endIndex, this.cache.getMemCacheSize(), this.cache.getPrefetchCount()));
//		return this.cache.getTop(new FetchMyPointingListNotification(this.cache.getCacheKey(), 0, endIndex, this.cache.getPrefetchCount()));
	}
	
//	public List<MyPointing> get(int startIndex, int endIndex)
	public List<MyPointing> get(int startIndex, int endIndex) throws DistributedListFetchException
	{
//		return this.cache.getRange(new FetchMyPointingNotification(this.cache.getCacheKey(), startIndex, endIndex, this.cache.getSize(), this.cache.getPrefetchCount(), this.cache.getPostfetchCount()));
		return this.cache.getRange(new FetchMyPointingListNotification(this.cache.getCacheKey(), startIndex, endIndex, this.cache.getMemCacheSize(), this.cache.getPrefetchCount()));
//			return this.cache.getRange(new FetchMyPointingListNotification(this.cache.getCacheKey(), startIndex, endIndex, this.cache.getPrefetchCount()));
	}
	
	public void prefetch(FetchMyPointingListNotification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
//		response = Coordinator.CPS().prefetch(notification.getCurrentCacheSize() - 1, notification.getPrefetchCount());
		PrefetchMyPointingsResponse response = Coordinator.CPS().prefetch(notification.getPrefetchStartIndex(), notification.getPrefetchEndIndex());
		System.out.println("PrefetchMyPointingThread: data size = " + response.getPointings().size());
		this.cache.addAllLocally(response.getPointings());
	}
	
	public void postfetch(FetchMyPointingListNotification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		if (notification.getType() == FetchConfig.POSTFETCH_RESOURCE_BY_KEY)
		{
			System.out.println("PostfetchMyPointingThread: I am Postfetching the data with the key, " + notification.getResourceKey());
			PostfetchMyPointingByKeyResponse pResponse = Coordinator.CPS().postfetchMyPointing(notification.getResourceKey());
			/*
			if (pResponse.getPointing() != null)
			{
//				this.save(notification.getKey(), pResponse.getPointing());
				this.cache.savePostfetchedData(notification.getKey(), pResponse.getPointing());
			}
			*/
			this.cache.savePostfetchedData(notification.getKey(), pResponse.getPointing(), notification.isBlocking());
		}
		else if (notification.getType() == FetchConfig.FETCH_RESOURCE_BY_INDEX)
		{
			System.out.println("MyPointingDistributedList-postfetch(): I am Postfetching the data with the index, " + notification.getResourceIndex());
			PostfetchMyPointingByIndexResponse piResponse = Coordinator.CPS().postfetchMyPointing(notification.getResourceIndex());
			if (piResponse.getPointing() != null)
			{
//				this.save(notification.getKey(), piResponse.getPointing());
//				this.cache.savePostfetchedData(notification.getKey(), piResponse.getPointing());
				System.out.println("MyPointingDistributedList-postfetch(): Done: ith the key, " + notification.getResourceIndex() + ", " + piResponse.getPointing().getKey() + ", " + piResponse.getPointing().getPoints() + ", " + piResponse.getPointing().getDescription());
			}
			this.cache.savePostfetchedData(notification.getKey(), piResponse.getPointing(), notification.isBlocking());
		}
		/*
		else if (notification.getType() == FetchConfig.POSTFETCH_ALL_RESOURCES_BY_CACHE_KEY || notification.getType() == FetchConfig.FETCH_RESOURCE_BY_INDEX)
		{
			System.out.println("PostfetchMyPointingThread: I am Postfetching the data with the key, " + notification.getResourceIndex() + ", for the count, " + notification.getPostfetchCount());
			psResponse = Coordinator.CPS().postfetchMyPointing(notification.getResourceIndex(), notification.getPostfetchCount());
			if (psResponse.getPointings() != null)
			{
				MyPointingDistributedList.MIDDLE().save(notification.getKey(), psResponse.getPointings());
			}
		}
		*/
		else if (notification.getType() == FetchConfig.FETCH_RESOURCES_BY_RANGE)
		{
			System.out.println("PostfetchMyPointingThread: I am Postfetching the TOP data to, " + notification.getEndIndex());
			PostfetchMyPointingsResponse psResponse = Coordinator.CPS().postfetchTopMyPointing(notification.getEndIndex());
//			psResponse = Coordinator.CPS().postfetchTopMyPointing(notification.getStartIndex(), notification.getPostfetchCount());
			/*
			if (psResponse.getPointings() != null)
			{
//				this.save(notification.getKey(), psResponse.getPointings());
				this.cache.savePostfetchedData(notification.getKey(), psResponse.getPointings());
			}
			*/
			System.out.println("PostfetchMyPointingThread: I got postfetch data size: " + psResponse.getPointings().size());
			this.cache.savePostfetchedData(notification.getKey(), psResponse.getPointings(), notification.isBlocking());
		}
	}
}
