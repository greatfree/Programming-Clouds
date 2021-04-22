package org.greatfree.framework.cps.cache.message.postfetch;

import java.util.Set;

import org.greatfree.cache.distributed.MapPostfetchNotification;
import org.greatfree.util.UtilConfig;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataNotification extends MapPostfetchNotification
{
	public PostfetchMyStoreDataNotification(String mapKey, String resourceKey)
	{
		super(mapKey, resourceKey, true);
	}

	public PostfetchMyStoreDataNotification(String mapKey, Set<String> resourceKeys)
	{
		super(mapKey, resourceKeys, true);
	}
	
	public PostfetchMyStoreDataNotification(String mapKey)
	{
		super(mapKey, UtilConfig.NO_KEY, true);
	}

}
