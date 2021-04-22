package org.greatfree.framework.cps.cache.message.postfetch;

import java.util.List;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class PostfetchMyUKValuesResponse extends ServerMessage
{
	private static final long serialVersionUID = 8950996108466073579L;
	
	private List<MyUKValue> vs;

	public PostfetchMyUKValuesResponse(List<MyUKValue> vs)
	{
		super(TestCacheMessageType.POSTFETCH_MY_UKS_RESPONSE);
		this.vs = vs;
	}

	public List<MyUKValue> getValues()
	{
		return this.vs;
	}
}
