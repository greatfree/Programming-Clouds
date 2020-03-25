package org.greatfree.dip.cps.cache.message.postfetch;

import java.util.Set;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class PostfetchMuchMyStoreDataRequest extends ServerMessage
{
	private static final long serialVersionUID = 7282687993443257637L;
	
	private String mapKey;
	private Set<String> keys;

	public PostfetchMuchMyStoreDataRequest(String mapKey, Set<String> keys)
	{
		super(TestCacheMessageType.POSTFETCH_MUCH_MY_STORE_DATA_REQUEST);
		this.mapKey = mapKey;
		this.keys = keys;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public Set<String> getKeys()
	{
		return this.keys;
	}
}
