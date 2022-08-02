package org.greatfree.cache.distributed.store;

import java.util.ArrayList;
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
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * The version is tested in the Clouds project. But it is replaced by the Pointing-Based caches. 08/22/2018, Bing Li
 */

// Created: 02/25/2018, Bing Li
// public class PrePostfetchableTimingMapCacheStore<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends ListPrePostfetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>>
class TimingDistributedCacheStore<Value extends CacheTiming, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<TimingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<TimingReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends TimingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>
{
//	private TimingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp> store;
	private NotificationObjectDispatcher<TimingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;

	private NotificationObjectDispatcher<FetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;

	private NotificationObjectDispatcher<FetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
//	private final int postfetchCount;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private Map<String, List<Value>> postfetchedData;
	private Map<String, Value> postfetchedSingleData;
	
	private Map<String, Sync> syncs;
	private ThreadPool threadPool;

	public TimingDistributedCacheStore(TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
//		this.store = new TimingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp>(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());

		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<TimingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
//				.threadPool(builder.getThreadPool())
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

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

		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
//		this.postfetchCount = builder.getPostfetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();

		this.syncs = new ConcurrentHashMap<String, Sync>();
	}

//	public static class PrePostfetchableTimingMapCacheStoreBuilder<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends ListPrePostfetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>> implements Builder<PrePostfetchableTimingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator>>
	public static class TimingDistributedCacheStoreBuilder<Value extends CacheTiming, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<TimingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<TimingReplicateNotification<Value>, ReplicateThread>, PrePostNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<PrePostNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<PrePostNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<TimingDistributedCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
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
		// private long keepAliveTime;
		private ReplicateThreadCreator replicateCreator;
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
		private ThreadPool threadPool;
		private int prefetchCount;
//		private int postfetchCount;
		private long postfetchTimeout;

		public TimingDistributedCacheStoreBuilder()
		{
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> descendantComparator(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeSize(int storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(int cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}


		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		/*
		 * public PrePostfetchableTimingMapCacheStoreBuilder<Resource, Factory,
		 * CompoundKeyCreator, DescendantComp, Notification, PrefetchThread,
		 * PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator>
		 * keepAliveTime(long keepAliveTime) { this.keepAliveTime = keepAliveTime;
		 * return this; }
		 */

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateThreadCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThreadCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchThreadCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> maxTaskSize(int maxTaskSize)
		{
			this.maxTaskSize = maxTaskSize;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		/*
		public TimingDistributedMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCount(int postfetchCount)
		{
			this.postfetchCount = postfetchCount;
			return this;
		}
		*/

		public TimingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		@Override
		public TimingDistributedCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new TimingDistributedCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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
		
		public int getPoolSize()
		{
			return this.poolSize;
		}
		
		 /* 
		 * public long getKeepAliveTime() { return this.keepAliveTime; }
		 */
		
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

		/*
		 * public String getRemoteServerIP() { return this.remoteServerIP; }
		 * 
		 * public int getRemoteServerPort() { return this.remoteServerPort; }
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

	public void dispose() throws InterruptedException
	{
		super.close();
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

	/*
	public boolean isDown()
	{
		return super.isDown();
	}
	*/

	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}

	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePostfetchedData(String key, String cacheKey, List<Value> values)
	{
		/*
		int index = 0;
		for (Value entry : values)
		{
			System.out.println("PointingDistributedCacheStore-PointingDistributedCacheStore(): " + index++ + ") points = " + entry.getPoints());
		}
		*/
		// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
		if (values != null)
		{
			this.postfetchedData.put(key, new ArrayList<Value>(values));
		}
		this.signal(key);
		if (values != null)
		{
			super.addAll(cacheKey, values);
		}
	}

	public void savePostfetchedData(String key, String cacheKey, Value v)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			super.add(cacheKey, v);
			this.postfetchedSingleData.put(key, v);
		}
		this.signal(key);
		/*
		if (v != null)
		{
			this.add(cacheKey, v);
		}
		*/
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
	*/

	public int getCacheSize(String cacheKey)
	{
		return super.getSize(cacheKey);
	}

	public Value getValueByKey(FetchNotification notification)
	{
		Value rsc = super.get(notification.getCacheKey(), notification.getResourceKey());
		if (rsc != null)
		{
			return rsc;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//		return super.get(notification.getCacheKey(), notification.getResourceKey());
		rsc = this.postfetchedSingleData.get(notification.getKey());
		this.postfetchedSingleData.remove(notification.getKey());
		return rsc;
	}

	public boolean containsKey(FetchNotification notification)
	{
		boolean isExisted = super.containsKey(notification.getCacheKey(), notification.getResourceKey());
		if (isExisted)
		{
			return true;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		return super.containsKey(notification.getCacheKey(), notification.getResourceKey());
	}

	/*
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	public List<Value> getAllResourcesByCacheKey(FetchNotification notification)
	{
		List<Value> rscs = super.getList(notification.getCacheKey());
		if (rscs != null)
		{
			if (notification.isPostfetchForNullOnly())
			{
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
		return super.getList(notification.getCacheKey());
	}
	*/

	public Value getMaxValue(FetchNotification notification)
	{
//		return super.get(cacheKey, 0);
		Value rsc = super.get(notification.getCacheKey(), 0);
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
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		rsc = this.postfetchedSingleData.get(notification.getKey());
		this.postfetchedSingleData.remove(notification.getKey());
		return rsc;
	}

	/*
	public List<Value> getWithoutFetch(String cacheKey, int startIndex, int endIndex)
	{
		return this.store.get(cacheKey, startIndex, endIndex);
	}

	public List<Value> getWithoutFetch(String cacheKey, int endIndex)
	{
		return this.store.getTop(cacheKey, endIndex);
	}

	public List<Value> getWithoutFetch(String cacheKey)
	{
		return this.store.getList(cacheKey);
	}
	*/

	/*
	public Map<String, Value> getAllResourcesInMapByCacheKey(Notification notification)
	{
		Map<String, Value> rscs = this.store.get(notification.getCacheKey());
		if (rscs != null)
		{
			if (notification.isPostfetchForNullOnly())
			{
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
		// this.syncs.remove(notification.getKey());
		return this.store.get(notification.getCacheKey());
	}
	*/

	/*
	public Value getValueByIndex(String cacheKey, int index)
	{
		return this.store.get(cacheKey, index);
	}
	*/

	public Value getValueByIndex(FetchNotification notification)
	{
		Value rsc = super.get(notification.getCacheKey(), notification.getResourceIndex());
		if (rsc != null)
		{
			if (super.getLeftSize(notification.getCacheKey(), notification.getResourceIndex()) <= this.prefetchThresholdSize)
			{
				if (!this.prefetchDispatcher.isReady())
				{
					this.threadPool.execute(this.prefetchDispatcher);
				}
				this.prefetchDispatcher.enqueue(notification);
			}
			return rsc;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		rsc = this.postfetchedSingleData.get(notification.getKey());
		this.postfetchedSingleData.remove(notification.getKey());
		return rsc;
	}

	public boolean isCacheExisted(FetchNotification notification)
	{
		boolean isExisted = super.isCacheInStore(notification.getCacheKey());
		if (isExisted)
		{
			return true;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		// this.syncs.remove(notification.getKey());
		return super.isCacheInStore(notification.getCacheKey());
	}

	/*
	public boolean isEmpty()
	{
		return this.store.isEmpty();
	}
	*/

	public boolean isCacheEmpty(FetchNotification notification)
	{
		boolean isExisted = super.isEmpty(notification.getCacheKey());
		if (!isExisted)
		{
			return false;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
		// this.syncs.remove(notification.getKey());
		return super.isEmpty(notification.getCacheKey());
	}

	/*
	 * public int getCacheSize(Notification notification) { int size =
	 * this.store.getCacheSize(notification.getCacheKey()); if (size !=
	 * IndependentUtilConfig.NO_COUNT) { return size; } if
	 * (!this.postfetchDispatcher.isReady()) {
	 * this.threadPool.execute(this.postfetchDispatcher); }
	 * this.postfetchDispatcher.enqueue(notification);
	 * this.syncs.put(notification.getKey(), new Sync(false));
	 * this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout); //
	 * this.syncs.remove(notification.getKey()); return
	 * this.store.getCacheSize(notification.getCacheKey()); }
	 */

	/*
	public int getCacheSize(String cacheKey)
	{
		return this.store.getCacheSize(cacheKey);
	}
	*/

	/*
	public int getPostfetchCount()
	{
		return this.postfetchCount;
	}
	*/

	/*
	public int getLeftSize(String cacheKey, int endIndex)
	{
		return this.store.getLeftSize(cacheKey, endIndex);
	}
	*/

	public List<Value> getTop(FetchNotification notification) throws DistributedListFetchException
	{
		if (notification.getEndIndex() < super.getCacheSize())
		{
			List<Value> values = super.getTop(notification.getCacheKey(), notification.getEndIndex());
			if (values != null)
			{
				if (super.getLeftSize(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
				{
					if (!this.prefetchDispatcher.isReady())
					{
						this.threadPool.execute(this.prefetchDispatcher);
					}
					this.prefetchDispatcher.enqueue(notification);
				}
				if (values.size() > notification.getEndIndex())
				{
					return values;
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//			return super.getTop(notification.getCacheKey(), notification.getEndIndex());
			values = this.postfetchedData.get(notification.getKey());
			this.postfetchedData.remove(notification.getKey());
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSize(), notification.getEndIndex());
		}
	}

	public List<Value> getRange(FetchNotification notification) throws DistributedListFetchException
	{
		if (notification.getEndIndex() - notification.getStartIndex() < super.getCacheSize())
		{
			List<Value> values = super.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
			if (values != null)
			{
//				if (rscs.size() >= notification.getEndIndex() - notification.getStartIndex() + 1)
//				{
				if (super.getLeftSize(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
				{
					if (!this.prefetchDispatcher.isReady())
					{
						this.threadPool.execute(this.prefetchDispatcher);
					}
					this.prefetchDispatcher.enqueue(notification);
				}
				if (values.size() >= notification.getEndIndex() - notification.getStartIndex())
				{
					return values;
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//			return super.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
			values = this.postfetchedData.get(notification.getKey());
			this.postfetchedData.remove(notification.getKey());
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSize(), notification.getEndIndex() - notification.getStartIndex() + 1);
		}
	}

	/*
	public Set<String> getMapKeys()
	{
		return this.store.getCacheKeys();
	}

	public void remove(String mapKey, Set<String> rscKeys)
	{
		this.store.remove(mapKey, rscKeys);
	}

	public void remove(String mapKey, String rscKey)
	{
		this.store.removeByResourceKey(mapKey, rscKey);
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
	public void put(TimingReplicateNotification<Value> notification)
	{
		super.add(notification.getValue().getCacheKey(), notification.getValue());
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
	}
	
	public void putAll(TimingReplicateNotification<Value> notification)
	{
		super.addAll(notification.getCacheKey(), notification.getValues());
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
	}
	
	public void putAllLocally(String mapKey, List<Value> timings)
	{
		super.addAll(mapKey, timings);
	}
	
	public void putLocally(String mapKey, Value value)
	{
		super.add(mapKey, value);
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
