package org.greatfree.cache.distributed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.util.Builder;
import org.greatfree.util.Pointing;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 07/04/2018, Bing Li
public class SortedDistributedList<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends SortedList<Value, Factory, DescendantComp>
{
	private NotificationObjectDispatcher<PointingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;

	private NotificationObjectDispatcher<FetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;
	
	private NotificationObjectDispatcher<FetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
//	private final int postfetchCount;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;
	
	// When postfetched data is large, some of them might be evicted. So it is necessary to put them into the map temporarily. 07/18/2018, Bing Li
	private Map<String, List<Value>> postfetchedData;
	private Map<String, Value> postfetchedSingleData;

	private ThreadPool threadPool;
	
	private Map<String, Sync> syncs;

	public SortedDistributedList(SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), builder.getComparator(), builder.getSortSize());
		
		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PointingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PrefetchThread, PrefetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPrefetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPostfetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.evictedDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<EvictedNotification<Value>, EvictThread, EvictThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getEvictThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.prefetchThresholdSize = builder.getPrefetchThresholdSize();
		
		this.postfetchedData = new ConcurrentHashMap<String, List<Value>>();
		this.postfetchedSingleData = new ConcurrentHashMap<String, Value>();

		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
//		this.postfetchCount = builder.getPostfetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();
		
		this.syncs = new ConcurrentHashMap<String, Sync>();
	}
	
	public static class SortedDistributedListBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<SortedDistributedList<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String cacheKey;
		private Factory factory;
		private String rootPath;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private DescendantComp comp;
		private int sortSize;

		private int poolSize;
		private ReplicateThreadCreator replicateCreator;
		private PrefetchThreadCreator prefetchCreator;
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
//		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private int prefetchThresholdSize;
		private ThreadPool threadPool;
		private int prefetchCount;
//		private int postfetchCount;
		private long postfetchTimeout;

		public SortedDistributedListBuilder()
		{
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> notificationQueueSize(int nqSize)
		{
			this.notificationQueueSize = nqSize;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}
		
		/*
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}
		
		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		/*
		public PointingDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCount(int postfetchCount)
		{
			this.postfetchCount = postfetchCount;
			return this;
		}
		*/

		public SortedDistributedListBuilder<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}
		
		@Override
		public SortedDistributedList<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new SortedDistributedList<Value, Factory, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
		}
		
		public String getCacheKey()
		{
			return this.cacheKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public DescendantComp getComparator()
		{
			return this.comp;
		}
		
		public int getSortSize()
		{
			return this.sortSize;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public long getCacheSize()
		{
			return this.cacheSize;
		}
		
		public int getOffheapSizeInMB()
		{
			return this.offheapSizeInMB;
		}
		
		public int getDiskSizeInMB()
		{
			return this.diskSizeInMB;
		}
		
		public int getPoolSize()
		{
			return this.poolSize;
		}
		
		public ReplicateThreadCreator getReplicateThreadCreator()
		{
			return this.replicateCreator;
		}
		
		public PrefetchThreadCreator getPrefetchThreadCreator()
		{
			return this.prefetchCreator;
		}
	
		public PostfetchThreadCreator getPostfetchThreadCreator()
		{
			return this.postfetchCreator;
		}
		
		public EvictThreadCreator getEvictThreadCreator()
		{
			return this.evictCreator;
		}

		public int getNotificationQueueSize()
		{
			return this.notificationQueueSize;
		}
		
		public long getDispatcherWaitTime()
		{
			return this.dispatcherWaitTime;
		}

		/*
		public int getWaitRound()
		{
			return this.waitRound;
		}
		*/
		
		public long getIdleCheckDelay()
		{
			return this.idleCheckDelay;
		}
		
		public long getIdleCheckPeriod()
		{
			return this.idleCheckPeriod;
		}
		
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
		
		public int getPrefetchThresholdSize()
		{
			return this.prefetchThresholdSize;
		}

		public int getPrefetchCount()
		{
			return this.prefetchCount;
		}

		/*
		public int getPostfetchCount()
		{
			return this.postfetchCount;
		}
		*/

		public long getPostfetchTimeout()
		{
			return this.postfetchTimeout;
		}

		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}
	}
	
	public void shutdown() throws InterruptedException
	{
		super.shutdown();
		this.postfetchedData.clear();
		this.postfetchedData = null;
		this.postfetchedSingleData.clear();
		this.postfetchedSingleData = null;
		this.replicateDispatcher.dispose();
		this.prefetchDispatcher.dispose();
		this.postfetchDispatcher.dispose();
		this.evictedDispatcher.dispose();
		for (Sync entry : this.syncs.values())
		{
			entry.signal();
		}
		this.syncs.clear();
		this.syncs = null;
	}
	
	public int getSize()
	{
		return super.getSizeAt2ndBase();
	}
	
	public String getCacheKey()
	{
		return super.getCacheKeyAtBase();
	}
	
	public long getMemCacheSize()
	{
		return super.getMemCacheSizeAtBase();
	}
	
	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePostfetchedData(String key, List<Value> values, boolean isBlocking)
	{
		if (isBlocking)
		{
			if (values != null)
			{
				// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
				if (this.postfetchedData.containsKey(key))
				{
					this.postfetchedData.remove(key);
				}
				this.postfetchedData.put(key, new ArrayList<Value>(values));
			}
			this.signal(key);
		}
		if (values != null)
		{
			super.addAllAt2ndBase(values);
		}
	}
	
	public void savePostfetchedData(String key, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			super.addAt2ndBase(v);
			if (isBlocking)
			{
				this.postfetchedSingleData.put(key, v);
			}
		}
		if (isBlocking)
		{
			this.signal(key);
		}
		/*
		if (v != null)
		{
			this.add(v);
		}
		*/
	}

	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}

	/*
	public int getPostfetchCount()
	{
		return this.postfetchCount;
	}
	*/
	
	public Value get(FetchNotification notification)
	{
		if (super.getLeftSizeAtBase(notification.getResourceIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
//		System.out.println("PointingDistributedList-get(): resourceIndex = " + notification.getResourceIndex());
//		Date startTime = Calendar.getInstance().getTime();
		Value v = super.getAt2ndBase(notification.getResourceIndex());
//		System.out.println("PointingDistributedList-get(): leftSize = " + super.getLeftSize(notification.getResourceIndex()));
//		Date endTime = Calendar.getInstance().getTime();
		if (v != null)
		{
//			System.out.println("PointingDistributedList-get(): v = " + v.getPoints() + " took " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms for prefetching ...");
			return v;
		}
		else
		{
//			System.out.println("PointingDistributedList-get(): v = NULL such that postfetching is REQUIRED...");
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
			
			/*
			 * It needs to make the below update. The postfetched data might not be sorted at the index because evicting results in some keys are removed. 07/29/2018, Bing Li 
			 */
//			return super.get(notification.getResourceIndex());
			v = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return v;
		}
		return v;
	}
	
	/*
	 * I think the exception is required. It is not reasonable to load huge data from the cache. So the range should be located within a certain range. 08/02/2018, Bing Li
	 * It is not necessary to throw the exception. 08/02/2018, Bing Li
	 */
//	public List<Value> getTop(PrePostfetchNotification notification)
	public List<Value> getTop(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAtBase(notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
//		System.out.println("0) PointingDistributedList-getTop(): endIndex = " + notification.getEndIndex());
		if (notification.getEndIndex() < super.getCacheSizeAtBase())
		{
			List<Value> rscs = super.getTopAt2ndBase(notification.getEndIndex());
//			System.out.println("0.1) PointingDistributedList-getTop(): left size = " + super.getLeftSize(notification.getEndIndex()));
//			System.out.println("0.2) PointingDistributedList-getTop(): threshold size = " + this.prefetchThresholdSize);
			if (rscs != null)
			{
//				System.out.println("1) PointingDistributedList-getTop(): rscs size = " + rscs.size());
				// Since the top index is required to be less than the cache size, the later condition is not necessary. 08/04/2018, Bing Li
				// When data is not evicted, the condition, rscs.size() < super.getCacheSize(), is fine. But if data has ever been evicted, the condition is not reasonable since the amount of loaded data must be less than the cache size and some required data is located at the terminal caches. 08/04/2018, Bing Li
//				if (rscs.size() >= notification.getEndIndex() || rscs.size() <= super.getCacheSize())
				if (rscs.size() > notification.getEndIndex())
				{
//					System.out.println("2) PointingDistributedList-getTop(): rscs size = " + rscs.size());
					return rscs;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), rscs);
					}
				}
			}
//				notification.setStartIndex(rscs.size() - 1);
//				notification.setStartIndex(rscs.size());
			/*
			if (this.getPostfetchCount() < notification.getEndIndex() - rscs.size())
			{
				notification.setPostfetchCount(notification.getEndIndex() - rscs.size() -1);
			}
			*/
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//					return super.getTop(notification.getEndIndex());
				List<Value> values = this.postfetchedData.get(notification.getKey());
				/*
					if (values != null)
					{
						// The below line does not work. Data at the client side is not sorted. But it shows roughly correct. It is really weird. 07/19/2018, Bing Li
						// It is weird data is not sorted after getting it out from the map. Fortunately, the order is not destroyed a lot. Most data is sorted. So the operation should not cost a lot. 07/19/2018, Bing Li
//						Collections.sort(values, super.getComp());
						System.out.println("3) PointingDistributedList-getTop(): values size = " + values.size());
//						rscs.addAll(values);
//						System.out.println("4) PointingDistributedList-getTop(): rscs size = " + rscs.size());
						this.postfetchedData.remove(notification.getKey());
					}
					*/
				this.postfetchedData.remove(notification.getKey());
				return values;
			}
			return rscs;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getEndIndex());
		}
	}

	/*
	 * I think the exception is required. It is not reasonable to load huge data from the cache. So the range should be located within a certain range. 08/02/2018, Bing Li
	 * It is not necessary to throw the exception. 08/02/2018, Bing Li
	 */
//	public List<Value> getRange(PrePostfetchNotification notification)
	public List<Value> getRange(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAtBase(notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getEndIndex() - notification.getStartIndex() < super.getCacheSizeAtBase())
		{
			List<Value> rscs = super.getAt2ndBase(notification.getStartIndex(), notification.getEndIndex());
			if (rscs != null)
			{
				// Since the top index is required to be less than the cache size, the later condition is not necessary. 08/04/2018, Bing Li
				// When data is not evicted, the condition, rscs.size() < super.getCacheSize(), is fine. But if data has ever been evicted, the condition is not reasonable since the amount of loaded data must be less than the cache size and some required data is located at the terminal caches. 08/04/2018, Bing Li
//				if (rscs.size() >= notification.getEndIndex() - notification.getStartIndex() || rscs.size() < super.getCacheSize())
				if (rscs.size() > notification.getEndIndex() - notification.getStartIndex())
				{
					return rscs;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), rscs);
					}
				}
			}
//				notification.setStartIndex(notification.getStartIndex() + rscs.size() - 1);
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//					return super.get(notification.getStartIndex(), notification.getEndIndex());
				List<Value> values = this.postfetchedData.get(notification.getKey());
				/*
				if (values != null)
				{
					this.postfetchedData.remove(notification.getKey());
				}
				*/
				this.postfetchedData.remove(notification.getKey());
				return values;
			}
			return rscs;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getEndIndex() - notification.getStartIndex() + 1);
		}
	}

	/*
	public void savePostfetchedData(String key, List<Value> values)
	{
		this.postfetchedData.put(key, values);
	}
	*/

	public void add(PointingReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.addAt2ndBase(notification.getValue());
	}
	
	public void addAll(PointingReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.addAllAt2ndBase(notification.getValues());
	}
	
	public void addAllLocally(List<Value> values)
	{
		super.addAllAt2ndBase(values);
	}

	@Override
	public void evict(String k, Value v)
	{
		/*
		 * According to the logs of 07/28/2018, the below comment is abandoned. 07/29/2018, Bing Li
		 */
		/*
		 * Since postfetching is implemented, it is not necessary to remove the key? I need to ensure that. 07/28/2018, Bing Li
		 */
		
		/*
		 * To keep the data sorted in the cache, only the minimum data can be evicted. 07/28/2018, Bing Li
		 */
//		int lastIndex = super.getLastIndex();
//		Value minData = super.get(lastIndex);
//		super.remove(minData.getKey());
//		Value minData = super.getMin();
//		super.remove(minData.getKey());
//		super.add(v);
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(minData.getKey(), minData);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		super.removeKeyAtBase(k);
	}

	@Override
	public void forward(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expire(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}
}
