package org.greatfree.multicast;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.abandoned.AnycastRequest;
import org.greatfree.message.abandoned.AnycastResponse;
import org.greatfree.util.HashFreeObject;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

/*
 * This is the implementation to send an anycast request to all of the nodes in a particular cluster to retrieve data on each of them. It is also required to collect the results and then form a response to return the root. However, only one response is good enough. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public abstract class RootRequestAnycastor<MessagedData extends Serializable, Request extends AnycastRequest, Response extends AnycastResponse, RequestCreator extends RootAnycastRequestCreatable<Request, MessagedData>> extends HashFreeObject
{
	// A TCP client pool to interact with remote nodes. 11/29/2014, Bing Li
	private FreeClientPool clientPool;
	// The root node's branch count, which can be understood as the capacity of the root node to send messages concurrently. The root is the broadcast request initial sender. 11/29/2014, Bing Li
	private int rootBranchCount;
	// The other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11/29/2014, Bing Li
	private int treeBranchCount;
	// The creator to generate anycast requests. 11/29/2014, Bing Li
	private RequestCreator requestCreator;
	// The collaborator that synchronizes to collect the results and determine whether the request is responded sufficiently. 11/29/2014, Bing Li
	private Sync collaborator;
	// The time to wait for responses. If it lasts too long, it might get problems for the request processing. 11/29/2014, Bing Li
	private long waitTime;
	// Since one response is enough, only one is declared here. 11/29/2014, Bing Li
	private Response response;
	
	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public RootRequestAnycastor(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, RequestCreator requestCreator, long waitTime)
	{
		this.clientPool = clientPool;
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
		this.requestCreator = requestCreator;
		this.collaborator = new Sync();
		this.waitTime = waitTime;
		this.response = null;
	}
	
	/*
	 * Dispose the broadcast requestor. 11/29/2014, Bing Li
	 */
	public void dispose()
	{
//		this.collaborator.setShutdown();
//		this.collaborator.signalAll();

		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();
	}
	
	/*
	 * To reuse the broadcast to take another anycast request, it must reset. 11/29/2014, Bing Li
	 */
	public synchronized String resetAnycast()
	{
		// Clear the response. 11/29/2014, Bing Li
		this.response = null;
		// Reset the collaborator key and return it. The key is critical for the anycast request to be responded because it determines the procedure of synchronization during the procedure. 11/29/2014, Bing Li
		return this.collaborator.resetKey();
	}

	/*
	 * Save one particular response from the remote node. 11/29/2014, Bing Li
	 */
	public void notify(Response response)
	{
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		if (this.collaborator.getKey().equals(response.getCollaboratorKey()))
		{
			// Assign the response. 11/29/2014, Bing Li
			this.response = response;
			// Notify the waiting collaborator to end the request broadcast. 11/29/2014, Bing Li
			this.collaborator.signal();
		}
	}

	/*
	 * Send the request to the cluster of nodes and return the response. 11/29/2014, Bing Li
	 */
	public Response disseminate(MessagedData messagedData) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// The initial request to be sent. 11/29/2014, Bing Li
		Request requestToBeSent;
		// Declare a tree to support the high efficient multicasting. 11/29/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/29/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/29/2014, Bing Li
		HashMap<String, String> remoteServerIPs;
		// An integer to keep the new parent node index. 11/29/2014, Bing Li
		int newParentNodeIndex;
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/29/2014, Bing Li
		if (this.clientPool.getClientSourceSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/29/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.clientPool.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
			// After the tree is constructed, the root only needs to send requests to its immediate children only. The loop does that by getting the root's children from the tree and sending the request one by one. 11/29/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/29/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/29/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/29/2014, Bing Li
					remoteServerIPs = new HashMap<String, String>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/29/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/29/2014, Bing Li
						remoteServerIPs.put(childrenKeyInTree, this.clientPool.getIP(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/29/2014, Bing Li
					requestToBeSent = this.requestCreator.createInstanceWithChildren(messagedData, this.collaborator.getKey(), remoteServerIPs);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/29/2014, Bing Li
					while (allChildrenKeys.size() > 0)
					{
						try
						{
							// Send the request to the immediate child of the root. 11/29/2014, Bing Li
							this.clientPool.send(childrenKey, requestToBeSent);
							// Jump out the loop after sending the request successfully. 11/29/2014, Bing Li
							break;
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/29/2014, Bing Li
							 */
							// Remove the failed child key. 11/29/2014, Bing Li
							this.clientPool.removeClient(childrenKey);
							// Select one new node from all of the children of the immediate node of the root. 11/29/2014, Bing Li
							newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
							// Get the new selected node key by its index. 11/29/2014, Bing Li
							childrenKey = allChildrenKeys.get(newParentNodeIndex);
							// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
							allChildrenKeys.remove(newParentNodeIndex);
							// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/29/2014, Bing Li
							remoteServerIPs.remove(childrenKey);
							// Reset the updated the children's IPs in the message to be sent. 11/29/2014, Bing Li
							requestToBeSent.setChildrenNodes(remoteServerIPs);
						}
					}
				}
				else
				{
					/*
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/29/2014, Bing Li
					 */

					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/29/2014, Bing Li
					requestToBeSent = this.requestCreator.createInstanceWithoutChildren(messagedData, this.collaborator.getKey());
					try
					{
						// Send the request to the immediate node of the root. 11/29/2014, Bing Li
						this.clientPool.send(childrenKey, requestToBeSent);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/29/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/29/2014, Bing Li
						this.clientPool.removeClient(childrenKey);
					}
				}
			}
		}
		else
		{
			/*
			 * If the root has sufficient capacity to send the request concurrently, i.e., the root branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/29/2014, Bing Li
			 */

			// Create the request without children's IPs. 11/29/2014, Bing Li
			requestToBeSent = this.requestCreator.createInstanceWithoutChildren(messagedData, this.collaborator.getKey());
			// Send the request one by one to the immediate nodes of the root. 11/29/2014, Bing Li
			for (String childClientKey : this.clientPool.getClientKeys())
			{
				try
				{
					// Send the request to the immediate node of the root. 11/29/2014, Bing Li
					this.clientPool.send(childClientKey, requestToBeSent);
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/29/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/29/2014, Bing Li
					this.clientPool.removeClient(childClientKey);
				}
			}
		}
		// The requesting procedure is blocked until at least one response is received or it has waited for sufficiently long time. 11/29/2014, Bing Li
		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/29/2014, Bing Li
		return this.response;
	}
}
