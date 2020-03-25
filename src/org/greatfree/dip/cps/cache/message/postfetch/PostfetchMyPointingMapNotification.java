package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.cache.distributed.MapPostfetchNotification;

// Created: 07/19/2018, Bing Li
public class PostfetchMyPointingMapNotification extends MapPostfetchNotification
{

	public PostfetchMyPointingMapNotification(String resourceKey)
	{
		super(resourceKey, true);
	}

	public PostfetchMyPointingMapNotification()
	{
		super(true);
	}
}
