package org.greatfree.cache.distributed.store;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.distributed.KickOffNotification;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.store.StackStore;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationObjectDispatcher;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * Distributed caches should consider four features;
 * 
 * 1) Replicating;
 * 
 * 2) Prefetching;
 * 
 * 3) Postfetching;
 * 
 * 4) Evicting
 * 
 * However, the cache does not meet the requirements. I will upgrade it when it is employed in the system. Now it is not used. 06/14/2018, Bing Li
 * 
 */

// Created: 02/16/2018, Bing Li
public class StackPrefetchStore<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>>
{
	private StackStore<Value, Factory, CompoundKeyCreator> store;
	private NotificationObjectDispatcher<Notification, PrefetchThread, ThreadCreator> prefetchDispatcher;
	private final int prefetchThresholdSize;
	private ThreadPool threadPool;
	private final int prefetchCount;

	public StackPrefetchStore(PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> builder)
	{
		this.store = new StackStore<Value, Factory, CompoundKeyCreator>(builder.getStoreKey(), builder.getFactory(), builder.getRootPath(), builder.getStoreSize(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), builder.getKeyCreator());
		
		this.prefetchDispatcher = new NotificationObjectDispatcher.NotificationObjectDispatcherBuilder<Notification, PrefetchThread, ThreadCreator>()
				.poolSize(builder.getPoolSize())
				.threadCreator(builder.getThreadCreator())
				.notificationQueueSize(builder.getMaxTaskSize())
				.dispatcherWaitTime(builder.getDispatcherWaitTime())
//				.waitRound(builder.getWaitRound())
				.idleCheckDelay(builder.getIdleCheckDelay())
				.idleCheckPeriod(builder.getIdleCheckPeriod())
				.scheduler(builder.getScheduler())
				.build();
		
		this.prefetchThresholdSize = builder.getPrefetchThresholdSize();
		this.threadPool = builder.getThreadPool();
		this.prefetchCount = builder.getPrefetchCount();
	}
	
	public static class PrefetchableStackStoreBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<Integer>, Notification extends KickOffNotification, PrefetchThread extends NotificationObjectQueue<Notification>, ThreadCreator extends NotificationObjectThreadCreatable<Notification, PrefetchThread>> implements Builder<StackPrefetchStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator>>
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
//		private long keepAliveTime;
		private ThreadCreator creator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
//		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private int prefetchThresholdSize;
		private ThreadPool threadPool;
		private int prefetchCount;
		
		public PrefetchableStackStoreBuilder()
		{
		}
		
		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> storeSize(long storeSize)
		{
			this.storeSize = storeSize;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		/*
		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> keepAliveTime(long keepAliveTime)
		{
			this.keepAliveTime = keepAliveTime;
			return this;
		}
		*/

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> threadCreator(ThreadCreator creator)
		{
			this.creator = creator;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> notificationQueueSize(int notificationQueueSize)
		{
			this.notificationQueueSize = notificationQueueSize;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		/*
		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> prefetchThresholdSize(int prefetchThresholdSize)
		{
			this.prefetchThresholdSize = prefetchThresholdSize;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}

		public PrefetchableStackStoreBuilder<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> prefetchCount(int prefetchCount)
		{
			this.prefetchCount = prefetchCount;
			return this;
		}
		
		@Override
		public StackPrefetchStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator> build()
		{
			return new StackPrefetchStore<Value, Factory, CompoundKeyCreator, Notification, PrefetchThread, ThreadCreator>(this);
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
		
		public int getPrefetchCount()
		{
			return this.prefetchCount;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.store.close();
		this.prefetchDispatcher.dispose();
	}
	
	public boolean isDown()
	{
		return this.store.isDown();
	}
	
	public void push(String stackKey, Value rsc)
	{
		/*
		if (rsc == null)
		{
			System.out.println("PrefetchableStackStore-push(): rsc is NULL");
		}
		if (this.store == null)
		{
			System.out.println("PrefetchableStackStore-push(): store is NULL");
		}
		*/
		this.store.push(stackKey, rsc);
	}

	public void push(String stackKey, List<Value> rscs)
	{
		this.store.pushAll(stackKey, rscs);
	}
	
	public List<Value> pop(Notification notification)
	{
		if (this.store.getSize(notification.getCacheKey()) <= this.prefetchThresholdSize)
		{
			if (!this.prefetchDispatcher.isReady())
			{
				this.threadPool.execute(this.prefetchDispatcher);
			}
			this.prefetchDispatcher.enqueue(notification);
		}
		return this.store.pop(notification.getCacheKey(), notification.getKickOffCount());
	}
	
	public Value pop(String stackKey)
	{
		return this.store.pop(stackKey);
	}
	
	public boolean isExisted(String stackKey)
	{
		return this.store.isStackInStore(stackKey);
	}
	
	public boolean isEmpty()
	{
		return this.store.isEmpty();
	}
	
	public boolean isEmpty(String stackKey)
	{
		return this.store.isEmpty(stackKey);
	}
	
	public int getSize(String stackKey)
	{
		return this.store.getSize(stackKey);
	}
	
	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}
}
