package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/28/2019, Bing Li
public class LoadRangeMyUKsResponse extends ServerMessage
{
	private static final long serialVersionUID = 7118073146188585843L;
	
	private List<MyUKValue> uks;

	public LoadRangeMyUKsResponse(List<MyUKValue> uks)
	{
		super(TestCacheMessageType.LOAD_RANGE_MY_UKS_RESPONSE);
		this.uks = uks;
	}

	public List<MyUKValue> getUKs()
	{
		return this.uks;
	}
}
