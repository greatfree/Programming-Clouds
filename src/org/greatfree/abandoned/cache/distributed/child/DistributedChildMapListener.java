package org.greatfree.abandoned.cache.distributed.child;

import java.io.IOException;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;

/*
 * This is the listener for the distributed persistable map at the child of a cluster. When data is evicted or expired, it should be removed from the local cache and forward the data to the other node in tits children. But I have NOT decided yet how to handle that. 07/09/2017, Bing Li
 */

// Created: 07/14/2017, Bing Li
public class DistributedChildMapListener<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>, Cache extends ChildMapDistributable<Key, Value, Factory, DB>> implements CacheEventListener<String, Value>
{
	private Cache cache;
	
	public DistributedChildMapListener(Cache cache)
	{
		this.cache = cache;
	}

	@Override
	public void onEvent(CacheEvent<? extends String, ? extends Value> event)
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
