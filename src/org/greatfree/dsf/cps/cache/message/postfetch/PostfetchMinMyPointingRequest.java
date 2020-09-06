package org.greatfree.dsf.cps.cache.message.postfetch;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/01/2018, Bing Li
public class PostfetchMinMyPointingRequest extends ServerMessage
{
	private static final long serialVersionUID = -5550594561116571282L;

	public PostfetchMinMyPointingRequest()
	{
		super(TestCacheMessageType.POSTFETCH_MIN_MY_POINTING_REQUEST);
	}

}
