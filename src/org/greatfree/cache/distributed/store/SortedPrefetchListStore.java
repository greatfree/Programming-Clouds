package org.greatfree.cache.distributed.store;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
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
 *
 * Now the cache is not used in the system. But it does not mean the cache is not useful. To use it, it should assume that each cache key exists in any layers of servers. Thus, prefetching can be performed normally without considering the issue of postfetching. In addition, no new data needs to be replicated to the backend server and evicte data is abandoned. At present, no cases fulfill the requirements. 06/13/2018, Bing Li
 */

/*
 * Now the cache is replaced with PointingDistributedCacheStore, which has the postfetching function. When a cache does not exist in the store, it has to perform postfetching. 06/10/2018, Bing Li
 * 
 * For that reason, PointingPrefetchListStore is useful only if it ensures that both the local store and the remote store have any caches which are retrieved. 06/10/2018, Bing Li
 * 
 */

// Created: 06/23/2017, Bing Li
// public class PrefetchablePointingListCacheStore<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PrefetchablePointingListCacheStore<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PointingPrefetchListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PointingPrefetchListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
// public class PointingPrefetchListStore<Value extends CachePointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> extends PointingCacheStore<Value, Factory, CompoundKeyCreator, DescendantComp>
public class SortedPrefetchListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> extends SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>
{
//	private PointingListStore<Value, Factory, CompoundKeyCreator, DescendantComp> store;
	private NotificationObjectDispatcher<Notification, PrefetchThread, ThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
//	private NotificationCreator notificationCreator;
	private ThreadPool threadPool;
	private final int prefetchCount;
	
//	public PrefetchablePointingListCacheStore(PointingListCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> builder)
	public SortedPrefetchListStore(PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> builder)
	{
//		this.store = new PointingListStore<Value, Factory, CompoundKeyCreator, DescendantComp>(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator());
		super(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator(), builder.getComparator(), builder.getSortSize());
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<Notification, PrefetchThread, ThreadCreator>()
				.poolSize(builder.getPoolSize())
//				.keepAliveTime(builder.getKeepAliveTime())
//				.threadPool(builder.getThreadPool())
				.threadCreator(builder.getThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();
		
		this.prefetchThresholdSize = builder.getPrefetchThresholdSize();
//		this.notificationCreator = builder.getNotificationCreator();
		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
	}

//	public static class PointingListCacheBuilder<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchablePointingListCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
//	public static class PointingListCacheBuilder<Resource extends Pointing, Factory extends PersistableMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends PrefetchNotification, NotificationCreator extends PrefetchCacheNotificationCreatable<Notification>, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PrefetchablePointingListCacheStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator>>
//	public static class PointingPrefetchListStoreBuilder<Resource extends CachePointing, Factory extends CacheMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<PointingPrefetchListStore<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator>>
	public static class PointingPrefetchListStoreBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, Notification extends ListPrefetchNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<SortedPrefetchListStore<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator>>
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
		private ThreadCreator creator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private int prefetchThresholdSize;
//		private NotificationCreator notificationCreator;
		private ThreadPool threadPool;
		private int prefetchCount;
	
		public PointingPrefetchListStoreBuilder()
		{
		}

//		public PointingListCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> storeKey(String storeKey)
		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

//		public PointingListCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, NotificationCreator, PrefetchThread, ThreadCreator> factory(Factory factory)
		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> descendantComparator(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> rootPath(String rootPath)
		{
//			this.rootPath = rootPath;
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		/*
		public PrefetchablePointingListCacheStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> keepAliveTime(long keepAliveTime)
		{
			this.keepAliveTime = keepAliveTime;
			return this;
		}
		*/

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> threadCreator(ThreadCreator creator)
		{
			this.creator = creator;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> notificationQueueSize(int notificationQueueSize)
		{
			this.notificationQueueSize = notificationQueueSize;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		/*
		public PointingListCacheBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> notificationCreator(NotificationCreator notificationCreator)
		{
			this.notificationCreator = notificationCreator;
			return this;
		}
		*/

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public PointingPrefetchListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}

		@Override
		public SortedPrefetchListStore<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator> build()
		{
			return new SortedPrefetchListStore<Value, Factory, CompoundKeyCreator, DescendantComp, Notification, PrefetchThread, ThreadCreator>(this);
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
		
		public ThreadCreator getThreadCreator()
		{
			return this.creator;
		}
		
		public int getMaxTaskSize()
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
	public void shutdown() throws InterruptedException, IOException
	{
		super.closeAtBase();
//		this.prefetchDispatcher.dispose(isProcessTerminated);
		this.prefetchDispatcher.dispose();
//		this.threadPool.shutdown();
	}
	
	public boolean isDown()
	{
		return super.isDownAtBase();
	}
	
	public void addLocally(String listKey, Value rsc)
	{
		super.addAtBase(listKey, rsc);
	}
	
	public void addAllLocally(String listKey, List<Value> rscs)
	{
		super.addAllAtBase(listKey, rscs);
	}

//	public List<Resource> get(String listKey, int startIndex, int endIndex, String addressKey)
//	public List<Resource> get(String listKey, int startIndex, int endIndex)
	public List<Value> getRange(Notification notification)
	{
//		if (this.store.getLeftSize(listKey, endIndex) <= this.prefetchThresholdSize)
		if (super.getLeftSizeAtBase(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
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
		return super.getRangeAtBase(notification.getCacheKey(), notification.getStartIndex(), notification.getEndIndex());
	}
	
	public Value get(Notification notification)
	{
//		System.out.println("PointingPrefetchListStore-get(): left size = " + super.getLeftSize(notification.getCacheKey(), notification.getResourceIndex()));
		if (super.getLeftSizeAtBase(notification.getCacheKey(), notification.getResourceIndex()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		return super.getAtBase(notification.getCacheKey(), notification.getResourceIndex());
	}
	
	/* It is not proper to load the entire cache data. 08/03/2018, Bing Li
	public List<Value> get(String listKey)
	{
		return super.get(listKey);
	}
	*/
	
	public boolean isExisted(String listKey)
	{
		return super.isListInStoreAtBase(listKey);
	}

	public boolean isEmpty()
	{
		return super.isEmptyAtBase();
	}
	
	public boolean isEmpty(String listKey)
	{
		return super.isEmptyAtBase(listKey);
	}
	
	public int getSize(String listKey)
	{
		return super.getSizeAtBase(listKey);
	}
	
	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}
	
//	public List<Resource> getTop(String listKey, int endIndex, String addressKey)
//	public List<Resource> getTop(String listKey, int endIndex)
	public List<Value> getTop(Notification notification)
	{
//		if (this.store.getLeftSize(listKey, endIndex) <= this.prefetchThresholdSize)
		if (super.getLeftSizeAtBase(notification.getCacheKey(), notification.getEndIndex()) <= this.prefetchThresholdSize)
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
		return super.getTopAtBase(notification.getCacheKey(), notification.getEndIndex());
	}
	
	public Set<String> getListKeys()
	{
		return super.getListKeysAtBase();
	}

	@Override
	public void evict(String k, Value v)
	{
		// TODO Auto-generated method stub
		
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
