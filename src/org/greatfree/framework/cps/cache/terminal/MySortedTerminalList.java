package org.greatfree.framework.cps.cache.terminal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.SortedTerminalList;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.data.MyPointing;
import org.greatfree.framework.cps.cache.data.MyPointingFactory;
import org.greatfree.framework.cps.cache.terminal.db.MyPointingDB;

// Created: 07/11/2018, Bing Li
public class MySortedTerminalList
{
	private SortedTerminalList<MyPointing, MyPointingFactory, DescendantListPointingComparator<MyPointing>, MyPointingDB> cache;
	
	private AtomicInteger totalCount;
	
	private MySortedTerminalList()
	{
	}
	
	private static MySortedTerminalList instance = new MySortedTerminalList();
	
	public static MySortedTerminalList BACKEND()
	{
		if (instance == null)
		{
			instance = new MySortedTerminalList();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.cache.shutdown();
	}
	
	public void init()
	{
		this.cache = new SortedTerminalList.SortedTerminalListBuilder<MyPointing, MyPointingFactory, DescendantListPointingComparator<MyPointing>, MyPointingDB>()
				.factory(new MyPointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.SORTED_TERMINAL_LIST_CACHE_KEY)
				.cacheSize(TestCacheConfig.SORTED_TERMINAL_LIST_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_TERMINAL_LIST_CACHE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.SORTED_TERMINAL_LIST_CACHE_DISK_SIZE_IN_MB)
				.comp(new DescendantListPointingComparator<MyPointing>())
				.sortSize(TestCacheConfig.SORTED_TERMINAL_LIST_SORT_SIZE)
				.db(new MyPointingDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.SORTED_TERMINAL_LIST_CACHE_KEY), TestCacheConfig.SORTED_TERMINAL_LIST_STORE))
				.alertEvictedCount(TestCacheConfig.POINTING_TERMINAL_LIST_EVICTED_COUNT_ALERT)
				.build();
		
		this.totalCount = new AtomicInteger(0);
	}
	
	public void add(MyPointing pointing)
	{
		System.out.println(this.totalCount.getAndIncrement() + ") MyPointingTerminalList-add(): key = " + pointing.getKey() + ", points = " + pointing.getPoints() + ", description = " + pointing.getDescription());
		this.cache.add(pointing);
	}
	
	public void addAll(List<MyPointing> pointings)
	{
		this.cache.addAll(pointings);
	}
	
	public MyPointing getPointing(String key)
	{
		return this.cache.get(key);
	}
	
	public MyPointing getPointing(int index)
	{
		return this.cache.get(index);
	}
	
	public List<MyPointing> getPointings(int endIndex)
	{
		return this.cache.getTop(endIndex);
	}
	
	public List<MyPointing> getPointings(int startIndex, int endIndex)
	{
		return this.cache.get(startIndex, endIndex);
	}
	
	public MyPointing getMinPointing()
	{
		return this.cache.getMinValue();
	}
}
