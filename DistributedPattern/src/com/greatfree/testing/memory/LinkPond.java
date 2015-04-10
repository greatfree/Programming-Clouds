package com.greatfree.testing.memory;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.greatfree.reuse.ResourceCache;
import com.greatfree.testing.data.CrawledLink;
import com.greatfree.util.Tools;

/*
 * This is a data storage in memory to keep crawled links as one port of the distributed memory system. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class LinkPond
{
	// The cache to save link records. 11/28/2014, Bing Li
	private ResourceCache<LinkRecord> cache;
	
	private LinkPond()
	{
	}

	/*
	 * A singleton implementation. 11/28/2014, Bing Li
	 */
	private static LinkPond instance = new LinkPond();
	
	public static LinkPond STORE()
	{
		if (instance == null)
		{
			instance = new LinkPond();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the cache. 11/28/2014, Bing Li
	 */
	public void dispose()
	{
		this.cache.dispose();
	}

	/*
	 * Initialize the memory cache. 11/28/2014, Bing Li
	 */
	public void init()
	{
		this.cache = new ResourceCache<LinkRecord>(MemConfig.MEMORY_CACHE_SIZE);
	}

	/*
	 * Save the data into the memory. 11/28/2014, Bing Li
	 */
	public void save(CrawledLink link)
	{
		this.cache.addResource(new LinkRecord(link.getKey(), link.getLink(), link.getText(), link.getHubURLKey()));
	}

	/*
	 * Check whether the URL is existed. By the way, for the sample code, it is not an efficient way to retrieve data. 11/29/2014, Bing Li
	 */
	public boolean isPublisherExisted(String url)
	{
		// Get the hash key of the URL. 11/29/2014, Bing Li
		String urlKey = Tools.getHash(url);
		// Get all of the links from the cache. 11/29/2014, Bing Li
		Map<String, LinkRecord> records = this.cache.getResources();
		// Scan each link to compare with the URL key. 11/29/2014, Bing Li
		for (LinkRecord record : records.values())
		{
			// Compared the key of the URL with the link's URL key. 11/29/2014, Bing Li
			if (record.getHubURLKey().equals(urlKey))
			{
				// Return true if matched. 11/29/2014, Bing Li
				return true;
			}
		}
		// Return false if not matched. 11/29/2014, Bing Li
		return false;
	}
	
	/*
	 * Retrieve links whose corresponding texts have the keyword. By the way, for the sample code, it is not an efficient way to retrieve data. 11/29/2014, Bing Li
	 */
	public Set<String> getLinksByKeyword(String keyword)
	{
		// Get all of the links from the cache. 11/29/2014, Bing Li
		Map<String, LinkRecord> records = this.cache.getResources();
		// Initialize a set to take the retrieved linnks. 11/29/2014, Bing Li
		Set<String> links = Sets.newHashSet();
		// Scan each link to compare with the URL key. 11/29/2014, Bing Li
		for (LinkRecord record : records.values())
		{
			// Compared the key of the URL with the link's URL key. 11/29/2014, Bing Li
			if (record.getText().indexOf(keyword) >= 0)
			{
				// Add the link. 11/29/2014, Bing Li
				links.add(record.getLink());
			}
		}
		// Return the set of links. 11/29/2014, Bing Li
		return links;
	}
}
