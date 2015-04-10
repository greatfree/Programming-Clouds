package com.greatfree.testing.coordinator.searching;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;
import com.greatfree.reuse.ResourcePool;
import com.greatfree.testing.coordinator.CoorConfig;
import com.greatfree.testing.coordinator.memorizing.MemoryServerClientPool;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.IsPublisherExistedAnycastResponse;
import com.greatfree.testing.message.SearchKeywordBroadcastResponse;

/*
 * This is a wrapper that encloses all of anycast and broadcast readers to accomplish the goal to retrieve data from the cluster of memory nodes. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class CoordinatorMulticastReader
{
	// The collection to save the current working anycast readers. 11/29/2014, Bing Li
	private Map<String, IsPublisherExistedAnycastReader> isPublisherExistedAnycastReaders;
	// The resource pool to manage the instances of anycast readers. 11/29/2014, Bing Li
	private ResourcePool<IsPublisherExistedAnycastReaderSource, IsPublisherExistedAnycastReader, IsPublisherExistedAnycastReaderCreator, IsPublisherExistedAnycastReaderDisposer> isPublisherExistedAnycastReaderPool;
	
	// The collection to save the current working broadcast readers. 11/29/2014, Bing Li
	private Map<String, SearchKeywordBroadcastReader> searchKeywordBroadcastReaders;
	// The resource pool to manage the instances of broadcast readers. 11/29/2014, Bing Li
	private ResourcePool<SearchKeywordBroadcastReaderSource, SearchKeywordBroadcastReader, SearchKeywordBroadcastReaderCreator, SearchKeywordBroadcastReaderDisposer> searchKeywordBroadcastReaderPool;
	
	private CoordinatorMulticastReader()
	{
	}

	/*
	 * A singleton implementation. 11/26/2014, Bing Li
	 */
	private static CoordinatorMulticastReader instance = new CoordinatorMulticastReader();
	
	public static CoordinatorMulticastReader COORDINATE()
	{
		if (instance == null)
		{
			instance = new CoordinatorMulticastReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose all of the pools. 11/26/2014, Bing Li
	 */
	public void dispose()
	{
		// Dispose each anycast reader. 11/29/2014, Bing Li
		for (IsPublisherExistedAnycastReader reader: this.isPublisherExistedAnycastReaders.values())
		{
			reader.dispose();
		}
		this.isPublisherExistedAnycastReaders.clear();
		// Shutdown the anycast reader pool. 11/29/2014, Bing Li
		this.isPublisherExistedAnycastReaderPool.shutdown();
		
		// Dispose each broadcast reader. 11/29/2014, Bing Li
		for (SearchKeywordBroadcastReader reader: this.searchKeywordBroadcastReaders.values())
		{
			reader.dispose();
		}
		this.searchKeywordBroadcastReaders.clear();
		// Shutdown the broadcast reader pool. 11/29/2014, Bing Li
		this.searchKeywordBroadcastReaderPool.shutdown();
	}

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the anycast reader collection. 11/29/2014, Bing Li
		this.isPublisherExistedAnycastReaders = new ConcurrentHashMap<String, IsPublisherExistedAnycastReader>();
		// Initialize the pool to manage the readers. 11/29/2014, Bing Li
		this.isPublisherExistedAnycastReaderPool = new ResourcePool<IsPublisherExistedAnycastReaderSource, IsPublisherExistedAnycastReader, IsPublisherExistedAnycastReaderCreator, IsPublisherExistedAnycastReaderDisposer>(CoorConfig.AUTHORITY_ANYCAST_READER_POOL_SIZE, new IsPublisherExistedAnycastReaderCreator(), new IsPublisherExistedAnycastReaderDisposer(), CoorConfig.AUTHORITY_ANYCAST_READER_POOL_WAIT_TIME);
		
		// Initialize the broadcast reader collection. 11/29/2014, Bing Li
		this.searchKeywordBroadcastReaders = new ConcurrentHashMap<String, SearchKeywordBroadcastReader>();
		this.searchKeywordBroadcastReaderPool = new ResourcePool<SearchKeywordBroadcastReaderSource, SearchKeywordBroadcastReader, SearchKeywordBroadcastReaderCreator, SearchKeywordBroadcastReaderDisposer>(CoorConfig.BROADCAST_READER_POOL_SIZE, new SearchKeywordBroadcastReaderCreator(), new SearchKeywordBroadcastReaderDisposer(), CoorConfig.BROADCAST_READER_POOL_WAIT_TIME);
	}
	
	/*
	 * Retrieve whether the URL is existed by anycast. 11/29/2014, Bing Li
	 */
	public boolean isPublisherExisted(String url)
	{
		try
		{
			// Get one instance of the anycast reader. 11/29/2014, Bing Li
			IsPublisherExistedAnycastReader reader = this.isPublisherExistedAnycastReaderPool.get(new IsPublisherExistedAnycastReaderSource(MemoryServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new IsPublisherExistedAnycastRequestCreator()));
			// Get the collaborator key of the reader. The key is unique to the reader. 11/29/2014, Bing Li
			String collaboratorKey = reader.resetAnycast();
			// Save the instance of the anycast reader. 11/29/2014, Bing Li
			this.isPublisherExistedAnycastReaders.put(collaboratorKey, reader);
			try
			{
				// Anycast the request and wait for the response. 11/29/2014, Bing Li
				IsPublisherExistedAnycastResponse response = reader.disseminate(url);
				// After the response is received, the reader must be collected. 11/29/2014, Bing Li
				this.isPublisherExistedAnycastReaderPool.collect(reader);
				// Remove the reader from the collection. 11/29/2014, Bing Li
				this.isPublisherExistedAnycastReaders.remove(collaboratorKey);
				// Check whether the response is valid. 11/29/2014, Bing Li
				if (response != null)
				{
					// Return the value of the response. 11/29/2014, Bing Li
					return response.isExisted();
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		// Return false in other cases. 11/29/2014, Bing Li
		return false;
	}

	/*
	 * Once if a response to an anycast response is received, it is required to check whether a corresponding collaborator is waiting. If so, just signal the collaborator and the response is what is needed. 11/29/2014, Bing Li
	 */
	public void notifyResponseReceived(IsPublisherExistedAnycastResponse response)
	{
		// Check whether the response has a matched unique corresponding collaborator. 11/29/2014, Bing Li
		if (this.isPublisherExistedAnycastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If it is, notify the collaborator. And the anycast requesting is accomplished. 11/29/2014, Bing Li
			this.isPublisherExistedAnycastReaders.get(response.getCollaboratorKey()).notify(response);
		}
	}
	
	/*
	 * Retrieve keyword for the matched links by broadcast. 11/29/2014, Bing Li
	 */
	public Set<String> searchKeyword(String keyword)
	{
		try
		{
			// Get one instance of the broadcast reader. 11/29/2014, Bing Li
			SearchKeywordBroadcastReader reader = this.searchKeywordBroadcastReaderPool.get(new SearchKeywordBroadcastReaderSource(MemoryServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new SearchKeywordBroadcastRequestCreator()));
			// Get the collaborator key of the reader. The key is unique to the reader. 11/29/2014, Bing Li
			String collaboratorKey = reader.resetBroadcast();
			// Save the instance of the broadcast reader. 11/29/2014, Bing Li
			this.searchKeywordBroadcastReaders.put(collaboratorKey, reader);
			try
			{
				// Broadcast the request and wait for the responses. 11/29/2014, Bing Li
				Map<String, SearchKeywordBroadcastResponse> responses = reader.disseminate(keyword);
				// After the responses are received, the reader must be collected. 11/29/2014, Bing Li
				this.searchKeywordBroadcastReaderPool.collect(reader);
				// Remove the reader from the collection. 11/29/2014, Bing Li
				this.searchKeywordBroadcastReaders.remove(collaboratorKey);
				// Check whether some responses are received. 11/29/2014, Bing Li
				if (responses.size() > 0)
				{
					// Initialize an empty set to take all of the matched links. 11/29/2014, Bing Li
					Set<String> links = Sets.newHashSet();
					// Scan each response to accumulate their results. 11/29/2014, Bing Li
					for (SearchKeywordBroadcastResponse response : responses.values())
					{
						// Check whether the links are valid. 11/29/2014, Bing Li
						if (response.getLinks() != null)
						{
							// Add them together. 11/29/2014, Bing Li
							links.addAll(response.getLinks());
						}
					}
					// Return the retrieved results. 11/29/2014, Bing Li
					return links;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		// Return null if any problems. 11/29/2014, Bing Li
		return null;
	}
	
	/*
	 * Once if a response to a broadcast request is received, it is required to save it in the reader until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
	 */
	public void notifyResponseReceived(SearchKeywordBroadcastResponse response)
	{
		// Check whether the response is the result to one of the reader by comparing the collaborator keys. 11/29/2014, Bing Li
		if (this.searchKeywordBroadcastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If the reader is waiting, save the response. The requesting process is waiting until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
			this.searchKeywordBroadcastReaders.get(response.getCollaboratorKey()).saveResponse(response);
		}
	}
}

