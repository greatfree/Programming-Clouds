package org.greatfree.abandoned.cache.distributed.root;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.exceptions.RemoteReadException;

/*
 * The interface encloses all of the method for the distributed map on the root of the cluster. 07/14/2017, Bing Li
 */

// Created: 07/08/2017, Bing Li
//public interface MapDistributable<Key extends Serializable, Value extends SerializedKey<Key>>
//public interface MapDistributable<Value extends CacheKey<String>>
//public interface MapDistributable<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
public interface MapDistributable<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
{
//	public void open() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException;
//	public void open(MapRegistry<Value, Factory, DB> registry) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException;
	public void open(MapRegistry<Key, Value, Factory, DB> registry) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException;
	public void forward(String key, Value value) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void removeDBKey(String key);
//	public void valueReceived(Key key, Value value);
	public void put(String key, Value value);
	public void putAll(Map<String, Value> values);
//	public Value get(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Value get(Key key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Map<String, Value> get(Set<String> keys) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
//	public boolean isExisted(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isExisted(Key key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isEmpty();
	public long getSize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public long getEmptySize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public long getLeftSize(long currentAccessedEndIndex) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isCacheFull() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Set<String> getKeys() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Map<String, Value> getValues() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public void remove(HashSet<String> keys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void remove(String k) throws InstantiationException, IllegalAccessException, IOException;
	public void clear() throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
//	public void close() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException;
//	public void close(MapRegistry<Value, Factory, DB> registry) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException;
	public void close(MapRegistry<Key, Value, Factory, DB> registry, long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException;
	public String getCacheKey();
	public String getCacheName();
}
