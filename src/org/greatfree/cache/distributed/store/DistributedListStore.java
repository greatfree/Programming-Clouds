package org.greatfree.cache.distributed.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.cache.distributed.ListFetchNotification;
import org.greatfree.cache.distributed.ListReplicateNotification;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.UniqueKey;

// Created: 02/21/2019, Bing Li
public class DistributedListStore<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, ReplicateThread extends NotificationObjectQueue<ListReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ListReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends CacheListStore<Value, Factory, CompoundKeyCreator>
{
	private NotificationObjectDispatcher<ListReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;

	private NotificationObjectDispatcher<FetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;

	private NotificationObjectDispatcher<FetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private Map<String, List<Value>> postfetchedData;
	private Map<String, Value> postfetchedSingleData;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;

	public DistributedListStore(DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator());

		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<ListReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PrefetchThread, PrefetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPrefetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PostfetchThread, PostfetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPostfetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.evictedDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<EvictedNotification<Value>, EvictThread, EvictThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getEvictThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();
		this.prefetchThresholdSize = builder.getPrefetchThresholdSize();

		this.postfetchedData = new ConcurrentHashMap<String, List<Value>>();
		this.postfetchedSingleData = new ConcurrentHashMap<String, Value>();

		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();
		this.syncs = new ConcurrentHashMap<String, Sync>();
	}

	public static class DistributedListStoreBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, ReplicateThread extends NotificationObjectQueue<ListReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ListReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedListStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String storeKey;
		private Factory factory;
		private String rootPath;
//		private int storeSize;
		private long storeSize;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private CompoundKeyCreator keyCreator;

		private int poolSize;
		private ReplicateThreadCreator replicateCreator;
		private PrefetchThreadCreator prefetchCreator;
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private int prefetchThresholdSize;
		private ThreadPool threadPool;
		private int prefetchCount;
		private long postfetchTimeout;

		public DistributedListStoreBuilder()
		{
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}
		
		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateThreadCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThreadCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchThreadCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> notificationQueueSize(int nqSize)
		{
			this.notificationQueueSize = nqSize;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public DistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		@Override
		public DistributedListStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new DistributedListStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
		}
		
		public String getStoreKey()
		{
			return this.storeKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public CompoundKeyCreator getKeyCreator()
		{
			return this.keyCreator;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public long getStoreSize()
		{
			return this.storeSize;
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
		
		public int getWaitRound()
		{
			return this.waitRound;
		}
		
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

		public long getPostfetchTimeout()
		{
			return this.postfetchTimeout;
		}

		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}

	}

	@Override
	public void shutdown() throws InterruptedException
	{
		super.closeAtBase();
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

	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}
	
	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePostfetchedData(String key, String listKey, List<Value> values, boolean isBlocking)
	{
		// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
		if (isBlocking)
		{
			if (values != null)
			{
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
			super.addAllAt2ndBase(listKey, values);
		}
	}
	
	public void savePostfetchedData(String key, String listKey, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			super.addAt2ndBase(listKey, v);
			if (isBlocking)
			{
				this.postfetchedSingleData.put(key, v);
			}
		}
		if (isBlocking)
		{
			this.signal(key);
		}
	}
	
	public int getCacheSize(String listKey)
	{
		return super.getSizeAt2ndBase(listKey);
	}

	public Value getValueByKey(FetchNotification notification)
	{
		Value rsc = super.getAt2ndBase(notification.getCacheKey(), notification.getResourceKey());
		if (rsc != null)
		{
			return rsc;
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
			rsc = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return rsc;
		}
		return rsc;
	}
	
	public boolean containsKey(FetchNotification notification)
	{
		boolean isExisted = super.containsKeyAt2ndBase(notification.getCacheKey(), notification.getResourceKey());
		if (isExisted)
		{
			return true;
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
			return super.containsKeyAt2ndBase(notification.getCacheKey(), notification.getResourceKey());
		}
		return isExisted;
	}
	
	public Value getMaxValue(FetchNotification notification)
	{
		Value rsc = super.getAt2ndBase(notification.getCacheKey(), 0);
		if (rsc != null)
		{
			return rsc;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		// The resource index should be equal to 0. 07/22/2018, Bing Li
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
			rsc = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return rsc;
		}
		return rsc;
	}
	
	public Value getValueByIndex(FetchNotification notification)
	{
		if (super.getLeftSizeAt2ndBase(notification.getCacheKey(), notification.getResourceIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value rsc = super.getAt2ndBase(notification.getCacheKey(), notification.getResourceIndex());
		if (rsc != null)
		{
			return rsc;
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
			rsc = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return rsc;
		}
		return rsc;
	}

	public boolean isListExisted(FetchNotification notification)
	{
		boolean isExisted = super.isListInStoreAt2ndBase(notification.getCacheKey());
		if (isExisted)
		{
			return true;
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
			return super.isListInStoreAt2ndBase(notification.getCacheKey());
		}
		return isExisted;
	}

	public boolean isListEmpty(FetchNotification notification)
	{
		boolean isEmpty = super.isEmpty2ndBase(notification.getCacheKey());
		if (!isEmpty)
		{
			return false;
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
//			this.syncs.remove(notification.getKey());
			return super.isEmpty2ndBase(notification.getCacheKey());
		}
		return isEmpty;
	}

	public List<Value> getTop(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAt2ndBase(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getEndIndex() < super.getCacheSizeAtBase())
		{
//			System.out.println("1) PointingDistributedCacheStore-getTop(): endIndex = " + notification.getEndIndex());
			List<Value> values = super.getTopAt2ndBase(notification.getCacheKey(), notification.getEndIndex());
			if (values != null)
			{
				// Since the top index is required to be less than the cache size, the later condition is not necessary. 08/04/2018, Bing Li
				// When data is not evicted, the condition, rscs.size() < this.getCacheSize(notification.getCacheKey()), is fine. But if data has ever been evicted, the condition is not reasonable since the amount of loaded data must be less than the cache size and some required data is located at the terminal caches. 08/04/2018, Bing Li
	//				if (rscs.size() >= notification.getEndIndex() || rscs.size() < this.getCacheSize(notification.getCacheKey()))
				if (values.size() > notification.getEndIndex())
				{
					return values;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), values);
					}
				}
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
				values = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				return values;
			}
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getEndIndex());
		}
	}

	public List<Value> getRange(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAt2ndBase(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getEndIndex() - notification.getStartIndex() < super.getCacheSizeAtBase())
		{
			List<Value> values = super.getRangeAt2ndBase(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
			if (values != null)
			{
				// Since the top index is required to be less than the cache size, the later condition is not necessary. 08/04/2018, Bing Li
				// When data is not evicted, the condition, rscs.size() < this.getCacheSize(notification.getCacheKey()), is fine. But if data has ever been evicted, the condition is not reasonable since the amount of loaded data must be less than the cache size and some required data is located at the terminal caches. 08/04/2018, Bing Li
//				if (rscs.size() >= notification.getEndIndex() - notification.getStartIndex() || rscs.size() < this.getCacheSize(notification.getCacheKey()))
				if (values.size() > notification.getEndIndex() - notification.getStartIndex())
				{
					return values;
				}
				else
				{
					if (notification.isBlocking())
					{
						/*
						 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
						 */
						this.postfetchedData.put(notification.getKey(), values);
					}
				}
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
				values = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				return values;
			}
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getEndIndex() - notification.getStartIndex() + 1);
		}
	}
	
	public void put(ListReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.addAt2ndBase(notification.getCacheKey(), notification.getValue());
	}

	public void putAll(ListReplicateNotification<Value> notification)
	{
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
		super.addAllAt2ndBase(notification.getCacheKey(), notification.getValues());
	}
	
	public void putAllLocally(String mapKey, List<Value> pointings)
	{
		super.addAllAt2ndBase(mapKey, pointings);
	}
	
	public void putLocally(String mapKey, Value value)
	{
		super.addAt2ndBase(mapKey, value);
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		super.removeAtBase(k);
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
