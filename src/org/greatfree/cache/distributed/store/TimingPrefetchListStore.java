package org.greatfree.cache.distributed.store;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.store.TimingListStore;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.exceptions.StoreOverflowException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.Timing;

/*
 * It is replaced by the Pointing-Based caches. 08/22/2018, Bing Li
 */

/*
 * Now the cache is not used in the system. But it does not mean the cache is not useful. To use it, it should assume that each cache key exists in any layers of servers. Thus, prefetching can be performed normally without considering the issue of postfetching. In addition, no new data needs to be replicated to the backend server and evicte data is abandoned. At present, no cases fulfill the requirements. 06/13/2018, Bing Li
 */

// Created: 06/23/2017, Bing Li
// public class PrefetchableTimingListCacheStore<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PrefetchableTimingListCacheStore<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
class TimingPrefetchListStore<Resource extends Timing, Factory extends CacheMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
{
	private TimingListStore<Resource, Factory, CompoundKeyCreator, DescendantComp> store;
	private NotificationObjectDispatcher<Notification, PrefetchThread, ThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
//	private NotificationCreator notificationCreator;
	private ThreadPool threadPool;
	private final int prefetchCount;

//	public PrefetchableTimingListCacheStore(TimingListCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> builder)
	public TimingPrefetchListStore(PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> builder)
	{
//		this.store = new TimingPersistableListStore<Resource, Factory, CompoundKeyCreator, DescendantComp>(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());
		
		this.store = new TimingListStore.TimingPersistableListStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp>()
				.storeKey(builder.getStoreKey())
				.factory(builder.getFactory())
				.rootPath(builder.getRootPath())
				.totalStoreSize(builder.getStoreSize())
				.perCacheSize(builder.getCacheSize())
				.offheapSizeInMB(builder.getOffheapSizeInMB())
				.diskSizeInMB(builder.getDiskSizeInMB())
				.keyCreator(builder.getKeyCreator())
				.comp(builder.getComparator())
				.build();
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<Notification, PrefetchThread, ThreadCreator>()
				.poolSize(builder.getStoreSize())
//				.keepAliveTime(builder.getKeepAliveTime())
//				.threadPool(builder.getThreadPool())
				.threadCreator(builder.getThreadCreator())
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
	}

//	public static class TimingListCacheBuilder<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchableTimingListCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
//	public static class TimingListCacheBuilder<Resource extends Timing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchableTimingListCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
	public static class PrefetchableTimingListCacheStoreBuilder<Resource extends Timing, Factory extends CacheMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<TimingPrefetchListStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator>>
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
		private ThreadCreator creator;
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

		public PrefetchableTimingListCacheStoreBuilder()
		{
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> rootPath(String rootPath)
		{
//			this.rootPath = rootPath;
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> storeSize(int storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> cacheSize(int cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> descendantComparator(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		/*
		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> keepAliveTime(long keepAliveTime)
		{
			this.keepAliveTime = keepAliveTime;
			return this;
		}
		*/

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> threadCreator(ThreadCreator creator)
		{
			this.creator = creator;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> maxTaskSize(int maxTaskSize)
		{
			this.maxTaskSize = maxTaskSize;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		/*
		public TimingListCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> notificationCreator(NotificationCreator notificationCreator)
		{
			this.notificationCreator = notificationCreator;
			return this;
		}
		*/

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public PrefetchableTimingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		/*
		@Override
		public PrefetchableTimingListCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> build()
		{
			return new PrefetchableTimingListCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>(this);
		}
		*/

		@Override
		public TimingPrefetchListStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> build()
		{
			return new TimingPrefetchListStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator>(this);
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
		
		public ThreadCreator getThreadCreator()
		{
			return this.creator;
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
	}

//	public void dispose(boolean isProcessTerminated) throws InterruptedException
	public void dispose() throws InterruptedException
	{
		this.store.close();
//		this.prefetchDispatcher.dispose(isProcessTerminated);
		this.prefetchDispatcher.dispose();
//		this.threadPool.shutdown();
	}
	
	public boolean isDown()
	{
		return this.store.isDown();
	}
	
	public void add(String listKey, Resource rsc) throws StoreOverflowException
	{
		this.store.add(listKey, rsc);
	}
	
	public void add(String listKey, List<Resource> rscs) throws StoreOverflowException
	{
		this.store.add(listKey, rscs);
	}

//	public List<Resource> get(String listKey, int startIndex, int endIndex, String addressKey)
//	public List<Resource> get(String listKey, int startIndex, int endIndex)
	public List<Resource> get(Notification notification)
	{
//		if (this.store.getLeftSize(listKey, endIndex) <= this.prefetchThresholdSize)
		if (this.store.getLeftSize(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(listKey, endIndex + 1, this.prefetchCount + 1));
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(new PrefetchNotification(listKey, endIndex + 1, this.prefetchCount + 1)));
			this.prefetchDispatcher.enqueue(notification);
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(listKey, endIndex + 1, this.prefetchCount + 1, addressKey));
		}
//		return this.store.get(listKey, startIndex, endIndex);
		return this.store.get(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
	}
	
	public Resource get(String listKey, int index)
	{
		return this.store.get(listKey, index);
	}

	public boolean isExisted(String listKey)
	{
		return this.store.isListInStore(listKey);
	}

	public boolean isEmpty()
	{
		return this.store.isEmpty();
	}
	
	public boolean isEmpty(String listKey)
	{
		return this.store.isEmpty(listKey);
	}
	
	public int getSize(String listKey)
	{
		return this.store.getSize(listKey);
	}
	
//	public List<Resource> getTop(String listKey, int endIndex, String addressKey)
//	public List<Resource> getTop(String listKey, int endIndex)
	public List<Resource> getTop(Notification notification)
	{
//		if (this.store.getLeftSize(listKey, endIndex) <= this.prefetchThresholdSize)
		if (this.store.getLeftSize(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(listKey, endIndex + 1, this.prefetchCount + 1));
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(new PrefetchNotification(listKey, endIndex + 1, this.prefetchCount + 1)));
			this.prefetchDispatcher.enqueue(notification);
//			this.prefetchDispatcher.enqueue(this.notificationCreator.createNotificationInstance(listKey, endIndex + 1, this.prefetchCount + 1, addressKey));
		}
//		return this.store.getTop(listKey, endIndex);
		return this.store.getTop(notification.getCacheKey(), notification.getEndIndex());
	}
	
	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}

	/*
	public List<Resource> get(String listKey)
	{
		return this.store.get(listKey);
	}
	*/

	public Set<String> getListKeys()
	{
		return this.store.getListKeys();
	}
}
