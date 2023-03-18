package org.greatfree.abandoned.cache.distributed.child.update;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.greatfree.abandoned.cache.distributed.CacheValue;
import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.cache.message.BroadValuesRequest;
import org.greatfree.cache.message.ClearNotification;
import org.greatfree.cache.message.CloseNotification;
import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;

// Created: 07/17/2017, Bing Li
public interface ChildMapDistributable
{
	public void open() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException;
	public void forward(String key, CacheValue value) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void removeDBKey(String key);
//	public void valueReceived(Key key, Value value);
	public void put(String key, CacheValue value);
	public void putAll(Map<String, CacheValue> values);
	public CacheValue get(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Map<String, CacheValue> get(Set<String> keys) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isExisted(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isEmpty();
	public long getSize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public long getEmptySize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public long getLeftSize(long currentAccessedEndIndex) throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public boolean isCacheFull() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Set<String> getKeys() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public Map<String, CacheValue> getValues() throws InstantiationException, IllegalAccessException, InterruptedException, IOException;
	public void remove(HashSet<String> keys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void remove(String k) throws InstantiationException, IllegalAccessException, IOException;
	public void clear() throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void close(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException;
	public String getCacheKey();
	public String getCacheName();

	public void broadcastGivenKeysRequest(BroadGetRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastGetSizeRequest(BroadSizeRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastGetAllKeysRequest(BroadKeysRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastGetAllValuesRequest(BroadValuesRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastRemovalKeysNotification(RemoveKeysNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastClearNotification(ClearNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
	public void broadcastCloseNotification(CloseNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException;
}
