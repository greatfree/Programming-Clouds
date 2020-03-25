package org.greatfree.abandoned.cache.distributed.root.update;

import java.io.IOException;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.greatfree.abandoned.cache.distributed.CacheValue;

// Created: 07/16/2017, Bing Li
public class DistributedMapListener implements CacheEventListener<String, CacheValue>
{
	private MapDistributable cache;
	
	public DistributedMapListener(MapDistributable cache)
	{
		this.cache = cache;
	}

	@Override
	public void onEvent(CacheEvent<? extends String, ? extends CacheValue> event)
	{
		if (event.getType() == EventType.CREATED)
		{
		}
		// When data is evicted, it should jump to the line. 07/09/2017, Bing Li
		else if (event.getType() == EventType.EVICTED)
		{
			try
			{
				// Remove the data key from the local cache. 07/09/2017, Bing Li
				this.cache.removeDBKey(event.getKey());
				// Forward the data to the other node in the cluster. 07/09/2017, Bing Li
				this.cache.forward(event.getKey(), event.getOldValue());
			}
			catch (InstantiationException | IllegalAccessException | IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		// When data is expired, it should jump to the line. 07/09/2017, Bing Li
		else if (event.getType() == EventType.EXPIRED)
		{
			try
			{
				// Remove the data key from the local cache. 07/09/2017, Bing Li
				this.cache.removeDBKey(event.getKey());
				// Forward the data to the other node in the cluster. 07/09/2017, Bing Li
				this.cache.forward(event.getKey(), event.getOldValue());
			}
			catch (InstantiationException | IllegalAccessException | IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else if (event.getType() == EventType.REMOVED)
		{
		}
		else if (event.getType() == EventType.UPDATED)
		{
		}
	}

}
