package org.greatfree.framework.cps.cache.message.front;

import java.util.Map;

import org.greatfree.framework.cps.cache.data.MyData;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/21/2018, Bing Li
public class LoadMyDataByKeysResponse extends ServerMessage
{
	private static final long serialVersionUID = -7719790853940899125L;
	
	private Map<String, MyData> data;
	
	private Map<String, MyStoreData> storeData;

	public LoadMyDataByKeysResponse(Map<String, MyData> data)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_BY_KEYS_RESPONSE);
		this.data = data;
	}

	public LoadMyDataByKeysResponse(String key, Map<String, MyStoreData> storeData)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_BY_KEYS_RESPONSE);
		this.storeData = storeData;
	}

	public Map<String, MyData> getData()
	{
		return this.data;
	}
	
	public Map<String, MyStoreData> getStoreData()
	{
		return this.storeData;
	}
}
