package org.greatfree.multicast;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.abandoned.BroadcastRequest;
import org.greatfree.message.abandoned.BroadcastResponse;
import org.greatfree.util.HashFreeObject;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

/*
 * This is the implementation to send a broadcast request to all of the nodes in a particular cluster to retrieve data on each of them. It is also required to collect the results and then form a response to return the root. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public abstract class RootRequestBroadcastor<MessagedRequestData extends Serializable, Request extends BroadcastRequest, Response extends BroadcastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, MessagedRequestData>> extends HashFreeObject
{
	// A TCP client pool to interact with remote nodes. 11/28/2014, Bing Li
	private FreeClientPool clientPool;
	// The root node's branch count, which can be understood as the capacity of the root node to send messages concurrently. The root is the broadcast request initial sender. 11/28/2014, Bing Li
	private int rootBranchCount;
	// The other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11//2014, Bing Li
	private int treeBranchCount;
	// The creator to generate broadcast requests. 11/28/2014, Bing Li
	private RequestCreator requestCreator;
	// All of the received responses from each of the node which is retrieved. 11/28/2014, Bing Li
	private Map<String, Response> responseMap;
	// The collaborator that synchronizes to collect the results and determine whether the request is responded sufficiently. 11/28/2014, Bing Li
	private Sync collaborator;
	// The time to wait for responses. If it lasts too long, it might get problems for the request processing. 11/28/2014, Bing Li
	private long waitTime;
	// The count of the total nodes in the broadcast. 11/28/2014, Bing Li
	private int nodeCount;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public RootRequestBroadcastor(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, RequestCreator requestCreator, long waitTime)
	{
		this.clientPool = clientPool;
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
		this.requestCreator = requestCreator;
		this.responseMap = new ConcurrentHashMap<String, Response>();
		this.collaborator = new Sync();
		this.waitTime = waitTime;
	}

	/*
	 * Dispose the broadcast requestor. 11/28/2014, Bing Li
	 */
	public void dispose()
	{
		/*
		this.collaborator.setShutdown();
		this.collaborator.signalAll();
		*/
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();
		this.responseMap.clear();
		this.responseMap = null;
	}

	/*
	 * To reuse the broadcast to take another broadcast request, it must reset. 11/28/2014, Bing Li
	 */
	public synchronized String resetBroadcast()
	{
		// Clear the existing responses. 11/28/2014, Bing Li
		this.responseMap.clear();
		// Reset the collaborator key and return it. The key is critical for the broadcast request to be responded because it determines the procedure of synchronization during the procedure. 11/28/2014, Bing Li
		return this.collaborator.resetKey();
	}

	/*
	 * Save one particular response from the remote node. 11/28/2014, Bing Li
	 */
	public void saveResponse(Response response)
	{
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		if (this.collaborator.getKey().equals(response.getCollaboratorKey()))
		{
			// Put it into the collection. 11/28/2014, Bing Li
			this.responseMap.put(response.getKey(), response);
			// Check whether the count of the collected response exceeds the total node count. 11/28/2014, Bing Li
			if (this.responseMap.size() >= this.nodeCount)
			{
				// If it does, it denotes that the request has been answered by all of the nodes. Notify the waiting collaborator to end the request broadcast. 11/28/2014, Bing Li
				this.collaborator.signal();
			}
		}
	}

	/*
	 * Send the request to the cluster of nodes and return the responses. 11/28/2014, Bing Li
	 */
	public Map<String, Response> disseminate(MessagedRequestData messagedData) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// The initial request to be sent. 11/28/2014, Bing Li
		Request requestToBeSent;
		// Declare a tree to support the high efficient multicasting. 11/28/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/28/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/28/2014, Bing Li
		HashMap<String, String> remoteServerIPs;
		// An integer to keep the new parent node index. 11/28/2014, Bing Li
		int newParentNodeIndex;
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
		this.nodeCount = this.clientPool.getClientSourceSize();
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (this.clientPool.getClientSourceSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.clientPool.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
			// After the tree is constructed, the root only needs to send requests to its immediate children only. The loop does that by getting the root's children from the tree and sending the request one by one. 11/28/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/28/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/28/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/28/2014, Bing Li
					remoteServerIPs = new HashMap<String, String>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/28/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/28/2014, Bing Li
						remoteServerIPs.put(childrenKeyInTree, this.clientPool.getIP(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
					requestToBeSent = this.requestCreator.createInstanceWithChildren(messagedData, this.collaborator.getKey(), remoteServerIPs);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/28/2014, Bing Li
					while (allChildrenKeys.size() > 0)
					{
						try
						{
							// Send the request to the immediate child of the root. 11/28/2014, Bing Li
							this.clientPool.send(childrenKey, requestToBeSent);
							// Jump out the loop after sending the request successfully. 11/28/2014, Bing Li
							break;
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/28/2014, Bing Li
							 */
							// Remove the failed child key. 11/28/2014, Bing Li
							this.clientPool.removeClient(childrenKey);
							// Select one new node from all of the children of the immediate node of the root. 11/28/2014, Bing Li
							newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
							// Get the new selected node key by its index. 11/28/2014, Bing Li
							childrenKey = allChildrenKeys.get(newParentNodeIndex);
							// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
							allChildrenKeys.remove(newParentNodeIndex);
							// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/28/2014, Bing Li
							remoteServerIPs.remove(childrenKey);
							// Reset the updated the children's IPs in the message to be sent. 11/28/2014, Bing Li
							requestToBeSent.setChildrenNodes(remoteServerIPs);
							// The count must be decremented for the failed node. 11/28/2014, Bing Li
							this.nodeCount--;
						}
					}
				}
				else
				{
					/*
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/28/2014, Bing Li
					 */

					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/28/2014, Bing Li
					requestToBeSent = this.requestCreator.createInstanceWithoutChildren(messagedData, this.collaborator.getKey());
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.clientPool.send(childrenKey, requestToBeSent);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.clientPool.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
						this.nodeCount--;
					}
				}
			}
		}
		else
		{
			/*
			 * If the root has sufficient capacity to send the request concurrently, i.e., the root branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/28/2014, Bing Li
			 */

			// Create the request without children's IPs. 11/28/2014, Bing Li
			requestToBeSent = this.requestCreator.createInstanceWithoutChildren(messagedData, this.collaborator.getKey());
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : this.clientPool.getClientKeys())
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.clientPool.send(childClientKey, requestToBeSent);
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.clientPool.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
					this.nodeCount--;
				}
			}
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
		return this.responseMap;
	}
	
	/*
	 * Send the request to the cluster of nodes. Different from the above method, the one specifies the exact node keys which must respond. The above one assumes that all of the nodes in the client pool must respond. 11/28/2014, Bing Li
	 */
	public Map<String, Response> disseminate(Set<String> clientKeys, MessagedRequestData messagedData) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// The initial request to be sent. 11/28/2014, Bing Li
		Request requestToBeSent;
		// Declare a tree to support the high efficient multicasting. 11/28/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/28/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/28/2014, Bing Li
		HashMap<String, String> remoteServerIPs;
		// An integer to keep the new parent node index. 11/28/2014, Bing Li
		int newParentNodeIndex;
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
		this.nodeCount = clientKeys.size();
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (this.nodeCount > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(clientKeys), this.rootBranchCount, this.treeBranchCount);
			// After the tree is constructed, the root only needs to send requests to its immediate children only. The loop does that by getting the root's children from the tree and sending the request one by one. 11/28/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/28/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/28/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/28/2014, Bing Li
					remoteServerIPs = new HashMap<String, String>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/28/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/28/2014, Bing Li
						remoteServerIPs.put(childrenKeyInTree, this.clientPool.getIP(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
					requestToBeSent = this.requestCreator.createInstanceWithChildren(messagedData, this.collaborator.getKey(), remoteServerIPs);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/28/2014, Bing Li
					while (allChildrenKeys.size() > 0)
					{
						try
						{
							// Send the request to the immediate child of the root. 11/28/2014, Bing Li
							this.clientPool.send(childrenKey, requestToBeSent);
							// Jump out the loop after sending the request successfully. 11/28/2014, Bing Li
							break;
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/28/2014, Bing Li
							 */
							// Remove the failed child key. 11/28/2014, Bing Li
							this.clientPool.removeClient(childrenKey);
							// Select one new node from all of the children of the immediate node of the root. 11/28/2014, Bing Li
							newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
							// Get the new selected node key by its index. 11/28/2014, Bing Li
							childrenKey = allChildrenKeys.get(newParentNodeIndex);
							// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
							allChildrenKeys.remove(newParentNodeIndex);
							// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/28/2014, Bing Li
							remoteServerIPs.remove(childrenKey);
							// Reset the updated the children's IPs in the message to be sent. 11/28/2014, Bing Li
							requestToBeSent.setChildrenNodes(remoteServerIPs);
							// The count must be decremented for the failed node. 11/28/2014, Bing Li
							this.nodeCount--;
						}
					}
				}
				else
				{
					/*
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/28/2014, Bing Li
					 */

					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/28/2014, Bing Li
					requestToBeSent = this.requestCreator.createInstanceWithoutChildren(messagedData, this.collaborator.getKey());
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.clientPool.send(childrenKey, requestToBeSent);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.clientPool.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
						this.nodeCount--;
					}
				}
			}
		}
		else
		{
			/*
			 * If the root has sufficient capacity to send the request concurrently, i.e., the root branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/28/2014, Bing Li
			 */

			// Create the request without children's IPs. 11/28/2014, Bing Li
			requestToBeSent = this.requestCreator.createInstanceWithoutChildren(messagedData, this.collaborator.getKey());
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : clientKeys)
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.clientPool.send(childClientKey, requestToBeSent);
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.clientPool.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
					this.nodeCount--;
				}
			}
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
		return this.responseMap;
	}

	/*
	 * Send the request to the cluster of nodes. Different from the above method, the one specifies the exact node keys which must respond. The above one assumes that all of the nodes in the client pool must respond. 11/28/2014, Bing Li
	 */
	public Map<String, Response> disseminate(String clientKey, MessagedRequestData messagedData) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// Create the request without children's IPs. 11/28/2014, Bing Li
		Request requestToBeSent = this.requestCreator.createInstanceWithoutChildren(messagedData, this.collaborator.getKey());
		try
		{
			// Send the request to the immediate node of the root. 11/28/2014, Bing Li
			this.clientPool.send(clientKey, requestToBeSent);
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/28/2014, Bing Li
			this.clientPool.removeClient(clientKey);
			// The count must be decremented for the failed node. 11/28/2014, Bing Li
			this.nodeCount--;
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
		return this.responseMap;
	}
}
