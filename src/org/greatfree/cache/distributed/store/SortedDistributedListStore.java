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
import org.greatfree.cache.distributed.PointingReplicateNotification;
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

// Created: 02/25/2018, Bing Li
// public class PrePostfetchablePointingMapCacheStore<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, PrefetchNotification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<PrefetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrefetchNotification, PrefetchThread>, PostfetchRequest extends ListPostfetchRequest, PostfetchResponse extends ListPostfetchResponse<Resource>>
// public class PrePostfetchablePointingMapCacheStore<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, PrefetchNotification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<PrefetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrefetchNotification, PrefetchThread>, PostfetchNotification extends ListPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>>
// public class PointingPPFetchMapStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrePostfetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>>
// public class PointingDistributedCacheStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrePostfetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends PointingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>
//public class PointingDistributedCacheStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends PointingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>
public class SortedDistributedListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> extends SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>
{
//	private PointingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp> store;

	private NotificationObjectDispatcher<PointingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator> replicateDispatcher;

	private NotificationObjectDispatcher<FetchNotification, PrefetchThread, PrefetchThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private final int prefetchCount;
	
	private NotificationObjectDispatcher<FetchNotification, PostfetchThread, PostfetchThreadCreator> postfetchDispatcher;
//	private final int postfetchCount;
//	private final String remoteServerIP;
//	private final int remoteServerPort;
	private final long postfetchTimeout;

	private NotificationObjectDispatcher<EvictedNotification<Value>, EvictThread, EvictThreadCreator> evictedDispatcher;

	private Map<String, List<Value>> postfetchedData;
	private Map<String, Value> postfetchedSingleData;

	private ThreadPool threadPool;
	private Map<String, Sync> syncs;

//	public PrePostfetchablePointingMapCacheStore(PrePostfetchablePointingMapCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, PrefetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchRequest, PostfetchResponse> builder)
//	public PrePostfetchablePointingMapCacheStore(PrePostfetchablePointingMapCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator> builder)
	public SortedDistributedListStore(SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> builder)
	{
//		this.store = new PointingMapStore<Value, Factory, CompoundKeyCreator, DescendantComp>(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator(), builder.getSortSize());

		this.replicateDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<PointingReplicateNotification<Value>, ReplicateThread, ReplicateThreadCreator>()
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
//		this.postfetchCount = builder.getPostfetchCount();
		this.postfetchTimeout = builder.getPostfetchTimeout();
//		this.remoteServerIP = builder.getRemoteServerIP();
//		this.remoteServerPort = builder.getRemoteServerPort();
		
		this.syncs = new ConcurrentHashMap<String, Sync>();
	}
	
//	public static class PrePostfetchablePointingMapCacheStoreBuilder<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, PrefetchNotification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<PrefetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrefetchNotification, PrefetchThread>, PostfetchRequest extends ListPostfetchNotification, PostfetchResponse extends ListPostfetchResponse<Resource>> implements Builder<PrePostfetchablePointingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, PrefetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchRequest, PostfetchResponse>>
//	public static class PrePostfetchablePointingMapCacheStoreBuilder<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, PrefetchNotification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<PrefetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrefetchNotification, PrefetchThread>, PostfetchNotification extends ListPostfetchNotification, PostfetchThread extends NotificationObjectQueue<PostfetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PostfetchNotification, PostfetchThread>> implements Builder<PrePostfetchablePointingMapCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, PrefetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator>>
//	public static class PointingPPFetchMapCacheStoreBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrePostfetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<Notification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<Notification, PostfetchThread>> implements Builder<PointingPPFetchMapStore<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator>>
//	public static class PointingDistributedCacheStoreBuilder<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, PrePostNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<PrePostNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<PrePostNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<PrePostNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<PointingDistributedCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
	public static class SortedDistributedListStoreBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, ReplicateThread extends NotificationObjectQueue<PointingReplicateNotification<Value>>, ReplicateThreadCreator extends NotificationObjectThreadCreatable<PointingReplicateNotification<Value>, ReplicateThread>, FetchNotification extends ListFetchNotification, PrefetchThread extends NotificationObjectQueue<FetchNotification>, PrefetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PrefetchThread>, PostfetchThread extends NotificationObjectQueue<FetchNotification>, PostfetchThreadCreator extends NotificationObjectThreadCreatable<FetchNotification, PostfetchThread>, EvictThread extends NotificationObjectQueue<EvictedNotification<Value>>, EvictThreadCreator extends NotificationObjectThreadCreatable<EvictedNotification<Value>, EvictThread>> implements Builder<SortedDistributedListStore<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>>
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
//		private int postfetchCount;
		private long postfetchTimeout;
//		private String remoteServerIP;
//		private int remoteServerPort;
	
		public SortedDistributedListStoreBuilder()
		{
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> descendantComparator(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		/*
		public PrePostfetchablePointingMapCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator> keepAliveTime(long keepAliveTime)
		{
			this.keepAliveTime = keepAliveTime;
			return this;
		}
		*/
		
		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> replicateThreadCreator(ReplicateThreadCreator replicateCreator)
		{
			this.replicateCreator = replicateCreator;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThreadCreator(PrefetchThreadCreator prefetchCreator)
		{
			this.prefetchCreator = prefetchCreator;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchThreadCreator(PostfetchThreadCreator postfetchCreator)
		{
			this.postfetchCreator = postfetchCreator;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> evictCreator(EvictThreadCreator evictCreator)
		{
			this.evictCreator = evictCreator;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> maxTaskSize(int maxTaskSize)
		{
			this.notificationQueueSize = maxTaskSize;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		/*
		public PointingDistributedCacheStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, PrePostNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchCount(int postfetchCount)
		{
			this.postfetchCount = postfetchCount;
			return this;
		}
		*/

		/*
		public PrePostfetchablePointingMapCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, PrefetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator> remoteServerIP(String remoteServerIP)
		{
			this.remoteServerIP = remoteServerIP;
			return this;
		}

		public PrePostfetchablePointingMapCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, PrefetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchNotification, PostfetchThread, PostfetchThreadCreator> remoteServerPort(int remoteServerPort)
		{
			this.remoteServerPort = remoteServerPort;
			return this;
		}
		*/

		public SortedDistributedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> postfetchTimeout(long postfetchTimeout)
		{
			this.postfetchTimeout = postfetchTimeout;
			return this;
		}

		@Override
		public SortedDistributedListStore<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator> build()
		{
			return new SortedDistributedListStore<Value, Factory, CompoundKeyCreator, DescendantComp, ReplicateThread, ReplicateThreadCreator, FetchNotification, PrefetchThread, PrefetchThreadCreator, PostfetchThread, PostfetchThreadCreator, EvictThread, EvictThreadCreator>(this);
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

		/*
		public int getPostfetchCount()
		{
			return this.postfetchCount;
		}
		*/

		/*
		public String getRemoteServerIP()
		{
			return this.remoteServerIP;
		}
		
		public int getRemoteServerPort()
		{
			return this.remoteServerPort;
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

	public void shutdown() throws InterruptedException, IOException
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
	
	public boolean isDown()
	{
		return super.isDownAtBase();
	}
	
	public boolean isEmpty()
	{
		return super.isEmptyAtBase();
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
	
	private void signal(String key)
	{
		this.syncs.get(key).signal();
		this.syncs.remove(key);
	}

	public void savePostfetchedData(String key, String listKey, List<Value> values, boolean isBlocking)
	{
		/*
		int index = 0;
		for (Value entry : values)
		{
			System.out.println("PointingDistributedCacheStore-PointingDistributedCacheStore(): " + index++ + ") points = " + entry.getPoints());
		}
		*/
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
			super.addAllAtBase(listKey, values);
		}
	}
	
	public void savePostfetchedData(String key, String listKey, Value v, boolean isBlocking)
	{
		if (v != null)
		{
			// The notify/wait mechanism results in the concurrently accessing on the value. It might lead to ConcurrentModificationException. Since only one piece of data is posfetched, it can be saved into the cache first. 08/04/2018, Bing Li
			super.addAtBase(listKey, v);
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
	
	public int getCacheSize(String listKey)
	{
		return super.getSizeAtBase(listKey);
	}

	public Value getValueByKey(FetchNotification notification)
	{
		Value rsc = super.getAtBase(notification.getCacheKey(), notification.getResourceKey());
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
//			PostfetchResponse response = RemoteReader.REMOTE().read(notification.getRemoteServerKey(), this.remoteServerIP, this.remoteServerPort, notification);
//			this.syncs.remove(notification.getKey());
//			return super.get(notification.getCacheKey(), notification.getResourceKey());
			rsc = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return rsc;
		}
		return rsc;
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

	/*
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	public List<Value> getAllTopValuesByCacheKey(FetchNotification notification)
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
//		this.syncs.remove(notification.getKey());
		return super.getList(notification.getCacheKey());
	}
	*/

	/*
	 * The method does not make sense. It is not proper to load all of the data from one cache. 07/22/2018, Bing Li
	 */
	/*
	public List<Value> getValuesByCacheKey(String cacheKey)
	{
		return super.getList(cacheKey);
	}
	*/

	/*
	 * The max value might be evicted to the backend storage server. So postfetching is required. 07/22/2018, Bing Li
	 */
	/*
	public Value getMaxValue(String cacheKey)
	{
		return super.get(cacheKey, 0);
	}
	*/
	
	public Value getMaxValue(FetchNotification notification)
	{
		Value rsc = super.getAtBase(notification.getCacheKey(), 0);
		if (rsc != null)
		{
//			System.out.println("1) PointingDistributedMapStore-getMaxValue(): " + rsc.getCacheKey() + ", " + rsc.getPoints());
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
//			this.syncs.remove(notification.getKey());
//			rsc = super.get(notification.getCacheKey(), 0);
//			System.out.println("2) PointingDistributedMapStore-getMaxValue(): cacheKey = " + notification.getCacheKey());
//			System.out.println("3) PointingDistributedMapStore-getMaxValue(): " + notification.getCacheKey() + ", " + rsc.getPoints());
//			return super.get(notification.getCacheKey(), 0);
			rsc = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return rsc;
		}
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
		Map<String, Value> rscs = super.get(notification.getCacheKey());
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
//		this.syncs.remove(notification.getKey());
		return super.get(notification.getCacheKey());
	}
	*/
	
	/*
	public Value getResourceByIndexWithoutFetch(String cacheKey, int index)
	{
		return super.get(cacheKey, index);
	}
	*/
	
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
//			this.syncs.remove(notification.getKey());
//			return super.get(notification.getCacheKey(), notification.getResourceIndex());
			rsc = this.postfetchedSingleData.get(notification.getKey());
			this.postfetchedSingleData.remove(notification.getKey());
			return rsc;
		}
		return rsc;
	}

	/*
	 * No. It is not necessary to prefetch in the case. But when postfetching, the count should be set when the cache is initialized. 07/23/2018, Bing Li
	 */
	/*
	 * Prefetching is needed for the method? 07/22/2018, Bing Li
	 */
	
	public boolean isListExisted(FetchNotification notification)
	{
		boolean isExisted = super.isListInStoreAtBase(notification.getCacheKey());
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
			return super.isListInStoreAtBase(notification.getCacheKey());
		}
		return isExisted;
	}

	/*
	public boolean isEmpty()
	{
		return this.store.isEmpty();
	}
	*/
	
	public Set<String> getListKeys()
	{
		return super.getListKeysAtBase();
	}
	
	public boolean isCacheEmpty(FetchNotification notification)
	{
		boolean isEmpty = super.isEmptyAtBase(notification.getCacheKey());
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
			return super.isEmptyAtBase(notification.getCacheKey());
		}
		return isEmpty;
	}
	
	/*
	public int getCacheSize(String cacheKey)
	{
		return this.store.getSize(cacheKey);
	}
	*/

	/*
	public int getCacheSize(Notification notification)
	{
		int size = this.store.getSize(notification.getCacheKey());
		if (size != IndependentUtilConfig.NO_COUNT)
		{
			return size;
		}
		if (!this.postfetchDispatcher.isReady())
		{
			this.threadPool.execute(this.postfetchDispatcher);
		}
		this.postfetchDispatcher.enqueue(notification);
		this.syncs.put(notification.getKey(), new Sync(false));
		this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//		this.syncs.remove(notification.getKey());
		return this.store.getSize(notification.getCacheKey());
	}
	*/
	
	/*
	 * I think the exception is required. It is not reasonable to load huge data from the cache. So the range should be located within a certain range. 08/02/2018, Bing Li
	 * It is not necessary to throw the exception. 08/02/2018, Bing Li
	 */
//	public List<Value> getTop(FetchNotification notification)
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
//			System.out.println("1) PointingDistributedCacheStore-getTop(): endIndex = " + notification.getEndIndex());
			List<Value> values = super.getTopAtBase(notification.getCacheKey(), notification.getEndIndex());
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
//				notification.setStartIndex(rscs.size() - 1);
			if (!this.postfetchDispatcher.isReady())
			{
				this.threadPool.execute(this.postfetchDispatcher);
			}
			this.postfetchDispatcher.enqueue(notification);
			if (notification.isBlocking())
			{
				this.syncs.put(notification.getKey(), new Sync(false));
				this.syncs.get(notification.getKey()).holdOn(this.postfetchTimeout);
//					this.syncs.remove(notification.getKey());
//					return super.getTop(notification.getCacheKey(), notification.getEndIndex());
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

	/*
	 * I think the exception is required. It is not reasonable to load huge data from the cache. So the range should be located within a certain range. 08/02/2018, Bing Li
	 * It is not necessary to throw the exception. 08/02/2018, Bing Li
	 */
//	public List<Value> getRange(FetchNotification notification)
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
			List<Value> values = super.getRangeAtBase(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
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
//					this.syncs.remove(notification.getKey());
//					return super.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
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
	
	public List<Value> getTopLocally(String listKey, int endIndex)
	{
		return super.getTopAtBase(listKey, endIndex);
	}
	
	public List<Value> getRangeLocally(String listKey, int startIndex, int endIndex)
	{
		return super.getRangeAtBase(listKey, startIndex, endIndex);
	}
	
	public void put(PointingReplicateNotification<Value> notification)
	{
//		System.out.println("PointingDistributedMapStore-put(): " + notification.getValue().getCacheKey() + ", " + notification.getValue().getKey() + ", " + notification.getValue().getPoints() + " is being saved!");
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
//		System.out.println("PointingDistributedMapStore-put(): " + notification.getValue().getCacheKey() + ", " + notification.getValue().getKey() + ", " + notification.getValue().getPoints() + " is saved!");
		super.addAtBase(notification.getValue().getCacheKey(), notification.getValue());
	}
	
	public void putAll(PointingReplicateNotification<Value> notification)
	{
//		System.out.println("PointingDistributedMapStore-putAll(): " + notification.getCacheKey() + ", " + notification.getValues().size() + " values are being saved!");
		if (!this.replicateDispatcher.isReady())
		{
			this.threadPool.execute(this.replicateDispatcher);
		}
		this.replicateDispatcher.enqueue(notification);
//		System.out.println("PointingDistributedMapStore-putAll(): " + notification.getCacheKey() + ", " + notification.getValues().size() + " values are saved!");
		super.addAllAtBase(notification.getCacheKey(), notification.getValues());
	}
	
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
		 * The removal might cause inconsistency. Meanwhile, since prefetching and postfetching are engaged, it is not necessary to remove the cache key even though it is empty. 07/26/2018, Bing Li
		 */
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
//		System.out.println("PointingDistributedMapStore-evict(): " + notification.getValue().getCacheKey() + ", " + notification.getValue().getKey() + ", " + notification.getValue().getPoints() + " is evicted!");
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
