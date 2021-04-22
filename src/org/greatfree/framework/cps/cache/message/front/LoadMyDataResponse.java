package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.data.MyData;
import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/09/2018, Bing Li
public class LoadMyDataResponse extends ServerMessage
{
	private static final long serialVersionUID = 3020086128406383633L;
	
	private MyData data;
	
	private MyStoreData storeData;

	public LoadMyDataResponse(MyData data)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_RESPONSE);
		this.data = data;
	}

	public LoadMyDataResponse(MyStoreData storeData)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_RESPONSE);
		this.storeData = storeData;
	}

	public MyData getMyData()
	{
		return this.data;
	}
	
	public MyStoreData getMyStoreData()
	{
		return this.storeData;
	}
}
