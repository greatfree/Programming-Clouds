package org.greatfree.cache.distributed;

import java.util.Set;

import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

// Created: 03/10/2018, Bing Li
public abstract class MapPostfetchNotification
{
	private String mapKey;
	private String key;
	private String resourceKey;
	private Set<String> resourceKeys;

	// Postfetching must result in the low performance. It affects the responsiveness of the client, especially for the UI. So the field indicates whether the hold-on is needed. 07/05/2019, Bing Li
	private boolean isBlocking;
	
	public MapPostfetchNotification(String resourceKey, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.resourceKey = resourceKey;
		this.resourceKeys = null;
		this.isBlocking = isBlocking;
	}

	public MapPostfetchNotification(String mapKey, String resourceKey, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.mapKey = mapKey;
		this.resourceKey = resourceKey;
		this.resourceKeys = null;
		this.isBlocking = isBlocking;
	}

	public MapPostfetchNotification(Set<String> resourceKeys, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.resourceKeys = resourceKeys;
		this.resourceKey = UtilConfig.EMPTY_STRING;
		this.isBlocking = isBlocking;
	}

	public MapPostfetchNotification(String mapKey, Set<String> resourceKeys, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.mapKey = mapKey;
		this.resourceKeys = resourceKeys;
		this.resourceKey = UtilConfig.EMPTY_STRING;
		this.isBlocking = isBlocking;
	}

	/*
	 * The constructor indicates that a minimum value needs to be postfetched. 08/01/2018, Bing Li
	 */
	public MapPostfetchNotification(boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.resourceKeys = null;
		this.resourceKey = UtilConfig.EMPTY_STRING;
		this.isBlocking = isBlocking;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}

	public String getResourceKey()
	{
		return this.resourceKey;
	}

	public Set<String> getResourceKeys()
	{
		return  this.resourceKeys;
	}
	
	public boolean isBlocking()
	{
		return this.isBlocking;
	}
}
