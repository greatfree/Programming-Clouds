package org.greatfree.app.search.dip.multicast.child.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 10/07/2018, Bing Li
public class UserPreferences
{
	private Map<String, Boolean> isInternationals;
	
	private UserPreferences()
	{
		this.isInternationals = new ConcurrentHashMap<String, Boolean>();
	}
	
	private static UserPreferences instance = new UserPreferences();
	
	public static UserPreferences STORAGE()
	{
		if (instance == null)
		{
			instance = new UserPreferences();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		this.isInternationals.clear();
		this.isInternationals = null;
	}
	
	public void setLocale(String userKey, boolean isInternational)
	{
		this.isInternationals.put(userKey, isInternational);
	}
	

	public boolean isInternational(String userKey)
	{
		return this.isInternationals.get(userKey);
	}
}
