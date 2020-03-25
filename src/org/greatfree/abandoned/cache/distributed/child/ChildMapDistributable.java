package org.greatfree.abandoned.cache.distributed.child;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.cache.message.BroadValuesRequest;
import org.greatfree.cache.message.ClearNotification;
import org.greatfree.cache.message.CloseNotification;
import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;

/*
 * The interface encloses all of the method for the distributed map on the child of the cluster. 07/14/2017, Bing Li
 */

// Created: 07/14/2017, Bing Li
//public interface ChildMapDistributable<Value extends CacheKey<String>>
public interface ChildMapDistributable<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
{
//	public void open() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException;
	public void open(ChildMapRegistry<Key, Value, Factory, DB> registry) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException;
	public void forward(String key, Value value) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void removeDBKey(String key);
//	public void valueReceived(Key key, Value value);
	public void put(String key, Value value);
	public void putAll(Map<String, Value> values);
	public Value get(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Map<String, Value> get(Set<String> keys) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isExisted(Key key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isEmpty();
	public long getSize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public long getEmptySize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public long getLeftSize(long currentAccessedEndIndex) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isCacheFull() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Set<String> getKeys() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Map<String, Value> getValues() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public void remove(HashSet<String> keys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void remove(Key k) throws InstantiationException, IllegalAccessException, IOException;
	public void clear() throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
//	public void close() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException;
	public void close(ChildMapRegistry<Key, Value, Factory, DB> registry, long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException;
	public String getCacheKey();
	public String getCacheName();
	public void respondToRoot(ServerMessage message) throws IOException, InterruptedException;

	public void broadcastGivenKeysRequest(BroadGetRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastGetSizeRequest(BroadSizeRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastGetAllKeysRequest(BroadKeysRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastGetAllValuesRequest(BroadValuesRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastRemovalKeysNotification(RemoveKeysNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastClearNotification(ClearNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastCloseNotification(CloseNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
}
