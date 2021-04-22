package org.greatfree.framework.cps.cache.message.prefetch;

import java.util.List;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/26/2019, Bing Li
public class PrefetchMyUKValuesResponse extends ServerMessage
{
	private static final long serialVersionUID = 7834361931068265699L;
	
	private List<MyUKValue> vs;

	public PrefetchMyUKValuesResponse(List<MyUKValue> vs)
	{
		super(TestCacheMessageType.PREFETCH_MY_UK_VALUES_RESPONSE);
		this.vs = vs;
	}

	public List<MyUKValue> getValues()
	{
		return this.vs;
	}
}
