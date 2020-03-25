package org.greatfree.dip.cps.cache.message.postfetch;

import java.util.Map;

import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataByKeysResponse extends ServerMessage
{
	private static final long serialVersionUID = 132371807903849014L;
	
	private Map<String, MyData> myData;

	public PostfetchMyDataByKeysResponse(Map<String, MyData> myData)
	{
		super(TestCacheMessageType.POSTFETCH_MY_DATA_BY_KEYS_RESPONSE);
		this.myData = myData;
	}

	public Map<String, MyData> getData()
	{
		return this.myData;
	}
}
