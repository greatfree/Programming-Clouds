package com.greatfree.testing.memory;

import java.io.IOException;

import com.greatfree.reuse.ResourcePool;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The multicastor contains all of the multicastors that send requests or notifications to the local node's children memory nodes by an efficient multicast way. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class MemoryMulticastor
{
	// A resource pool to manage the anycastor. 11/29/2014, Bing Li
	private ResourcePool<IsPublisherExistedChildAnycastorSource, IsPublisherExistedChildAnycastor, IsPublisherExistedChildAnycastorCreator, IsPublisherExistedChildAnycastorDisposer> isPublisherExistedAnycastorPool;

	private ResourcePool<SearchKeywordRequestChildBroadcastorSource, SearchKeywordRequestChildBroadcastor, SearchKeywordRequestChildBroadcastorCreator, SearchKeywordRequestChildBroadcastorDisposer> searchKeywordRequestChildBroadcastorPool;

	private MemoryMulticastor()
	{
	}

	/*
	 * A singleton definition. 11/29/2014, Bing Li
	 */
	private static MemoryMulticastor instance = new MemoryMulticastor();
	
	public static MemoryMulticastor STORE()
	{
		if (instance == null)
		{
			instance = new MemoryMulticastor();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the multicastors. 11/29/2014, Bing Li
	 */
	public void dispose()
	{
		this.isPublisherExistedAnycastorPool.shutdown();
		this.searchKeywordRequestChildBroadcastorPool.shutdown();
	}

	/*
	 * Initialize the multicastors. 11/29/2014, Bing Li
	 */
	public void init()
	{
		this.isPublisherExistedAnycastorPool = new ResourcePool<IsPublisherExistedChildAnycastorSource, IsPublisherExistedChildAnycastor, IsPublisherExistedChildAnycastorCreator, IsPublisherExistedChildAnycastorDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new IsPublisherExistedChildAnycastorCreator(), new IsPublisherExistedChildAnycastorDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
		this.searchKeywordRequestChildBroadcastorPool = new ResourcePool<SearchKeywordRequestChildBroadcastorSource, SearchKeywordRequestChildBroadcastor, SearchKeywordRequestChildBroadcastorCreator, SearchKeywordRequestChildBroadcastorDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new SearchKeywordRequestChildBroadcastorCreator(), new SearchKeywordRequestChildBroadcastorDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
	}

	/*
	 * Disseminate the anycast request to the local node's children. 11/29/2014, Bing Li
	 */
	public void disseminateIsPublisherExistedRequestAmongSubMemServers(IsPublisherExistedAnycastRequest request)
	{
		try
		{
			// Get an instance of the anycastor. 11/29/2014, Bing Li
			IsPublisherExistedChildAnycastor anycastor = this.isPublisherExistedAnycastorPool.get(new IsPublisherExistedChildAnycastorSource(SubClientPool.STORE().getPool(), ServerConfig.MULTICAST_BRANCH_COUNT, ServerConfig.MEMORY_SERVER_PORT, new IsPublisherExistedRequestCreator()));
			// Check whether the anycastor is valid. 11/29/2014, Bing Li
			if (anycastor != null)
			{
				try
				{
					// Disseminate the anycast request to its children. 11/29/2014, Bing Li
					anycastor.disseminate(request);
					// Collect the instance of anycastor after it is sent out. 11/29/2014, Bing Li
					isPublisherExistedAnycastorPool.collect(anycastor);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
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
	}

	/*
	 * Disseminate the broadcast request to the local node's children. 11/29/2014, Bing Li
	 */
	public void disseminateSearchKeywordRequestAmongSubMemServers(SearchKeywordBroadcastRequest request)
	{
		try
		{
			// Get an instance of the broadcastor. 11/29/2014, Bing Li
			SearchKeywordRequestChildBroadcastor broadcastor = this.searchKeywordRequestChildBroadcastorPool.get(new SearchKeywordRequestChildBroadcastorSource(SubClientPool.STORE().getPool(), ServerConfig.MULTICAST_BRANCH_COUNT, ServerConfig.MEMORY_SERVER_PORT, new SearchKeywordBroadcastRequestCreator()));
			// Check whether the broadcastor is valid. 11/29/2014, Bing Li
			if (broadcastor != null)
			{
				try
				{
					// Disseminate the broadcast request to its children. 11/29/2014, Bing Li
					broadcastor.disseminate(request);
					// Collect the instance of broadcastor after it is sent out. 11/29/2014, Bing Li
					this.searchKeywordRequestChildBroadcastorPool.collect(broadcastor);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
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
	}
}
