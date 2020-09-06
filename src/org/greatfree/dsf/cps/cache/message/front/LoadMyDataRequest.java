package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/09/2018, Bing Li
public class LoadMyDataRequest extends ServerMessage
{
	private static final long serialVersionUID = -234417899729344673L;
	
	private String myDataKey;
	
	private boolean isPostMap;
	
	private String mapKey;

	public LoadMyDataRequest(String myDataKey)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_REQUEST);
		this.myDataKey = myDataKey;
		this.isPostMap = false;
	}

	public LoadMyDataRequest(String myDataKey, boolean isPostMap)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_REQUEST);
		this.myDataKey = myDataKey;
		this.isPostMap = isPostMap;
	}

	public LoadMyDataRequest(String mapKey, String myDataKey)
	{
		super(TestCacheMessageType.LOAD_MY_DATA_REQUEST);
		this.mapKey = mapKey;
		this.myDataKey = myDataKey;
	}

	public String getMyDataKey()
	{
		return this.myDataKey;
	}
	
	public boolean isPostMap()
	{
		return this.isPostMap;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}
}
