package org.greatfree.dip.cps.cache.message.front;

import java.util.Map;

import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/28/2018, Bing Li
public class SaveMuchMyDataNotification extends ServerMessage
{
	private static final long serialVersionUID = -4268446840240046949L;

	private Map<String, MyData> data;
	
	private String mapKey;
	private Map<String, MyStoreData> storeData;

	public SaveMuchMyDataNotification(Map<String, MyData> data)
	{
		super(TestCacheMessageType.SAVE_MUCH_MY_DATA_NOTIFICATION);
		this.data = data;
		this.storeData = null;
	}

	public SaveMuchMyDataNotification(String mapKey, Map<String, MyStoreData> storeData)
	{
		super(TestCacheMessageType.SAVE_MUCH_MY_DATA_NOTIFICATION);
		this.mapKey = mapKey;
		this.storeData = storeData;
		this.data = null;
	}

	public Map<String, MyData> getData()
	{
		return this.data;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public Map<String, MyStoreData> getStoreData()
	{
		return this.storeData;
	}
}
