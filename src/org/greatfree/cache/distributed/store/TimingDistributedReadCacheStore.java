package org.greatfree.cache.distributed.store;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CacheTiming;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.cache.distributed.ListFetchNotification;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * It is replaced by the Pointing-Based caches. 08/22/2018, Bing Li
 */

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * The so-called semi represents that no new data is created through the cache. So it is unnecessary to replicate the new data to the backend servers. 06/12/2018, Bing Li
 */

// Created: 01/30/2018, Bing Li
// public class PrefetchableTimingMapCacheStore<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PrefetchableTimingMapCacheStore<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PrefetchableTimingMapCacheStore<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class TimingPrefetchMapStore<Value extends CacheTiming, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> extends TimingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>
class TimingDistributedReadCacheStore<Value extends CacheTiming, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, PrePostNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<PrePostNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<PrePostNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends TimingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>
{
//	private TimingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp> store;
	private NotificationObjectDispatcher<PrePostNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;
//	private NotificationCreator notificationCreator;
	
	private NotificationObjectDispatcher<PrePostNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private final int postfetchCount;
	private final long postfetchTimeout;
	private Map<String, Sync> syncs;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;
	
	private ThreadPool threadPool;

//	public PrefetchableTimingMapCacheStore(TimingMapCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> builder)
	public TimingDistributedReadCacheStore(SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
//		this.store = new TimingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp>(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PrePostNotification, PrefetchThread, PrefetchThreadCreator>()
//				.threadPool(builder.getThreadPool())
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PrePostNotification, PostfetchThread, PostfetchThreadCreator>()
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
//		this.notificationCreator = builder.getNotificationCreator();
		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
		this.postfetchCount = builder.getPostfetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();

		this.syncs = new ConcurrentHashMap<String, Sync>();
	}

//	public static class TimingMapCacheBuilder<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchableTimingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
//	public static class TimingMapCacheBuilder<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchableTimingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
//	public static class PrefetchableTimingMapCacheStoreBuilder<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchableTimingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator>>
	public static class SemiTimingDistributedCacheStoreBuilder<Value extends CacheTiming, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, PrePostNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<PrePostNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<PrePostNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<TimingDistributedReadCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	{
		private String storeKey;
		private Factory factory;
		private String rootPath;
		private int storeSize;
		private int cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private DescendantComp comp;
		private CompoundKeyCreator keyCreator;

		private int poolSize;
//		private long keepAliveTime;
		private PrefetchThreadCreator prefechCreator;
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
		private int postfetchCount;
		private long postfetchTimeout;

		public SemiTimingDistributedCacheStoreBuilder()
		{
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
//			this.rootPath = rootPath;
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeSize(int storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(int cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> descendantComparator(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		/*
		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keepAliveTime(long keepAliveTime)
		{
			this.keepAliveTime = keepAliveTime;
			return this;
		}
		*/

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThreadCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefechCreator = prefetchCreator;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchThreadCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> maxTaskSize(int maxTaskSize)
		{
			this.maxTaskSize = maxTaskSize;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		/*
		public TimingMapCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> notificationCreator(NotificationCreator notificationCreator)
		{
			this.notificationCreator = notificationCreator;
			return this;
		}
		*/

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCount(int postfetchCount)
		{
			this.postfetchCount = postfetchCount;
			return this;
		}

		public SemiTimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		/*
		@Override
		public PrefetchableTimingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> build()
		{
			return new PrefetchableTimingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>(this);
		}
		*/

		@Override
		public TimingDistributedReadCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new TimingDistributedReadCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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
		
		public int getStoreSize()
		{
			return this.storeSize;
		}
		
		public int getCacheSize()
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
		
		public DescendantComp getComparator()
		{
			return this.comp;
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
		*/
		
		public PrefetchThreadCreator getThreadCreator()
		{
			return this.prefechCreator;
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

		public int getPostfetchCount()
		{
			return this.postfetchCount;
		}

		public long getPostfetchTimeout()
		{
			return this.postfetchTimeout;
		}
	}

	public void dispose() throws InterruptedException
	{
		super.close();
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

	public void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	/*
	public boolean isDown()
	{
		return this.store.isDown();
	}
	
	public void put(String mapKey, Value rsc) throws StoreOverflowException
	{
		this.store.put(mapKey, rsc);
	}
	
	public void putAll(String mapKey, List<Value> rscs) throws StoreOverflowException
	{
		this.store.putAll(mapKey, rscs);
	}
	
	public boolean isResourceAvailable(String mapKey, String rscKey)
	{
		return this.store.isResourceAvailable(mapKey, rscKey);
	}
	
	public Value get(String mapKey, String rscKey)
	{
		return this.store.get(mapKey, rscKey);
	}
	*/
	
//	public List<Resource> get(String mapKey, int startIndex, int endIndex, String addressKey)
//	public List<Resource> get(String mapKey, int startIndex, int endIndex)
	public List<Value> getRange(PrePostNotification notification)
	{
		List<Value> rscs = super.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
		if (rscs != null)
		{
			if (rscs.size() >= notification.getEndIndex() - notification.getStartIndex() + 1)
			{
//				if (this.store.getLeftSize(mapKey, endIndex) <= this.prefetchThresholdSize)
				if (super.getLeftSize(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
				{
					if (!this.prefetchDispatcher.isReady())
					{
						this.threadPool.execute(this.prefetchDispatcher);
					}
//					this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(mapKey, endIndex + 1, this.prefetchCount + 1));
//					this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(new PrefetchNotification(mapKey, endIndex + 1, this.prefetchCount + 1)));
					this.prefetchDispatcher.enqueue(notification);
//					this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(mapKey, endIndex + 1, this.prefetchCount + 1, addressKey));
				}
				return rscs;
			}
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//		return this.store.get(mapKey, startIndex, endIndex);
		return super.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
	}

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

	public Value get(String mapKey, int index)
	{
		return this.store.get(mapKey, index);
	}

	public boolean isExisted(String mapKey)
	{
		return this.store.isMapInStore(mapKey);
	}

	public boolean isEmpty()
	{
		return this.store.isEmpty();
	}
	
	public boolean isEmpty(String mapKey)
	{
		return this.store.isEmpty(mapKey);
	}
	
	public int getCacheSize(String mapKey)
	{
		return this.store.getCacheSize(mapKey);
	}
	
	public int getMaxIndex(String mapKey)
	{
		return this.store.getMaxIndex(mapKey);
	}
	*/
	
	public Value getEarliestResource(String mapKey)
	{
		return super.get(mapKey, super.getMaxIndex(mapKey));
	}
	
	public Value getLatestResource(String mapKey)
	{
		return super.getMinResource(mapKey);
	}
	
	/*
	public int getLeftSize(String mapKey, int endIndex)
	{
		return this.store.getLeftSize(mapKey, endIndex);
	}
	*/

//	public List<Resource> getTop(String mapKey, int endIndex, String addressKey)
//	public List<Resource> getTop(String mapKey, int endIndex)
	public List<Value> getTop(PrePostNotification notification)
	{
		List<Value> rscs = super.getTop(notification.getCacheKey(), notification.getEndIndex());
		if (rscs != null)
		{
			if (rscs.size() >= notification.getEndIndex() + 1)
			{
//				if (this.store.getLeftSize(mapKey, endIndex) <= this.prefetchThresholdSize)
				if (super.getLeftSize(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
				{
					if (!this.prefetchDispatcher.isReady())
					{
						this.threadPool.execute(this.prefetchDispatcher);
					}
//					this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(mapKey, endIndex + 1, this.prefetchCount + 1));
//					this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(new PrefetchNotification(mapKey, endIndex + 1, this.prefetchCount + 1)));
					this.prefetchDispatcher.enqueue(notification);
//					this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(mapKey, endIndex + 1, this.prefetchCount + 1, addressKey));
				}
				return rscs;
			}
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//		return this.store.getTop(mapKey, endIndex);
		return super.getTop(notification.getCacheKey(), notification.getEndIndex());
	}

	/*
	public Set<String> getMapKeys()
	{
		return this.store.getCacheKeys();
	}

	public List<Value> get(String listKey)
	{
		return this.store.getList(listKey);
	}
	*/
	
	 public int getPrefetchCount()
	 {
		 return this.prefetchCount;
	 }

	public int getPostfetchCount()
	{
		return this.postfetchCount;
	}
	
	/*
	public void remove(String mapKey, Set<String> rscKeys)
	{
		this.store.remove(mapKey, rscKeys);
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

	@Override
	public void evict(String k, Value v)
	{
		/*
		if (super.isEmpty(v.getCacheKey()))
		{
			super.removeCacheKey(v.getCacheKey());
		}
		*/
		super.removeKey(v.getCacheKey(), k);
		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
		this.evictedDispatcher.enqueue(notification);
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
