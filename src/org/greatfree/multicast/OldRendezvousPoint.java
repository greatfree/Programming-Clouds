package org.greatfree.multicast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.concurrency.Sync;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastResponse;

// Created: 10/14/2018, Bing Li
public class OldRendezvousPoint
{
	// All of the received responses from each of the node which is retrieved. 11/28/2014, Bing Li
//	private Map<String, RPMulticastResponse> responses;
	private Map<String, List<MulticastResponse>> responses;
	// The collaborator that synchronizes to collect the results and determine whether the request is responded sufficiently. 11/28/2014, Bing Li
//	private Sync collaborator;
	private Map<String, Sync> syncs;
	// The time to wait for responses. If it lasts too long, it might get problems for the request processing. 11/28/2014, Bing Li
	private long waitTime;
	// The count of the total nodes in the broadcast. 11/28/2014, Bing Li
//	private AtomicInteger nodeCount;
	private Map<String, Integer> receiverCounts;

	public OldRendezvousPoint(long waitTime)
	{
//		this.responses = new ConcurrentHashMap<String, RPMulticastResponse>();
		this.responses = new ConcurrentHashMap<String, List<MulticastResponse>>();
//		this.collaborator = new Sync();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.waitTime = waitTime;
//		this.nodeCount = new AtomicInteger(0);
		this.receiverCounts = new ConcurrentHashMap<String, Integer>();
	}
	
	public void dispose()
	{
		// Signal all of the current threads waiting for responses
		for (Sync entry : this.syncs.values())
		{
			entry.signalAll();
		}
		// Clear the collections
		this.syncs.clear();
		this.responses.clear();
		this.receiverCounts.clear();
	}
	
	/*
	 * Save one particular response from the remote node. 11/28/2014, Bing Li
	 */
//	public void saveResponse(RPMulticastResponse response)
	public void saveResponse(MulticastResponse response)
	{
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		if (!this.responses.containsKey(response.getCollaboratorKey()))
		{
//			this.responses.put(response.getCollaboratorKey(), response);
			this.responses.put(response.getCollaboratorKey(), new ArrayList<MulticastResponse>());
		}

		System.out.println("1) RendezvousPoint-saveResponse(): collaborator key = " + response.getCollaboratorKey());

//		this.responses.get(response.getCollaboratorKey()).getResponses().addAll(response.getResponses());
		this.responses.get(response.getCollaboratorKey()).add(response);
//		if (this.responses.get(response.getCollaboratorKey()).getResponses().size() >= this.receiverCounts.get(response.getCollaboratorKey()))
		if (!this.receiverCounts.containsKey(response.getCollaboratorKey()))
		{
			if (this.syncs.containsKey(response.getCollaboratorKey()))
			{
				this.syncs.get(response.getCollaboratorKey()).signal();
			}
		}
		else
		{
			if (this.responses.get(response.getCollaboratorKey()).size() >= this.receiverCounts.get(response.getCollaboratorKey()))
			{
				if (this.syncs.containsKey(response.getCollaboratorKey()))
				{
					this.syncs.get(response.getCollaboratorKey()).signal();
				}
			}
		}
	}
	
	public void saveResponse(RPMulticastResponse response)
	{
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		if (!this.responses.containsKey(response.getCollaboratorKey()))
		{
//			this.responses.put(response.getCollaboratorKey(), response);
			this.responses.put(response.getCollaboratorKey(), new ArrayList<MulticastResponse>());
		}
		
		if (response.getResponses() != null)
		{
			System.out.println("2) RendezvousPoint-saveResponse(): responses size = " + response.getResponses().size());
		}
		else
		{
			System.out.println("2) RendezvousPoint-saveResponse(): responses is NULL");
		}
		
//		this.responses.get(response.getCollaboratorKey()).getResponses().addAll(response.getResponses());
		this.responses.get(response.getCollaboratorKey()).addAll(response.getResponses());
//		if (this.responses.get(response.getCollaboratorKey()).getResponses().size() >= this.receiverCounts.get(response.getCollaboratorKey()))

		System.out.println("2) RendezvousPoint-saveResponse(): collaborator key = " + response.getCollaboratorKey());

		if (this.responses.get(response.getCollaboratorKey()).size() >= this.receiverCounts.get(response.getCollaboratorKey()))
		{
			if (this.syncs.containsKey(response.getCollaboratorKey()))
			{
				this.syncs.get(response.getCollaboratorKey()).signal();
				System.out.println("2.1) RendezvousPoint-saveResponse(): Signal is performed!");
			}
			else
			{
				while (!this.syncs.containsKey(response.getCollaboratorKey()))
				{
					if (this.syncs.containsKey(response.getCollaboratorKey()))
					{
						this.syncs.get(response.getCollaboratorKey()).signal();
						System.out.println("2.2) RendezvousPoint-saveResponse(): Signal is performed!");
					}
				}
			}
		}
	}

	/*
	 * The failed node should be removed from the waiting counts. 08/26/2018, Bing Li
	 */
	public void decrementNode(String collaboratorKey)
	{
		int count = this.receiverCounts.get(collaboratorKey);
		this.receiverCounts.put(collaboratorKey, --count);
	}

	/*
	 * Waiting for responses from the distributed nodes. 08/26/2018, Bing Li
	 */
//	public List<MulticastResponse> waitForResponses(String collaboratorKey)
	public RPMulticastResponse waitForResponses(String collaboratorKey)
	{
		// If the receiverCounts does not contain the collaborator key, it means the node is a leaf such that it does not need to wait for responses. 11/22/2018, Bing Li
		if (this.receiverCounts.containsKey(collaboratorKey))
		{
			if (this.responses.containsKey(collaboratorKey))
			{
				if (this.responses.get(collaboratorKey).size() < this.receiverCounts.get(collaboratorKey))
				{
					// Create an instance of Sync, a wait/notify mechanism, for one multicasting request
					this.syncs.put(collaboratorKey, new Sync(false));
					// Wait for responses from distributed nodes
					System.out.println("---->>>> I am waiting ...");
					this.syncs.get(collaboratorKey).holdOn(this.waitTime);
					System.out.println("---->>>> Waiting is done!");
					// Remove the instance of Sync for the multicasting request
					this.syncs.remove(collaboratorKey);
				}
			}
			else
			{
				// Create an instance of Sync, a wait/notify mechanism, for one multicasting request
				this.syncs.put(collaboratorKey, new Sync(false));
				// Wait for responses from distributed nodes
				System.out.println("---->>>> I am waiting ...");
				this.syncs.get(collaboratorKey).holdOn(this.waitTime);
				System.out.println("---->>>> Waiting is done!");
				// Remove the instance of Sync for the multicasting request
				this.syncs.remove(collaboratorKey);
			}
		}
		// Get the list that keeps the responses from distributed nodes
//		RPMulticastResponse results = this.responses.get(collaboratorKey);
		List<MulticastResponse> results = this.responses.get(collaboratorKey);
		// Remove the list from the map
		this.responses.remove(collaboratorKey);
		this.receiverCounts.remove(collaboratorKey);
		this.syncs.remove(collaboratorKey);
		// Return the list of responses
		if (results != null)
		{
			System.out.println("RendezvousPoint-waitForResponses(): results size = " + results.size());
		}
		else
		{
			System.out.println("RendezvousPoint-waitForResponses(): results are null");
		}
		return new RPMulticastResponse(collaboratorKey, results);
	}
	
	public void addPoint(String collaboratorKey, int count)
	{
		this.receiverCounts.put(collaboratorKey, count);
	}
	
	public int getPointSize(String collaboratorKey)
	{
		return this.receiverCounts.get(collaboratorKey);
	}
}
