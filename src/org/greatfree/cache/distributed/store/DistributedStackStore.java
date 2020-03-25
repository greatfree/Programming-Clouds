package org.greatfree.cache.distributed.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.cache.distributed.KickOffNotification;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * The version is created and tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * Locking is done. 08/22/2018, Bing Li
 * 
 * Locking still has problems. 08/11/2018, Bing Li
 */

// Created: 08/05/2018, Bing Li
// public class PointingDistributedStackStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends StackStore<Value, Factory, CompoundKeyCreator>

// I think the distributed stack is done. The locking should protect my DB only. The cache should not be locked. 08/12/2018, Bing Li
// Postfetching needs to get more data than the count of the rest ones. For a stack, if no additional data is obtained, all of the postfetched data is popped. Next time, postfetching has to be performed again. Then, the performance is lowered. The size of the additional data is equal to the prefetching count. Thus, postfetching has the prefetching function in the case. 08/11/2018, Bing Li
// Postfetching is still useful. When doing that, the size to be postfetched is equal to the count of the rest ones. 08/11/2018, Bing Li
// The distributed stack should NOT have the feature of postfetching. Postfetching is performed when replicating is available. When popped data size is smaller than requested, that indicates some data is evicted to the terminal stack. It does not matter too much. Postfetching got the data that is completely irrelevant to the popped data in terms of the order because the terminal stack only takes the overflowed data from the distributed stack. 08/09/2018, Bing Li
// It is more clear to replicate the overflowed data with the replicating thread. 08/08/2018, Bing Li
// The replicating is not needed for the new design since overflowed data is evicted. 08/08/2018, Bing Li
// Although evicting must affect the order, it needs to be added. But the evicting data should contain the overflowed data. 08/08/2018, Bing Li
// Since data is pushed on the top of a stack, it is not proper to push evicted data on the top. Because of the replicating, it is also unnecessary to evict data. 08/08/2018, Bing Li
//public class PointingDistributedStackStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, StackNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<StackNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<StackNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends StackStore<Value, Factory, CompoundKeyCreator>
// public class DistributedStackStore<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, ReplicateThread extends NotificationObjectQueue<ReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ReplicateNotification<Value>, ReplicateThread>, StackNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<StackNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PrefetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends StackStore<Value, Factory, CompoundKeyCreator>
public class DistributedStackStore<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, ReplicateThread extends NotificationObjectQueue<ReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ReplicateNotification<Value>, ReplicateThread>, StackNotification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<StackNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<StackNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<StackNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends StackStore<Value, Factory, CompoundKeyCreator>
{
	private NotificationObjectDispatcher<ReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;

	private NotificationObjectDispatcher<StackNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;

	private NotificationObjectDispatcher<StackNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private Map<String, List<Value>> postfetchedData;
//	private Map<String, Value> postfetchedSingleData;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;
	
	private AtomicBoolean isOverflowed;
	
//	public PointingDistributedStackStore(PointingDistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
//	public PointingDistributedStackStore(PointingDistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, StackNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
//	public DistributedStackStore(DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, StackNotification, PrefetchThread, PrefetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	public DistributedStackStore(DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, StackNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator());

		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<ReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getReplicateThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<StackNotification, PrefetchThread, PrefetchThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getPrefetchThreadCreator())
				.notificationQueueSize(builder.getNotificationQueueSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();

		this.postfetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<StackNotification, PostfetchThread, PostfetchThreadCreator>()
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
//		this.postfetchedSingleData = new ConcurrentHashMap<String, Value>();

		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();

		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.isOverflowed = new AtomicBoolean(false);
	}

//	public static class PointingDistributedStackStoreBuilder<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<PointingDistributedStackStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
//	public static class PointingDistributedStackStoreBuilder<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<PointingDistributedStackStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
//	public static class DistributedStackStoreBuilder<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, ReplicateThread extends NotificationObjectQueue<ReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ReplicateNotification<Value>, ReplicateThread>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedStackStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, EvictThread, EvictThreadCreator>>
	public static class DistributedStackStoreBuilder<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, ReplicateThread extends NotificationObjectQueue<ReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<ReplicateNotification<Value>, ReplicateThread>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<DistributedStackStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
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

		public DistributedStackStoreBuilder()
		{
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateThreadCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThreadCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchThreadCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> notificationQueueSize(int notificationQueueSize)
		{
			this.notificationQueueSize = notificationQueueSize;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		public DistributedStackStoreBuilder<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		@Override
		public DistributedStackStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new DistributedStackStore<Value, Factory, CompoundKeyCreator, ReplicateThread, ReplicateThreadCreator, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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
	
	public void dispose() throws InterruptedException
	{
		super.closeAtBase();
		this.postfetchedData.clear();
//		this.postfetchedData = null;
//		this.postfetchedSingleData.clear();
//		this.postfetchedSingleData = null;
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

	// Postfetching needs to get more data than the count of the rest ones. For a stack, if no additional data is obtained, all of the postfetched data is popped. Next time, postfetching has to be performed again. Then, the performance is lowered. The size of the additional data is equal to the prefetching count. Thus, postfetching has the prefetching function in the case. 08/11/2018, Bing Li
	public void savePostfetchedData(String key, String stackKey, int postfetchCount, List<Value> values, boolean isBlocking)
	{
		// I forget why the below line is required. 03/19/2019, Bing Li
		int poppingCount = postfetchCount - this.prefetchCount;
		
//		System.out.println("DistributedStackStore-savePostfetchedData(): values size = " + values.size());
//		System.out.println("DistributedStackStore-savePostfetchedData(): poppingCount = " + poppingCount);
		
		if (isBlocking)
		{
			if (values != null)
			{
				// The notify/wait mechanism results in the concurrently accessing on the values. It might lead to ConcurrentModificationException. So it is necessary to send out the values first and then save them into the cache. Or make a copy. 08/04/2018, Bing Li
				
				if (this.postfetchedData.containsKey(key))
				{
					this.postfetchedData.remove(key);
				}
				if (values.size() <= poppingCount)
				{
					this.postfetchedData.put(key, new ArrayList<Value>(values));
				}
				else
				{
					if (poppingCount > 0)
					{
						this.postfetchedData.put(key, new ArrayList<Value>(values.subList(0, poppingCount)));
					}
				}
			}
			this.signal(key);
		}
		if (values != null)
		{
			if (values.size() > poppingCount && poppingCount >= 0)
			{
//					this.pushAllBottomLocally(cacheKey, values.subList(values.size() - poppingCount - 1, values.size() - 1));
				super.pushAllBottomAtBase(stackKey, values.subList(poppingCount, values.size()));
			}
		}
	}

	/*
	 * No single data is postfetched in the stack case. 08/11/2018, Bing Li
	 */
	/*
	public void savePostfetchedData(String key, String cacheKey, Value v)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			this.push(cacheKey, v);
			this.postfetchedSingleData.put(key, v);
		}
		this.signal(key);
	}
	*/
	
	public boolean isStoreEmpty()
	{
		return super.isEmptyAtBase();
	}
	
	public boolean isStackExisted(String stackKey)
	{
		return super.isStackInStoreAtBase(stackKey);
	}
	
	public Set<String> getStacksKeys()
	{
		return super.getCacheKeysAtBase();
	}

	public Value pop(StackNotification notification)
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value v = super.popAtBase(notification.getCacheKey());
		if (v != null)
		{
			this.isOverflowed.set(false);
			return v;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		// When popping multiple data, the solution is good. It happens when the local data is insufficient and should obtain from the terminal stack. 08/12/2018, Bing Li
		// To pop a single data, the postfetching is usually performed when the data is evicted. So the posfetched data is usually not the right one. For the system of the large-scale, I just leave the solution here since it does not affect too much. 08/12/2018, Bing Li 
		// Postfetching needs to get more data than the count of the rest ones. For a stack, if no additional data is obtained, all of the postfetched data is popped. Next time, postfetching has to be performed again. Then, the performance is lowered. The size of the additional data is equal to the prefetching count. Thus, postfetching has the prefetching function in the case. 08/11/2018, Bing Li
		notification.setPostfetchCount(this.prefetchCount + 1);
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//			return super.pop(notification.getCacheKey());
//			v = this.postfetchedSingleData.get(notification.getKey());
//			this.postfetchedSingleData.remove(notification.getKey());
			if (this.postfetchedData.get(notification.getKey()).size() > 0)
			{
				v = this.postfetchedData.get(notification.getKey()).get(0);
//				System.out.println("DistributedStackStore-pop() [single]: " + v.getCacheKey() + "'s element is obtained!");
			}
			else
			{
//				System.out.println("DistributedStackStore-pop() [single]: " + notification.getCacheKey() + "'s element is NOT obtained!");
			}
			this.postfetchedData.remove(notification.getKey());
			this.isOverflowed.set(false);
			return v;
		}
		return v;
	}

	public List<Value> popAll(StackNotification notification) throws DistributedListFetchException
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getKickOffCount() < super.getCacheSizeAtBase())
		{
			List<Value> values = super.popAtBase(notification.getCacheKey(), notification.getKickOffCount());
			if (values != null)
			{
				if (values.size() >= notification.getKickOffCount())
				{
					this.isOverflowed.set(false);
					return values;
				}
				else
				{
					/*
					 * In the previous solution, it assumes that the terminal cache has the data that covers its distributed ones. But in practice, the assumption might not be correct. So it is reasonable to put the loaded data into the postfetchedData. Otherwise, the data from the terminal might overwrite the loaded data. 04/30/2019, Bing Li
					 */
					this.postfetchedData.put(notification.getKey(), values);
				}
			}
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			// Postfetching needs to get more data than the count of the rest ones. For a stack, if no additional data is obtained, all of the postfetched data is popped. Next time, postfetching has to be performed again. Then, the performance is lowered. The size of the additional data is equal to the prefetching count. Thus, postfetching has the prefetching function in the case. 08/11/2018, Bing Li
			notification.setPostfetchCount(notification.getKickOffCount() - values.size() + this.prefetchCount);
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//				return super.pop(notification.getCacheKey(), notification.getKickOffCount());
				List<Value> restValues = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				if (restValues != null)
				{
					values.addAll(restValues);
				}
				this.isOverflowed.set(false);
				return values;
			}
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getKickOffCount());
		}
	}

	public Value popBottom(String stackKey)
	{
		return super.popBottomAtBase(stackKey);
	}

	public List<Value> popBottom(String stackKey, int size)
	{
		return super.popBottomAtBase(stackKey, size);
	}
	
	public List<Value> popLocally(String stackKey, int size)
	{
		return super.popAtBase(stackKey, size);
	}
	
	public Value peekLocally(String stackKey)
	{
		return super.peekAtBase(stackKey);
	}

	public Value peek(StackNotification notification)
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value v = super.peekAtBase(notification.getCacheKey());
		if (v != null)
		{
			return v;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		// Postfetching needs to get more data than the count of the rest ones. For a stack, if no additional data is obtained, all of the postfetched data is popped. Next time, postfetching has to be performed again. Then, the performance is lowered. The size of the additional data is equal to the prefetching count. Thus, postfetching has the prefetching function in the case. 08/11/2018, Bing Li
		notification.setPostfetchCount(this.prefetchCount + 1);
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//			return super.peek(notification.getCacheKey());
//			v = this.postfetchedSingleData.get(notification.getKey());
//			this.postfetchedSingleData.remove(notification.getKey());
			if (this.postfetchedData.get(notification.getKey()).size() > 0)
			{
				v = this.postfetchedData.get(notification.getKey()).get(0);
			}
			this.postfetchedData.remove(notification.getKey());
			return v;
		}
		return v;
	}
	
	public List<Value> peekAllLocally(String stackKey, int endIndex)
	{
		return super.peekAtBase(stackKey, endIndex);
	}
	
	public List<Value> peekAll(StackNotification notification) throws DistributedListFetchException
	{
		if (super.getStackSizeAtBase(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		if (notification.getKickOffCount() < super.getCacheSizeAtBase())
		{
//			System.out.println("DistributedStackStore-peekAll(): kickOffCount = " + notification.getKickOffCount());
			List<Value> values = super.peekAtBase(notification.getCacheKey(), notification.getKickOffCount());
//			System.out.println("DistributedStackStore-peekAll(): values size = " + values.size());
			if (values != null)
			{
				if (values.size() >= notification.getKickOffCount())
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
			// Postfetching needs to get more data than the count of the rest ones. For a stack, if no additional data is obtained, all of the postfetched data is popped. Next time, postfetching has to be performed again. Then, the performance is lowered. The size of the additional data is equal to the prefetching count. Thus, postfetching has the prefetching function in the case. 08/11/2018, Bing Li
			notification.setPostfetchCount(notification.getKickOffCount() - values.size() + this.prefetchCount);
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//				return super.peek(notification.getCacheKey(), notification.getKickOffCount());
				List<Value> restValues = this.postfetchedData.get(notification.getKey());
				this.postfetchedData.remove(notification.getKey());
				if (restValues != null)
				{
					values.addAll(restValues);
				}
				return values;
			}
			return values;
		}
		else
		{
			throw new DistributedListFetchException(super.getCacheSizeAtBase(), notification.getKickOffCount());
		}
	}
	
	public List<Value> peekRangeLocally(String stackKey, int startIndex, int endIndex) throws IndexOutOfRangeException
	{
		return super.peekAtBase(stackKey, startIndex, endIndex);
	}
	
	public List<Value> peekRange(StackNotification notification) throws IndexOutOfRangeException
	{
//		System.out.println("DistributedStackStore-peekRange(): stack size = " + super.getStackSize(notification.getCacheKey())); 
//		System.out.println("DistributedStackStore-peekRange(): end index = " + notification.getEndIndex()); 
//		System.out.println("DistributedStackStore-peekRange(): prefetchThresholdSize = " + this.prefetchThresholdSize); 
		if ((super.getStackSizeAtBase(notification.getCacheKey()) - notification.getEndIndex() - 1) <= this.prefetchThresholdSize)
		{
//			System.out.println("DistributedStackStore-peekRange(): prefetching is Started ..."); 
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		List<Value> values = super.peekAtBase(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
		if (values != null)
		{
//			if (values.size() >= notification.getPeekRangeSize())
			if (values.size() >= notification.getKickOffCount())
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
//		notification.setPostfetchCount(notification.getPeekRangeSize() - values.size()  + this.prefetchCount);
//		notification.setPostfetchCount(notification.getKickOffCount() - values.size()  + this.prefetchCount);
		notification.setPostfetchIndex(values.size());
		this.postfetchDispatcher.enqueue(notification);
		if (notification.isBlocking())
		{
			this.syncs.put(notification.getKey(), new Sync(false));
			this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
			List<Value> restValues = this.postfetchedData.get(notification.getKey());
			this.postfetchedData.remove(notification.getKey());
			if (restValues != null)
			{
				values.addAll(restValues);
			}
			return values;
		}
		return values;
	}
	
	public Value get(StackNotification notification) throws IndexOutOfRangeException
	{
		if ((super.getStackSizeAtBase(notification.getCacheKey()) - notification.getStartIndex() - 1) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		Value v = super.getAtBase(notification.getCacheKey(), notification.getStartIndex());
		if (v != null)
		{
			return v;
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
			this.postfetchedData.remove(notification.getKey());
			return super.getAtBase(notification.getCacheKey(), notification.getStartIndex());
		}
		return v;
	}
	
//	public void push(PointingReplicateNotification<Value> notification)
//	public void push(String cacheKey, Value v)
	public void push(Value v)
	{
		// Originally, I think the stack and the backend stack for a sequential order. So only the bottom data is replicated. But it must cause the inconsistent between distributed data. Moreover, now data can be fetched through indexing. So the current approach is not necessary. 08/06/2019, Bing Li
		/*
		if (this.isOverflowed.get())
		{
			Value oldValue = super.popBottom(v.getCacheKey());
			if (oldValue != null)
			{
				if (!this.replicateDispatcher.isReady())
				{
					this.threadPool.execute(this.replicateDispatcher);
				}
				this.replicateDispatcher.enqueue(new ReplicateNotification<Value>(oldValue));
			}
		}
		*/
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
//		System.out.println("1) DistributedStackStore-push() [SINGLE] is initialized!");
		this.replicateDispatcher.enqueue(new ReplicateNotification<Value>(v));
		super.pushAtBase(v.getCacheKey(), v);
	}
	
	public void pushLocally(String stackKey, Value v)
	{
		super.pushAtBase(stackKey, v);
	}
	
	public void pushAllLocally(String stackKey, List<Value> values)
	{
		super.pushAllAtBase(stackKey, values);
	}

	public void push(String stackKey, Value v)
	{
		// Originally, I think the stack and the backend stack for a sequential order. So only the bottom data is replicated. But it must cause the inconsistent between distributed data. Moreover, now data can be fetched through indexing. So the current approach is not necessary. 08/06/2019, Bing Li
		/*
		if (this.isOverflowed.get())
		{
			Value oldValue = super.popBottom(stackKey);
			if (oldValue != null)
			{
				if (!this.replicateDispatcher.isReady())
				{
					this.threadPool.execute(this.replicateDispatcher);
				}
				this.replicateDispatcher.enqueue(new ReplicateNotification<Value>(oldValue));
			}
		}
		*/
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
//		System.out.println("2) DistributedStackStore-push() [SINGLE] is initialized!");
		this.replicateDispatcher.enqueue(new ReplicateNotification<Value>(v));
		super.pushAtBase(stackKey, v);
	}

//	public void pushAll(PointingReplicateNotification<Value> notification)
	public void pushAll(String stackKey, List<Value> values)
	{
		/*
		if (this.isOverflowed.get())
		{
			List<Value> oldValues = super.popBottom(stackKey, values.size());
			if (oldValues != null)
			{
				if (!this.replicateDispatcher.isReady())
				{
					this.threadPool.execute(this.replicateDispatcher);
				}
				this.replicateDispatcher.enqueue(new ReplicateNotification<Value>(stackKey, oldValues));
			}
		}
		*/
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
//		System.out.println("DistributedStackStore-push() [MULTIPLE] is initialized!");
		this.replicateDispatcher.enqueue(new ReplicateNotification<Value>(stackKey, values));
		super.pushAllAtBase(stackKey, values);
	}

	public void pushAllBottomLocally(String stackKey, List<Value> values)
	{
		super.pushAllBottomAtBase(stackKey, values);
	}
	
	public int getStackSize(String stackKey)
	{
		return super.getStackSizeAtBase(stackKey);
	}
	
	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
		this.isOverflowed.set(true);
//		EvictedNotification<Value> notification = new EvictedNotification<Value>(k, v);
		if (!this.evictedDispatcher.isReady())
		{
			this.threadPool.execute(this.evictedDispatcher);
		}
//		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(k, v));
		
		// The Ehcache assigns the key through the evict(String k, Value v) interface. But it usually does not make sense to the application level. So I attempt the assign the cache key to replace it since the cache key sometimes is used on the application level, e.g., the CircleTimingPageCache. In the case, the cache key is not generated by the information from the Value, i.e., TimingPage. Thus, it is required to keep the cache key in the notification. Usually, the store needs to be updated for that. 08/09/2019, Bing Li

		// The update is useful. In most time, the k is not useful on the application level. However, the cache key makes sense on the application level. 08/09/2019, Bing Li
		this.evictedDispatcher.enqueue(new EvictedNotification<Value>(v.getCacheKey(), v));
		if (super.isEmptyAtBase(v.getCacheKey()))
		{
			super.removeStackAtBase(v.getCacheKey());
		}
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
