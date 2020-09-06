package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/28/2019, Bing Li
public class LoadTopMyUKsResponse extends ServerMessage
{
	private static final long serialVersionUID = 1661168462436471782L;
	
	private List<MyUKValue> vs;

	public LoadTopMyUKsResponse(List<MyUKValue> vs)
	{
		super(TestCacheMessageType.LOAD_TOP_MY_UKS_RESPONSE);
		this.vs = vs;
	}

	public List<MyUKValue> getUKs()
	{
		return this.vs;
	}
}
