package org.greatfree.framework.cps.cache.terminal;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.TerminalList;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.terminal.db.MyUKDB;
import org.greatfree.testing.cache.local.MyUKFactory;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class MyTerminalList
{
	private TerminalList<MyUKValue, MyUKFactory, MyUKDB> list;
	
	private MyTerminalList()
	{
	}
	
	private static MyTerminalList instance = new MyTerminalList();
	
	public static MyTerminalList BACKEND()
	{
		if (instance == null)
		{
			instance = new MyTerminalList();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.list.shutdown();
	}
	
	public void init()
	{
		this.list = new TerminalList.TerminalListBuilder<MyUKValue, MyUKFactory, MyUKDB>()
				.factory(new MyUKFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.TERMINAL_LIST_KEY)
				.cacheSize(TestCacheConfig.TERMINAL_LIST_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TERMINAL_LIST_OFF_HEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TERMINAL_LIST_DISK_SIZE_IN_MB)
				.db(new MyUKDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.TERMINAL_LIST_KEY), TestCacheConfig.TERMINAL_LIST_STORE))
				.alertEvictedCount(TestCacheConfig.TERMINAL_LIST_EVICTED_COUNT)
				.build();
	}

	public void add(MyUKValue v)
	{
		this.list.add(v);
	}
	
	public void addAll(List<MyUKValue> vs)
	{
		this.list.addAll(vs);
	}
	
	public MyUKValue get(int index)
	{
		return this.list.get(index);
	}
	
	public List<MyUKValue> getTop(int endIndex)
	{
		return this.list.getTop(endIndex);
	}
	
	public List<MyUKValue> getRange(int startIndex, int endIndex)
	{
		return this.list.getRange(startIndex, endIndex);
	}
}
