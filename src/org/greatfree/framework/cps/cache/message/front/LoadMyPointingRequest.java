package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/13/2018, Bing Li
public class LoadMyPointingRequest extends ServerMessage
{
	private static final long serialVersionUID = 6810373726044891059L;
	
	private int index;

	public LoadMyPointingRequest(int index)
	{
		super(TestCacheMessageType.LOAD_MY_POINTING_REQUEST);
		this.index = index;
	}

	public int getIndex()
	{
		return this.index;
	}
}
