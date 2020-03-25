package org.greatfree.cache;

import java.util.concurrent.ConcurrentSkipListSet;

// Created: 05/22/2017, Bing Li
public interface KeyLoadable<Key>
{
	public ConcurrentSkipListSet<Key> loadKeys();
//	public Set<Key> loadKeys(String cacheKey);
	public void saveKey(Key key);
//	public void saveKey(Key key, String cacheKey);
	public void saveKeys(ConcurrentSkipListSet<Key> keys);
//	public void saveKeys(Set<Key> keys, String cacheKey);
	public void remove(Key key);
//	public void remove(Key key, String cacheKey);
	public void remove(ConcurrentSkipListSet<Key> keys);
//	public void remove(Set<Key> keys, String cacheKey);
	public void removeAll();
	public void dispose();
}
