package org.greatfree.dsf.cps.cache.message.postfetch;

import java.util.Set;

import org.greatfree.cache.distributed.MapPostfetchNotification;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataNotification extends MapPostfetchNotification
{
	public PostfetchMyDataNotification(String resourceKey)
	{
		super(resourceKey, true);
	}

	public PostfetchMyDataNotification(Set<String> resourceKeys)
	{
		super(resourceKeys, true);
	}
	

}
