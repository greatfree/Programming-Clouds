package org.greatfree.cache.local;

import java.io.Serializable;

import org.greatfree.exceptions.TerminalServerOverflowedException;

// Created: 06/25/2017, Bing Li
public interface CacheEventable<Key, Value extends Serializable>
{
//	public void evict(Key k);
	public void evict(Key k, Value v) throws TerminalServerOverflowedException;
	public void forward(Key k, Value v);
	public void remove(Key k, Value v);
	public void expire(Key k, Value v);
	public void update(Key k, Value v);
}
