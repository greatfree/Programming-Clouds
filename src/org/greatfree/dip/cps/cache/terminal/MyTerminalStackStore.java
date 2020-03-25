package org.greatfree.dip.cps.cache.terminal;

import java.util.List;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.TerminalStackStore;
import org.greatfree.cache.factory.StackKeyCreator;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.data.MyStoreDataFactory;
import org.greatfree.dip.cps.cache.terminal.db.MyStoreDataDB;
import org.greatfree.exceptions.IndexOutOfRangeException;

// Created: 08/08/2018, Bing Li
public class MyTerminalStackStore
{
	private TerminalStackStore<MyStoreData, MyStoreDataFactory, StackKeyCreator, MyStoreDataDB> store;
	
	private MyTerminalStackStore()
	{
	}
	
	private static MyTerminalStackStore instance = new MyTerminalStackStore();

	public static MyTerminalStackStore BACKEND()
	{
		if (instance == null)
		{
			instance = new MyTerminalStackStore();
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
		this.store = new TerminalStackStore.TerminalStackStoreBuilder<MyStoreData, MyStoreDataFactory, StackKeyCreator, MyStoreDataDB>()
				.storeKey(TestCacheConfig.TERMINAL_STACK_STORE_KEY)
				.factory(new MyStoreDataFactory())
				.cacheSize(TestCacheConfig.TERMINAL_STACK_STORE_CACHE_SIZE)
				.keyCreator(new StackKeyCreator())
				.rootPath(ServerConfig.CACHE_HOME)
				.totalStoreSize(TestCacheConfig.TERMINAL_STACK_STORE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TERMINAL_STACK_STORE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TERMINAL_STACK_STORE_DISK_SIZE_IN_MB)
				.db(new MyStoreDataDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.TERMINAL_STACK_STORE_KEY), TestCacheConfig.TERMINAL_STACK_STORE))
				.alertEvictedCount(TestCacheConfig.TERMINAL_STACK_STORE_EVICTED_COUNT_ALERT)
				.build();
	}
	
	public void init(String home)
	{
		this.store = new TerminalStackStore.TerminalStackStoreBuilder<MyStoreData, MyStoreDataFactory, StackKeyCreator, MyStoreDataDB>()
				.storeKey(TestCacheConfig.TERMINAL_STACK_STORE_KEY)
				.factory(new MyStoreDataFactory())
				.cacheSize(TestCacheConfig.TERMINAL_STACK_STORE_CACHE_SIZE)
				.keyCreator(new StackKeyCreator())
				.rootPath(home)
				.totalStoreSize(TestCacheConfig.TERMINAL_STACK_STORE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TERMINAL_STACK_STORE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TERMINAL_STACK_STORE_DISK_SIZE_IN_MB)
				.db(new MyStoreDataDB(CacheConfig.getTerminalDBPath(home, TestCacheConfig.TERMINAL_STACK_STORE_KEY), TestCacheConfig.TERMINAL_STACK_STORE))
				.alertEvictedCount(TestCacheConfig.TERMINAL_STACK_STORE_EVICTED_COUNT_ALERT)
				.build();
	}
	
	public void push(MyStoreData value)
	{
		this.store.push(value.getCacheKey(), value);
	}
	
	public void pushAll(String stackKey, List<MyStoreData> values)
	{
		this.store.pushAll(stackKey, values);
	}
	
	public MyStoreData pop(String stackKey)
	{
		return this.store.pop(stackKey);
	}
	
	public List<MyStoreData> popAll(String stackKey, int count)
	{
		return this.store.popAll(stackKey, count);
	}
	
	public MyStoreData peerBottom(String stackKey)
	{
		return this.store.peekBottom(stackKey);
	}
	
	public MyStoreData peek(String stackKey)
	{
		return this.store.peek(stackKey);
	}
	
	public List<MyStoreData> peekAll(String stackKey, int count)
	{
		return this.store.peekAll(stackKey, count);
	}
	
	public List<MyStoreData> peekRange(String stackKey, int startIndex, int endIndex)
	{
		try
		{
			return this.store.peekRange(stackKey, startIndex, endIndex);
		}
		catch (IndexOutOfRangeException e)
		{
			return null;
		}
	}

}
