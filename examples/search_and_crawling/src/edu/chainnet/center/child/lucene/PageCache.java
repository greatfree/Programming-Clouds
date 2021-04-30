package edu.chainnet.center.child.lucene;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.cache.local.CacheQueue;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.center.PageIndex;

// Created: 04/28/2021, Bing Li
public final class PageCache
{
	private CacheMap<Page, PageFactory> pages;
	private CacheQueue<String, PageKeyFactory> pageQueue;
	
	private PageCache()
	{
	}

	private static PageCache instance = new PageCache();
	
	public static PageCache CENTER()
	{
		if (instance == null)
		{
			instance = new PageCache();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException
	{
		this.pages.close();
		this.pageQueue.shutdown();
	}

	public void init(String path)
	{
		this.pages = new CacheMap.CacheMapBuilder<Page, PageFactory>()
				.factory(new PageFactory())
				.rootPath(path)
				.cacheKey(CenterConfig.PAGE_MAP)
				.cacheSize(CenterConfig.PAGE_MAP_CACHE_SIZE)
				.offheapSizeInMB(CenterConfig.PAGE_MAP_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(CenterConfig.PAGE_MAP_DISK_SIZE_IN_MB)
				.build();

		this.pageQueue = new CacheQueue.PersistableQueueBuilder<String, PageKeyFactory>()
				.factory(new PageKeyFactory())
				.rootPath(path)
				.queueKey(CenterConfig.PAGE_QUEUE)
				.cacheSize(CenterConfig.PAGE_QUEUE_CACHE_SIZE)
				.offheapSizeInMB(CenterConfig.PAGE_QUEUE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(CenterConfig.PAGE_QUEUE_DISK_SIZE_IN_MB)
				.build();
	}
	
	public void put(Page page)
	{
		this.pages.put(page.getPageKey(), page);
	}

	public Page get(String pageKey)
	{
		return this.pages.get(pageKey);
	}

	public Set<String> getAllKeys()
	{
		this.pages.clear();
		return this.pages.getKeys();
	}
	
	public void enqueue(String key)
	{
		this.pageQueue.enqueue(key);
	}
	
	public String dequeue()
	{
		return this.pageQueue.dequeue();
	}
	
	public boolean isQueueEmpty()
	{
		return this.pageQueue.isEmpty();
	}
	
	public List<PageIndex> getIndexes(List<String> keys)
	{
		if (keys != null)
		{
			List<PageIndex> indexes = new ArrayList<PageIndex>();
			Page page;
			for (String key : keys)
			{
				page = PageCache.CENTER().get(key);
				if (page != null)
				{
					indexes.add(new PageIndex(key, page.getTitle(), page.getURL(), page.getHostHub(), page.getTime()));
				}
			}
			return indexes;
		}
		return CenterConfig.NO_RESULTS;
	}
}

