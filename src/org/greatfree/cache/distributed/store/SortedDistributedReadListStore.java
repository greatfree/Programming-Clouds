package org.greatfree.cache.distributed.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.cache.distributed.ListFetchNotification;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.Pointing;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * Compared with PointingDistributedCacheStore, the most important feature for the store is that it does not replicate data to the backend. 07/04/2018, Bing Li
 */

/*
 * The store is only used by an abandoned class, com.greatfree.iserver.caching.abandoned.AuthorityCache. Thus, the store is actually not used. When using the store, it assumes that both the local store and the backend one have the exactly same caches such that prefetching can be performed. 06/10/2018, Bing Li
 */

// Created: 01/30/2018, Bing Li
// public class PrefetchablePointingMapCacheStore<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PrefetchablePointingMapCacheStore<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PointingPrefetchMapStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PointingDistributedReadCacheStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends PointingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>
public class SortedDistributedReadListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>
{
//	private PointingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp> store;
	private NotificationObjectDispatcher<FetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;

	private NotificationObjectDispatcher<FetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
//	private final int postfetchCount;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private Map<String, List<Value>> postfetchedData;
	private Map<String, Value> postfetchedSingleData;

	private ThreadPool threadPool;
	
	private Map<String, Sync> syncs;

//	public PrefetchablePointingMapCacheStore(PointingMapCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> builder)
	public SortedDistributedReadListStore(SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
//		this.store = new PointingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp>(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator(), builder.getSortSize());
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PrefetchThread, PrefetchThreadCreator>()
//				.threadPool(builder.getThreadPool())
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPrefetchThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();
		
		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<FetchNotification, PostfetchThread, PostfetchThreadCreator>()
//				.threadPool(builder.getThreadPool())
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPostfetchThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.evictedDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<EvictedNotification<Value>, EvictThread, EvictThreadCreator>()
//				.threadPool(builder.getThreadPool())
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getEvictThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.prefetchThresholdSize = builder.getPrefetchThresholdSize();
		
		this.postfetchedData = new ConcurrentHashMap<String, List<Value>>();
		this.postfetchedSingleData = new ConcurrentHashMap<String, Value>();

//		this.notificationCreator = builder.getNotificationCreator();
		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
//		this.postfetchCount = builder.getPostfetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();
		this.syncs = new ConcurrentHashMap<String, Sync>();
	}

//	public static class PointingMapCacheBuilder<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchablePointingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
//	public static class PointingMapCacheBuilder<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchablePointingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
//	public static class PointingDistributedReadCacheStoreBuilder<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, PrePostfetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<PrePostfetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrePostfetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<PrePostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PrePostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<PointingDistributedReadCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	public static class SortedDistributedReadListStoreBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, PrePostfetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<PrePostfetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrePostfetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<PrePostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PrePostfetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<SortedDistributedReadListStore<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
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
		private DescendantComp comp;
		private int sortSize;
		private CompoundKeyCreator keyCreator;

		private int poolSize;
//		private long keepAliveTime;
//		private ThreadCreator creator;
		private PrefetchThreadCreator prefetchCreator;
		private PostfetchThreadCreator postfetchCreator;
		private EvictThreadCreator evictCreator;
		private int maxTaskSize;
		private long dispatcherWaitTime;
//		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private int prefetchThresholdSize;
//		private NotificationCreator notificationCreator;
		private ThreadPool threadPool;
		private int prefetchCount;
//		private int postfetchCount;
		private long postfetchTimeout;

		public SortedDistributedReadListStoreBuilder()
		{
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> descendantComparator(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		/*
		public PrefetchablePointingMapCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keepAliveTime(long keepAliveTime)
		{
			this.keepAliveTime = keepAliveTime;
			return this;
		}
		*/

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> maxTaskSize(int maxTaskSize)
		{
			this.maxTaskSize = maxTaskSize;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		/*
		public PointingMapCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> notificationCreator(NotificationCreator notificationCreator)
		{
			this.notificationCreator = notificationCreator;
			return this;
		}
		*/

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		/*
		public PointingDistributedReadCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCount(int postfetchCount)
		{
			this.postfetchCount = postfetchCount;
			return this;
		}
		*/

		public SortedDistributedReadListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		@Override
		public SortedDistributedReadListStore<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new SortedDistributedReadListStore<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostfetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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
		
		/*
		public long getKeepAliveTime()
		{
			return this.keepAliveTime;
		}
		
		public ThreadCreator getThreadCreator()
		{
			return this.creator;
		}
		*/
		
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
		
		public int getMaxTaskSize()
		{
			return this.maxTaskSize;
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
		
		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}

		/*
		public NotificationCreator getNotificationCreator()
		{
			return this.notificationCreator;
		}
		*/

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
	}

	public void shutdown() throws InterruptedException, IOException
	{
		super.closeAtBase();
		this.postfetchedData.clear();
		this.postfetchedData = null;
		this.postfetchedSingleData.clear();
		this.postfetchedSingleData = null;
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
	
	public boolean isDown()
	{
		return super.isDownAtBase();
	}
	
	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePostfetchedData(String key, String cacheKey, List<Value> values, boolean isBlocking)
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
			super.addAllAtBase(cacheKey, values);
		}
	}
	
	public void savePostfetchedData(String key, String cacheKey, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			super.addAtBase(cacheKey, v);
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

	/*
	public void put(String mapKey, Value rsc) throws StoreOverflowException
	{
		this.store.put(mapKey, rsc);
	}
	
	public void putAll(String mapKey, List<Value> rscs) throws StoreOverflowException
	{
		this.store.putAll(mapKey, rscs);
	}

	public Value get(String mapKey, String rscKey)
	{
		return this.store.get(mapKey, rscKey);
	}
	*/
	
	/*
	public boolean isExisted(String mapKey, String rscKey)
	{
		return this.store.containsKey(mapKey, rscKey);
	}

	public List<Value> get(String listKey)
	{
		return this.store.getList(listKey);
	}
	*/

//	public List<Resource> get(String mapKey, int startIndex, int endIndex, String addressKey)
//	public List<Resource> get(String mapKey, int startIndex, int endIndex)
	/*
	public List<Value> get(PrePostfetchNotification notification)
	{
		List<Value> rscs = super.getTop(notification.getCacheKey(), notification.getEndIndex());
//		if (this.store.getLeftSize(mapKey, endIndex) <= this.prefetchThresholdSize)
		if (this.store.getLeftSize(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(mapKey, endIndex + 1, this.prefetchCount + 1));
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(new PrefetchNotification(mapKey, endIndex + 1, this.prefetchCount + 1)));
			this.prefetchDispatcher.enqueue(notification);
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(mapKey, endIndex + 1, this.prefetchCount + 1, addressKey));
		}
//		return this.store.get(mapKey, startIndex, endIndex);
		return this.store.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
	}
	*/
	
	/*
	public Value getMaxPointResource(String cacheKey)
	{
		return this.store.get(cacheKey, 0);
	}
	*/
	
	/*
	public List<Value> getWithoutPrefetch(String cacheKey, int startIndex, int endIndex)
	{
		return this.store.get(cacheKey, startIndex, endIndex);
	}
	
	public List<Value> getWithoutPrefetch(String cacheKey, int endIndex)
	{
		return this.store.getTop(cacheKey, endIndex);
	}

	public List<Value> getWithoutPrefetch(String cacheKey)
	{
		return this.store.getList(cacheKey);
	}
	
	public Map<String, Value> getCache(String cacheKey)
	{
		return this.store.get(cacheKey);
	}

	public Value get(String mapKey, int index)
	{
		return this.store.get(mapKey, index);
	}

	public boolean isExisted(String mapKey)
	{
		return this.store.isMapInStore(mapKey);
	}

	
	public boolean isEmpty(String mapKey)
	{
		return this.store.isEmpty(mapKey);
	}
	
	public int getSize(String mapKey)
	{
		return this.store.getSize(mapKey);
	}
	*/

	public boolean isEmpty()
	{
		return super.isEmptyAtBase();
	}

	public int getCacheSize(String cacheKey)
	{
		return super.getSizeAtBase(cacheKey);
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
	
	public Set<String> getListKeys()
	{
		return super.getListKeysAtBase();
	}

	public boolean isCacheExisted(FetchNotification notification)
	{
		boolean isExisted = !super.isEmptyAtBase(notification.getCacheKey());
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
			return super.isListInStoreAtBase(notification.getCacheKey());
		}
		return isExisted;
	}

	public boolean containsKey(FetchNotification notification)
	{
		boolean isExisted = super.containsKeyAtBase(notification.getCacheKey(), notification.getResourceKey());
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
//			this.syncs.remove(notification.getKey());
			return super.containsKeyAtBase(notification.getCacheKey(), notification.getResourceKey());
		}
		return isExisted;
	}

	public Value getValueByIndex(FetchNotification notification)
	{
		if (super.getLeftSizeAtBase(notification.getCacheKey(), notification.getResourceIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value rsc = super.getAtBase(notification.getCacheKey(), notification.getResourceIndex());
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

//	public List<Resource> getTop(String mapKey, int endIndex, String addressKey)
//	public List<Resource> getTop(String mapKey, int endIndex)
	public List<Value> getTop(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAtBase(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getEndIndex() < super.getCacheSizeAtBase())
		{
			List<Value> rscs = super.getTopAtBase(notification.getCacheKey(), notification.getEndIndex());
			if (rscs != null)
			{
				if (rscs.size() >= notification.getEndIndex())
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
//			notification.setStartIndex(rscs.size() - 1);
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//				this.syncs.remove(notification.getKey());
//				return super.getTop(notification.getCacheKey(), notification.getEndIndex());
				List<Value> values = this.postfetchedData.get(notification.getKey());
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

	public List<Value> getRange(FetchNotification notification) throws DistributedListFetchException
	{
		if (super.getLeftSizeAtBase(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getEndIndex() - notification.getStartIndex() < super.getCacheSizeAtBase())
		{
			List<Value> rscs = super.getRangeAtBase(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
			if (rscs != null)
			{
				if (rscs.size() >= notification.getEndIndex() - notification.getStartIndex())
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
//			notification.setStartIndex(notification.getStartIndex() + rscs.size() - 1);
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//				this.syncs.remove(notification.getKey());
//				return super.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
				List<Value> values = this.postfetchedData.get(notification.getKey());
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
	public Set<String> getMapKeys()
	{
		return this.store.getMapKeys();
	}
	
	public void remove(String mapKey, Set<String> rscKeys)
	{
		this.store.remove(mapKey, rscKeys);
	}
	
	public void remove(String mapKey, String rscKey)
	{
		this.store.remove(mapKey, rscKey);
	}
	
	public void clear(String mapKey)
	{
		this.store.clear(mapKey);
	}
	
	public void clear(Set<String> mapKeys)
	{
		this.store.clear(mapKeys);
	}
	*/

	public void putAllLocally(String mapKey, List<Value> pointings)
	{
		super.addAllAtBase(mapKey, pointings);
	}
	
	public void putLocally(String mapKey, Value value)
	{
		super.addAtBase(mapKey, value);
	}

	@Override
	public void evict(String k, Value v)
	{
		/*
		if (super.isEmpty(v.getCacheKey()))
		{
			super.removeCacheKey(v.getCacheKey());
		}
		*/
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
//		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		// The Ehcache assigns the key through the evict(String k, Value v) interface. But it usually does not make sense to the application level. So I attempt the assign the cache key to replace it since the cache key sometimes is used on the application level, e.g., the CircleTimingPageCache. In the case, the cache key is not generated by the information from the Value, i.e., TimingPage. Thus, it is required to keep the cache key in the notification. Usually, the store needs to be updated for that. 08/09/2019, Bing Li
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(v.getCacheKey(), v));
		super.removeKeyAtBase(v.getCacheKey(), k);
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
