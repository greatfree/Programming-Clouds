package org.greatfree.dip.cps.cache.message.front;

import java.util.Set;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class LoadMapStoreDataKeysResponse extends ServerMessage
{
	private static final long serialVersionUID = -6398336510449938455L;
	
	private Set<String> dataKeys;

	public LoadMapStoreDataKeysResponse(Set<String> dataKeys)
	{
		super(TestCacheMessageType.LOAD_MAP_STORE_DATA_KEYS_RESPONSE);
		this.dataKeys = dataKeys;
	}

	public Set<String> getDataKeys()
	{
		return this.dataKeys;
	}
}
