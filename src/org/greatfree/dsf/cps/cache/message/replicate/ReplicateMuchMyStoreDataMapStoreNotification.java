package org.greatfree.dsf.cps.cache.message.replicate;

import java.util.Map;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class ReplicateMuchMyStoreDataMapStoreNotification extends ServerMessage
{
	private static final long serialVersionUID = -7145858626201441167L;
	
	private String mapKey;
	private Map<String, MyStoreData> data;

	public ReplicateMuchMyStoreDataMapStoreNotification(String mapKey, Map<String, MyStoreData> data)
	{
		super(TestCacheMessageType.REPLICATE_MUCH_MY_STORE_DATA_MAP_STORE_NOTIFICATION);
		this.mapKey = mapKey;
		this.data = data;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public Map<String, MyStoreData> getData()
	{
		return this.data;
	}
}
