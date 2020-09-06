package org.greatfree.dsf.cps.cache.message.front;

import java.util.Set;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/21/2018, Bing Li
public class LoadMyDataByKeysRequest extends ServerMessage
{
	private static final long serialVersionUID = -8544426391014238280L;
	
	private Set<String> resourceKeys;
	private boolean isPostMap;
	
	private String mapKey;

	public LoadMyDataByKeysRequest(Set<String> resourceKeys)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_BY_KEYS_REQUEST);
		this.resourceKeys = resourceKeys;
		this.isPostMap = false;
	}

	public LoadMyDataByKeysRequest(Set<String> resourceKeys, boolean isPostMap)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_BY_KEYS_REQUEST);
		this.resourceKeys = resourceKeys;
		this.isPostMap = isPostMap;
	}

	public LoadMyDataByKeysRequest(String mapKey, Set<String> resourceKeys)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_BY_KEYS_REQUEST);
		this.mapKey = mapKey;
		this.resourceKeys = resourceKeys;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}

	public Set<String> getResourceKeys()
	{
		return this.resourceKeys;
	}
	
	public boolean isPostMap()
	{
		return this.isPostMap;
	}
}
