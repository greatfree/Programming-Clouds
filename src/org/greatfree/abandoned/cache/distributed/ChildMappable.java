package org.greatfree.abandoned.cache.distributed;

import java.io.Serializable;

// Created: 0728/2017, Bing Li
interface ChildMappable<Key extends Serializable, Value extends CacheKey<Key>>
{
	public Value get(Key key);
	public void put(Key key, Value value);
}
