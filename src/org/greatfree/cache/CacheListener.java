package org.greatfree.cache;

import java.io.Serializable;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.exceptions.TerminalServerOverflowedException;

// Created: 06/28/2017, Bing Li
//public class MapListener<Key, Value extends Serializable, Factory extends PersistableMapFactorable<Key, Value>, DB extends KeyLoadable<Key>> implements CacheEventListener<Key, Value>
public class CacheListener<Key, Value extends Serializable, Cache extends CacheEventable<Key, Value>> implements CacheEventListener<Key, Value>
{
//	private PersistableMap<Key, Value, Factory, DB> map;
	private Cache cache;
	
//	public MapListener(PersistableMap<Key, Value, Factory, DB> map)
	public CacheListener(Cache cache)
	{
//		this.map = map;
		this.cache = cache;		
	}

	@Override
	public void onEvent(CacheEvent<? extends Key, ? extends Value> event)
	{
		if (event.getType() == EventType.CREATED)
		{
//			System.out.println(event.getKey() + ": " + event.getNewValue() + " is created!");
		}
		else if (event.getType() == EventType.EVICTED)
		{
//			System.out.println(event.getKey() + ": " + event.getOldValue() + " is evicted!");
//			this.map.remove(event.getKey());
//			this.cache.forward(event.getOldValue());
//			this.cache.evict(event.getKey());
			
//			AuthorityTiming page = (AuthorityTiming)event.getNewValue();
//			System.out.println("CacheListener-onEvent(): new key = " + page.getKey());
//			System.out.println("CacheListener-onEvent(): " + page.getKey() + ": " + page.getTitle());

			
			try
			{
				this.cache.evict(event.getKey(), event.getOldValue());
			}
			catch (TerminalServerOverflowedException e)
			{
				e.printStackTrace();
			}
		}
		else if (event.getType() == EventType.EXPIRED)
		{
//			System.out.println(event.getKey() + ": " + event.getOldValue() + " is expired!");
//			this.map.remove(event.getKey());
//			this.cache.forward(event.getOldValue());
			this.cache.expire(event.getKey(), event.getOldValue());
		}
		else if (event.getType() == EventType.REMOVED)
		{
//			System.out.println(event.getKey() + ": " + event.getOldValue() + " is removed!");
			this.cache.remove(event.getKey(), event.getOldValue());
		}
		else if (event.getType() == EventType.UPDATED)
		{
//			System.out.println(event.getKey() + ": " + event.getOldValue() + " is updated!");
			this.cache.update(event.getKey(), event.getOldValue());
		}
	}

}
