package org.greatfree.testing.cluster.coordinator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.data.ServerConfig;
import org.greatfree.reuse.ResourcePool;
import org.greatfree.testing.cluster.coordinator.dn.AnycastReader;
import org.greatfree.testing.cluster.coordinator.dn.AnycastReaderCreator;
import org.greatfree.testing.cluster.coordinator.dn.AnycastReaderDisposer;
import org.greatfree.testing.cluster.coordinator.dn.AnycastReaderSource;
import org.greatfree.testing.cluster.coordinator.dn.AnycastRequestCreator;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastReader;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastReaderCreator;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastReaderDisposer;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastReaderSource;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastRequestCreator;
import org.greatfree.testing.cluster.coordinator.dn.DNServerClientPool;
import org.greatfree.testing.cluster.coordinator.dn.UnicastReader;
import org.greatfree.testing.cluster.coordinator.dn.UnicastReaderCreator;
import org.greatfree.testing.cluster.coordinator.dn.UnicastReaderDisposer;
import org.greatfree.testing.cluster.coordinator.dn.UnicastReaderSource;
import org.greatfree.testing.cluster.coordinator.dn.UnicastRequestCreator;
import org.greatfree.testing.coordinator.CoorConfig;
import org.greatfree.testing.message.DNAnycastResponse;
import org.greatfree.testing.message.DNBroadcastResponse;
import org.greatfree.testing.message.UnicastResponse;
import org.greatfree.util.UtilConfig;

/*
 * This is a singleton that contains all of the request/response multicastor pools. Those multicastors are critical to compose a cluster for all of the DN servers. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class CoordinatorMulticastReader
{
	// The collection to save the current working broadcast readers. 11/29/2014, Bing Li
	private Map<String, BroadcastReader> broadcastReaders;
	// The resource pool to manage the instances of broadcast readers. 11/29/2014, Bing Li
	private ResourcePool<BroadcastReaderSource, BroadcastReader, BroadcastReaderCreator, BroadcastReaderDisposer> broadcastReaderPool;
	
	// The collection to save the current working unicast readers. 11/29/2014, Bing Li
	private Map<String, UnicastReader> unicastReaders;
	// The resource pool to manage the instances of unicast readers. 11/29/2014, Bing Li
	private ResourcePool<UnicastReaderSource, UnicastReader, UnicastReaderCreator, UnicastReaderDisposer> unicastReaderPool;
	
	// The collection to save the current working anycast readers. 11/29/2014, Bing Li
	private Map<String, AnycastReader> anycastReaders;
	// The resource pool to manage the instances of anycast readers. 11/29/2014, Bing Li
	private ResourcePool<AnycastReaderSource, AnycastReader, AnycastReaderCreator, AnycastReaderDisposer> anycastReaderPool;

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
	public void dispose() throws InterruptedException
	{
		// Dispose each broadcast reader. 11/29/2014, Bing Li
		for (BroadcastReader reader: this.broadcastReaders.values())
		{
			reader.dispose();
		}
		this.broadcastReaders.clear();
		// Shutdown the broadcast reader pool. 11/29/2014, Bing Li
		this.broadcastReaderPool.shutdown();
		
		// Dispose each unicast reader. 11/29/2014, Bing Li
		for (UnicastReader reader : this.unicastReaders.values())
		{
			reader.dispose();
		}
		// Shutdown the unicast reader pool. 11/29/2014, Bing Li
		this.unicastReaderPool.shutdown();
		
		// Dispose each unicast reader. 11/29/2014, Bing Li
		for (AnycastReader reader : this.anycastReaders.values())
		{
			reader.dispose();
		}
		// Shutdown the unicast reader pool. 11/29/2014, Bing Li
		this.anycastReaderPool.shutdown();
	}
	
	public void init()
	{
		// Initialize the broadcast reader collection. 11/29/2014, Bing Li
		this.broadcastReaders = new ConcurrentHashMap<String, BroadcastReader>();
		// Initialize the broadcastor pool. 11/25/2016, Bing Li
		this.broadcastReaderPool = new ResourcePool<BroadcastReaderSource, BroadcastReader, BroadcastReaderCreator, BroadcastReaderDisposer>(CoorConfig.BROADCAST_READER_POOL_SIZE, new BroadcastReaderCreator(), new BroadcastReaderDisposer(), CoorConfig.BROADCAST_READER_POOL_WAIT_TIME);
		
		// Initialize the unicast reader collection. 11/29/2014, Bing Li
		this.unicastReaders = new ConcurrentHashMap<String, UnicastReader>();
		// Initialize the unicastor pool. 11/25/2016, Bing Li
		this.unicastReaderPool = new ResourcePool<UnicastReaderSource, UnicastReader, UnicastReaderCreator, UnicastReaderDisposer>(CoorConfig.BROADCAST_READER_POOL_SIZE, new UnicastReaderCreator(), new UnicastReaderDisposer(), CoorConfig.BROADCAST_READER_POOL_WAIT_TIME);
		
		// Initialize the anycast reader collection. 11/29/2014, Bing Li
		this.anycastReaders = new ConcurrentHashMap<String, AnycastReader>();
		// Initialize the anycastor pool. 11/25/2016, Bing Li
		this.anycastReaderPool = new ResourcePool<AnycastReaderSource, AnycastReader, AnycastReaderCreator, AnycastReaderDisposer>(CoorConfig.BROADCAST_READER_POOL_SIZE, new AnycastReaderCreator(), new AnycastReaderDisposer(), CoorConfig.BROADCAST_READER_POOL_WAIT_TIME);
	}

	/*
	 * Requesting by broadcast. 11/29/2014, Bing Li
	 */
	public String broadcastRequest(String request)
	{
		// Define the result from DNs. 11/25/2016, Bing Li
		String result = UtilConfig.EMPTY_STRING;
		try
		{
			// Get one instance of the broadcast reader. 11/29/2014, Bing Li
			BroadcastReader reader = this.broadcastReaderPool.get(new BroadcastReaderSource(DNServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new BroadcastRequestCreator(), ServerConfig.DISTRIBUTE_DATA_WAIT_TIME));
			// Get the collaborator key of the reader. The key is unique to the reader. 11/29/2014, Bing Li
			String collaboratorKey = reader.resetBroadcast();
			// Save the instance of the broadcast reader. 11/29/2014, Bing Li
			this.broadcastReaders.put(collaboratorKey, reader);
			try
			{
				// Broadcast the request and wait for the responses. 11/29/2014, Bing Li
				Map<String, DNBroadcastResponse> responses = reader.disseminate(request);
				// After the responses are received, the reader must be collected. 11/29/2014, Bing Li
				this.broadcastReaderPool.collect(reader);
				// Remove the reader from the collection. 11/29/2014, Bing Li
				this.broadcastReaders.remove(collaboratorKey);
				// Check whether some responses are received. 11/29/2014, Bing Li
				if (responses.size() > 0)
				{
					// Scan each response to accumulate their results. 11/29/2014, Bing Li
					for (DNBroadcastResponse response : responses.values())
					{
						System.out.println("CoordinatorMulticastReader-broadcastRequest(): response = " + response.getResponse());
						result += response.getResponse();
					}
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
		// Return the result. 11/25/2016, Bing Li
		return result;
	}
	
	/*
	 * Once if a response to a broadcast request is received, it is required to save it in the reader until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
	 */
	public void notifyResponseReceived(DNBroadcastResponse response)
	{
		// Check whether the response is the result to one of the reader by comparing the collaborator keys. 11/29/2014, Bing Li
		if (this.broadcastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If the reader is waiting, save the response. The requesting process is waiting until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
			this.broadcastReaders.get(response.getCollaboratorKey()).saveResponse(response);
		}
	}

	/*
	 * Requesting by unicast. 11/29/2014, Bing Li
	 */
	public String unicastRequest(String request, String dnKey)
	{
		// Define the result from DNs. 11/25/2016, Bing Li
		String result = UtilConfig.EMPTY_STRING;
		try
		{
			// Get one instance of the unicast reader. 11/29/2014, Bing Li
			UnicastReader reader = this.unicastReaderPool.get(new UnicastReaderSource(DNServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new UnicastRequestCreator(), ServerConfig.DISTRIBUTE_DATA_WAIT_TIME));
			// Get the collaborator key of the reader. The key is unique to the reader. 11/29/2014, Bing Li
			String collaboratorKey = reader.resetBroadcast();
			// Save the instance of the unicast reader. 11/29/2014, Bing Li
			this.unicastReaders.put(collaboratorKey, reader);
			try
			{
				// Unicast the request and wait for the responses. 11/29/2014, Bing Li
//				Set<String> dnKeys = Sets.newHashSet();
				Set<String> dnKeys = new HashSet<String>();
				dnKeys.add(dnKey);
				Map<String, UnicastResponse> responses = reader.disseminate(dnKeys, request);
				// After the responses are received, the reader must be collected. 11/29/2014, Bing Li
				this.unicastReaderPool.collect(reader);
				// Remove the reader from the collection. 11/29/2014, Bing Li
				this.unicastReaders.remove(collaboratorKey);
				// Check whether some responses are received. 11/29/2014, Bing Li
				if (responses.size() > 0)
				{
					// Scan each response to accumulate their results. 11/29/2014, Bing Li
					for (UnicastResponse response : responses.values())
					{
						System.out.println("CoordinatorMulticastReader-unicastRequest(): response = " + response.getResponse());
						result += response.getResponse();
					}
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
		// Return the result. 11/25/2016, Bing Li
		return result;
	}
	
	/*
	 * Once if a response to a unicast request is received, it is required to save it in the reader until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
	 */
	public void notifyResponseReceived(UnicastResponse response)
	{
		// Check whether the response is the result to one of the reader by comparing the collaborator keys. 11/29/2014, Bing Li
		if (this.unicastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If the reader is waiting, save the response. The requesting process is waiting until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
			this.unicastReaders.get(response.getCollaboratorKey()).saveResponse(response);
		}
	}

	/*
	 * Requesting by anycast. 11/29/2014, Bing Li
	 */
	public String anycastRequest(String request)
	{
		// Define the result from DNs. 11/25/2016, Bing Li
		String result = UtilConfig.EMPTY_STRING;
		try
		{
			// Get one instance of the anycast reader. 11/29/2014, Bing Li
			AnycastReader reader = this.anycastReaderPool.get(new AnycastReaderSource(DNServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new AnycastRequestCreator(), ServerConfig.DISTRIBUTE_DATA_WAIT_TIME));
			// Get the collaborator key of the reader. The key is unique to the reader. 11/29/2014, Bing Li
			String collaboratorKey = reader.resetBroadcast();
			// Save the instance of the anycast reader. 11/29/2014, Bing Li
			this.anycastReaders.put(collaboratorKey, reader);
			try
			{
				// Broadcast the request and wait for the responses. 11/29/2014, Bing Li
				Map<String, DNAnycastResponse> responses = reader.disseminate(request);
				// After the responses are received, the reader must be collected. 11/29/2014, Bing Li
				this.anycastReaderPool.collect(reader);
				// Remove the reader from the collection. 11/29/2014, Bing Li
				this.anycastReaders.remove(collaboratorKey);
				// Check whether some responses are received. 11/29/2014, Bing Li
				if (responses.size() > 0)
				{
					// Scan each response to accumulate their results. 11/29/2014, Bing Li
					for (DNAnycastResponse response : responses.values())
					{
						System.out.println("CoordinatorMulticastReader-anycastRequest(): response = " + response.getResponse());
						result += response.getResponse();
					}
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
		// Return the result. 11/25/2016, Bing Li
		return result;
	}
	
	/*
	 * Once if a response to an anycast request is received, it is required to save it in the reader until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
	 */
	public void notifyResponseReceived(DNAnycastResponse response)
	{
		// Check whether the response is the result to one of the reader by comparing the collaborator keys. 11/29/2014, Bing Li
		if (this.anycastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If the reader is waiting, save the response. The requesting process is waiting until all of the node respond or the waiting time is over. 11/29/2014, Bing Li
			this.anycastReaders.get(response.getCollaboratorKey()).saveResponse(response);
		}
	}
}
