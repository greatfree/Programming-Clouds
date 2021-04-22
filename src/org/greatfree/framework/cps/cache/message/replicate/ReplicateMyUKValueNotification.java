package org.greatfree.framework.cps.cache.message.replicate;

import java.util.List;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class ReplicateMyUKValueNotification extends ServerMessage
{
	private static final long serialVersionUID = -5813926964763163426L;
	
	private MyUKValue v;
	private List<MyUKValue> vs;

	public ReplicateMyUKValueNotification(MyUKValue v)
	{
		super(TestCacheMessageType.REPLICATE_MY_UK_VALUE_NOTIFICATION);
		this.v = v;
		this.vs = null;
	}
	
	public ReplicateMyUKValueNotification(List<MyUKValue> vs)
	{
		super(TestCacheMessageType.REPLICATE_MY_UK_VALUE_NOTIFICATION);
		this.v = null;
		this.vs = vs;
	}

	public MyUKValue getValue()
	{
		return this.v;
	}
	
	public List<MyUKValue> getValues()
	{
		return this.vs;
	}
}
