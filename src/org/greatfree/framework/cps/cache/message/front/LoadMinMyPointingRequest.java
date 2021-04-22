package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/20/2018, Bing Li
public class LoadMinMyPointingRequest extends ServerMessage
{
	private static final long serialVersionUID = -1571061312568324287L;

	public LoadMinMyPointingRequest()
	{
		super(TestCacheMessageType.LOAD_MIN_MY_POINTING_REQUEST);
	}

}
