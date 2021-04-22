package org.greatfree.framework.cps.cache.terminal;

import java.util.List;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.TerminalQueueStore;
import org.greatfree.cache.factory.QueueKeyCreator;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.IndexOutOfRangeException;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.data.MyStoreDataFactory;
import org.greatfree.framework.cps.cache.terminal.db.MyStoreDataDB;

// Created: 08/13/2018, Bing Li
public class MyTerminalQueueStore
{
	private TerminalQueueStore<MyStoreData, MyStoreDataFactory, QueueKeyCreator, MyStoreDataDB> store;

	private MyTerminalQueueStore()
	{
	}
	
	private static MyTerminalQueueStore instance = new MyTerminalQueueStore();

	public static MyTerminalQueueStore BACKEND()
	{
		if (instance == null)
		{
			instance = new MyTerminalQueueStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.store.close();
	}

	public void init()
	{
		this.store = new TerminalQueueStore.TerminalQueueStoreBuilder<MyStoreData, MyStoreDataFactory, QueueKeyCreator, MyStoreDataDB>()
				.storeKey(TestCacheConfig.TERMINAL_QUEUE_STORE_KEY)
				.factory(new MyStoreDataFactory())
				.cacheSize(TestCacheConfig.TERMINAL_QUEUE_STORE_CACHE_SIZE)
				.keyCreator(new QueueKeyCreator())
				.rootPath(ServerConfig.CACHE_HOME)
				.totalStoreSize(TestCacheConfig.TERMINAL_QUEUE_STORE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TERMINAL_QUEUE_STORE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TERMINAL_QUEUE_STORE_DISK_SIZE_IN_MB)
				.db(new MyStoreDataDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.TERMINAL_QUEUE_STORE_KEY), TestCacheConfig.TERMINAL_QUEUE_STORE))
				.alertEvictedCount(TestCacheConfig.TERMINAL_QUEUE_STORE_EVICTED_COUNT_ALERT)
				.build();
	}
	
	public void enqueue(MyStoreData value)
	{
		this.store.enqueue(value.getCacheKey(), value);
	}
	
	public void enqueueAll(String queueKey, List<MyStoreData> values)
	{
		this.store.enqueueAll(queueKey, values);
	}
	
	public MyStoreData dequeue(String queueKey)
	{
		return this.store.dequeue(queueKey);
	}
	
	public List<MyStoreData> dequeueAll(String queueKey, int count)
	{
		return this.store.dequeueAll(queueKey, count);
	}
	
	public MyStoreData peek(String queueKey)
	{
		return this.store.peek(queueKey);
	}
	
	public List<MyStoreData> peekAll(String queueKey, int count)
	{
		return this.store.peekAll(queueKey, count);
	}
	
	public List<MyStoreData> peekRange(String queueKey, int startIndex, int endIndex)
	{
		try
		{
			return this.store.peekRange(queueKey, startIndex, endIndex);
		}
		catch (IndexOutOfRangeException e)
		{
			return null;
		}
	}
}
