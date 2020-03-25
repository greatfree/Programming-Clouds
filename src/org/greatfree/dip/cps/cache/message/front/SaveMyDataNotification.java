package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/09/2018, Bing Li
public class SaveMyDataNotification extends ServerMessage
{
	private static final long serialVersionUID = 4947807939621977030L;
	
	private MyData data;
	
	private String mapKey;
	private MyStoreData storeData;

	public SaveMyDataNotification(MyData data)
	{
		super(TestCacheMessageType.SAVE_MY_DATA_NOTIFICATION);
		this.data = data;
		this.storeData = null;
	}

	public SaveMyDataNotification(String mapKey, MyStoreData data)
	{
		super(TestCacheMessageType.SAVE_MY_DATA_NOTIFICATION);
		this.mapKey = mapKey;
		this.storeData = data;
		this.data = null;
	}

	public MyData getData()
	{
		return this.data;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public MyStoreData getStoreData()
	{
		return this.storeData;
	}
}
