package org.greatfree.app.search.dip.multicast.child.storage;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.app.search.dip.multicast.message.Page;

// Created: 09/28/2018, Bing Li
public class PageStorage
{
	private Map<String, Page> pages;
	
	private PageStorage()
	{
		this.pages = new ConcurrentHashMap<String, Page>();
	}
	
	private static PageStorage instance = new PageStorage();
	
	public static PageStorage STORAGE()
	{
		if (instance == null)
		{
			instance = new PageStorage();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		this.pages.clear();
		this.pages = null;
	}

	public void save(Page page)
	{
		this.pages.put(page.getKey(), page);
	}
	
//	public List<Page> search(String userKey, String query)
	public ArrayList<Page> search(String userKey, String query)
	{
		ArrayList<Page> results = new ArrayList<Page>();
		for (Page entry : pages.values())
		{
			if (UserPreferences.STORAGE().isInternational(userKey))
			{
				if (entry.isInternational())
				{
					if (entry.getTitle().contains(query))
					{
						results.add(entry);
					}
				}
			}
			else
			{
				if (!entry.isInternational())
				{
					if (entry.getTitle().contains(query))
					{
						results.add(entry);
					}
				}
			}
		}
		return results;
	}

}
