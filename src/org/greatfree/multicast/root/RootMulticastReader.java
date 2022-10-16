package org.greatfree.multicast.root;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.multicast.Tree;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

// Created: 08/23/2018, Bing Li
class RootMulticastReader<Request extends MulticastRequest, Response extends MulticastResponse>
{
	// The TCP client pool that sends requests. 05/04/2017, Bing Li
	private SyncRemoteEventer<Request> reader;
	// The root node's branch count, which can be understood as the capacity of the root node to send messages concurrently. The root is the broadcast request initial sender. 11/28/2014, Bing Li
	private int rootBranchCount;
	// The other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11//2014, Bing Li
	private int treeBranchCount;
	// The creator to generate broadcast requests. 11/28/2014, Bing Li
	// All of the received responses from each of the node which is retrieved. 11/28/2014, Bing Li
	private Map<String, List<Response>> responses;
	// The collaborator that synchronizes to collect the results and determine whether the request is responded sufficiently. 11/28/2014, Bing Li
//	private Sync collaborator;
	private Map<String, Sync> syncs;
	// The time to wait for responses. If it lasts too long, it might get problems for the request processing. 11/28/2014, Bing Li
	private long waitTime;
	// The count of the total nodes in the broadcast. 11/28/2014, Bing Li
//	private AtomicInteger nodeCount;
	private Map<String, Integer> receiverCounts;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public RootMulticastReader(SyncRemoteEventer<Request> reader, int rootBranchCount, int treeBranchCount, long waitTime)
	{
		this.reader = reader;
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
		this.responses = new ConcurrentHashMap<String, List<Response>>();
//		this.collaborator = new Sync();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.waitTime = waitTime;
//		this.nodeCount = new AtomicInteger(0);
		this.receiverCounts = new ConcurrentHashMap<String, Integer>();
	}

	/*
	 * Dispose the broadcast requestor. 11/28/2014, Bing Li
	 */
	public void dispose() throws IOException, ClassNotFoundException
	{
		/*
		this.collaborator.setShutdown();
		this.collaborator.signalAll();
		*/
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
//		this.collaborator.shutdown();
		this.reader.dispose();
		for (Sync entry : this.syncs.values())
		{
			entry.signalAll();
		}
		this.syncs.clear();
		this.responses.clear();
//		this.responseMap = null;
		this.receiverCounts.clear();
	}

	/*
	 * To reuse the broadcast to take another broadcast request, it must reset. 11/28/2014, Bing Li
	 */
	/*
	public synchronized String resetBroadcast()
	{
		// Clear the existing responses. 11/28/2014, Bing Li
		this.responseMap.clear();
		// Reset the collaborator key and return it. The key is critical for the broadcast request to be responded because it determines the procedure of synchronization during the procedure. 11/28/2014, Bing Li
		return this.collaborator.resetKey();
	}
	*/

	/*
	 * Save one particular response from the remote node. 11/28/2014, Bing Li
	 */
	public void saveResponse(Response response)
	{
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		/*
		if (this.collaborator.getKey().equals(response.getCollaboratorKey()))
		{
			// Put it into the collection. 11/28/2014, Bing Li
			this.responseMap.put(response.getKey(), response);
			// Check whether the count of the collected response exceeds the total node count. 11/28/2014, Bing Li
			if (this.responseMap.size() >= this.nodeCount.get())
			{
				// If it does, it denotes that the request has been answered by all of the nodes. Notify the waiting collaborator to end the request broadcast. 11/28/2014, Bing Li
				this.collaborator.signal();
			}
		}
		*/
		if (!this.responses.containsKey(response.getCollaboratorKey()))
		{
			this.responses.put(response.getCollaboratorKey(), new ArrayList<Response>());
		}
		this.responses.get(response.getCollaboratorKey()).add(response);
		if (this.responses.get(response.getCollaboratorKey()).size() >= this.receiverCounts.get(response.getCollaboratorKey()))
		{
			this.syncs.get(response.getCollaboratorKey()).signal();
		}
	}
	
	private void decrementNode(String collaboratorKey)
	{
		int count = this.receiverCounts.get(collaboratorKey);
		this.receiverCounts.put(collaboratorKey, --count);
	}
	
	private List<Response> waitForResponses(String collaboratorKey)
	{
		this.syncs.put(collaboratorKey, new Sync(false));
		this.syncs.get(collaboratorKey).holdOn(this.waitTime);
		this.syncs.remove(collaboratorKey);
		// Return the response collection. 11/28/2014, Bing Li
		List<Response> results = this.responses.get(collaboratorKey);
		this.responses.remove(collaboratorKey);
		return results;
	}

	public List<Response> broadcast(Request request) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// The initial request to be sent. 11/28/2014, Bing Li
//		Request requestToBeSent;
		// Declare a tree to support the high efficient multicasting. 11/28/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/28/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/28/2014, Bing Li
		HashMap<String, IPAddress> remoteServerIPs;
		// An integer to keep the new parent node index. 11/28/2014, Bing Li
		int newParentNodeIndex;
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
//		this.nodeCount.set(this.clientPool.getClientSize());
		this.receiverCounts.put(request.getCollaboratorKey(), this.reader.getClientSize());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (this.reader.getClientSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.reader.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
			// Is sending exception existed. 05/18/2017, Bing Li
			boolean isSendingNormal = true;
			// After the tree is constructed, the root only needs to send requests to its immediate children only. The loop does that by getting the root's children from the tree and sending the request one by one. 11/28/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/28/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/28/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/28/2014, Bing Li
					remoteServerIPs = new HashMap<String, IPAddress>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/28/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/28/2014, Bing Li
						remoteServerIPs.put(childrenKeyInTree, this.reader.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//					request = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, messagedData);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/28/2014, Bing Li
//					while (allChildrenKeys.size() > 0)
					if (allChildrenKeys.size() > 0)
					{
						do
						{
							try
							{
								// Set the sending gets normal. 05/18/2017, Bing Li
								isSendingNormal = true;
								// Send the request to the immediate child of the root. 11/28/2014, Bing Li
								this.reader.notify(childrenKey, request);
								// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//								this.nodeCount.incrementAndGet();
								// Jump out the loop after sending the request successfully. 11/28/2014, Bing Li
								break;
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/28/2014, Bing Li
								 */
								// Remove the failed child key. 11/28/2014, Bing Li
								this.reader.removeClient(childrenKey);
								// Select one new node from all of the children of the immediate node of the root. 11/28/2014, Bing Li
								newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
								// Get the new selected node key by its index. 11/28/2014, Bing Li
								childrenKey = allChildrenKeys.get(newParentNodeIndex);
								// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
								allChildrenKeys.remove(newParentNodeIndex);
								// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/28/2014, Bing Li
								remoteServerIPs.remove(childrenKey);
								// Reset the updated the children's IPs in the message to be sent. 11/28/2014, Bing Li
								request.setChildrenNodes(remoteServerIPs);
								// The count must be decremented for the failed node. 11/28/2014, Bing Li
//								this.nodeCount.decrementAndGet();
								this.decrementNode(request.getCollaboratorKey());
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
							}
						}
						while (!isSendingNormal);
					}
				}
				else
				{
					/*
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/28/2014, Bing Li
					 */

					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/28/2014, Bing Li
//					request = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), messagedData);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.reader.notify(childrenKey, request);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.reader.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
						this.decrementNode(request.getCollaboratorKey());
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
//			request = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), messagedData);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : this.reader.getClientKeys())
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.reader.notify(childClientKey, request);
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.reader.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//					this.nodeCount.decrementAndGet();
					this.decrementNode(request.getCollaboratorKey());
				}
			}
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		return this.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * For a distributed map, data is transmitted to the nearest node in terms of their keys similarity. The method is invoked by a distributed map root to retrieve data from the cluster. 07/10/2017, Bing Li
	 */
	public List<Response> broadcastNearestly(Set<String> dataKeys, Request request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		Set<String> nearestClientKeys = Sets.newHashSet();
		for (String dataKey : dataKeys)
		{
			nearestClientKeys.add(Tools.getClosestKey(dataKey, this.reader.getClientKeys()));
		}
		return this.broadcast(nearestClientKeys, request);
	}

//	public Map<String, Response> disseminate(IPAddress rootAddress, RequestedData messagedData) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	public List<Response> anycast(Request request, int n) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// The initial request to be sent. 11/28/2014, Bing Li
//		Request requestToBeSent;
		// Declare a tree to support the high efficient multicasting. 11/28/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/28/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/28/2014, Bing Li
		HashMap<String, IPAddress> remoteServerIPs;
		// An integer to keep the new parent node index. 11/28/2014, Bing Li
		int newParentNodeIndex;
		// Initialize the child count. For any cast, it is required since not all of children need to receive the message. 05/21/2017, Bing Li
//		this.nodeCount.set(0);
		this.receiverCounts.put(request.getCollaboratorKey(), n);
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
//		this.nodeCount.set(this.clientPool.getClientSize());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (this.reader.getClientSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.reader.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
			// Is sending exception existed. 05/18/2017, Bing Li
			boolean isSendingNormal = true;
			// After the tree is constructed, the root only needs to send requests to its immediate children only. The loop does that by getting the root's children from the tree and sending the request one by one. 11/28/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/28/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/28/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/28/2014, Bing Li
					remoteServerIPs = new HashMap<String, IPAddress>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/28/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/28/2014, Bing Li
						remoteServerIPs.put(childrenKeyInTree, this.reader.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//						requestToBeSent = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, request);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/28/2014, Bing Li
//						while (allChildrenKeys.size() > 0)
					if (allChildrenKeys.size() > 0)
					{
						do
						{
							try
							{
								// Set the sending gets normal. 05/18/2017, Bing Li
								isSendingNormal = true;
								// Send the request to the immediate child of the root. 11/28/2014, Bing Li
								this.reader.notify(childrenKey, request);
								// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//									this.nodeCount.incrementAndGet();
//								this.decrementNode(request.getCollaboratorKey());
								// Jump out the loop after sending the request successfully. 11/28/2014, Bing Li
								break;
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/28/2014, Bing Li
								 */
								// Remove the failed child key. 11/28/2014, Bing Li
								this.reader.removeClient(childrenKey);
								// Select one new node from all of the children of the immediate node of the root. 11/28/2014, Bing Li
								newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
								// Get the new selected node key by its index. 11/28/2014, Bing Li
								childrenKey = allChildrenKeys.get(newParentNodeIndex);
								// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
								allChildrenKeys.remove(newParentNodeIndex);
								// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/28/2014, Bing Li
								remoteServerIPs.remove(childrenKey);
								// Reset the updated the children's IPs in the message to be sent. 11/28/2014, Bing Li
								request.setChildrenNodes(remoteServerIPs);
								// The count must be decremented for the failed node. 11/28/2014, Bing Li
//								this.nodeCount.decrementAndGet();
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
							}
						}
						while (!isSendingNormal);
					}
				}
				else
				{
					/*
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/28/2014, Bing Li
					 */

					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/28/2014, Bing Li
//						requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.reader.notify(childrenKey, request);
						// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//						this.nodeCount.incrementAndGet();
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.reader.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//							this.nodeCount.decrementAndGet();
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
//				requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : this.reader.getClientKeys())
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.reader.notify(childClientKey, request);
					// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//					this.nodeCount.incrementAndGet();
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.reader.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
				}
			}
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
		return this.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Send the request to the cluster of nodes. Different from the above method, the one specifies the exact node keys which must respond. The above one assumes that all of the nodes in the client pool must respond. 11/28/2014, Bing Li
	 */
//	public Map<String, Response> disseminate(IPAddress rootAddress, Set<String> clientKeys, RequestedData messagedData) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	public List<Response> broadcast(Set<String> clientKeys, Request request) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// The initial request to be sent. 11/28/2014, Bing Li
//		Request requestToBeSent;
		// Declare a tree to support the high efficient multicasting. 11/28/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/28/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/28/2014, Bing Li
		HashMap<String, IPAddress> remoteServerIPs;
		// An integer to keep the new parent node index. 11/28/2014, Bing Li
		int newParentNodeIndex;
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
//		this.nodeCount.set(clientKeys.size());
		this.receiverCounts.put(request.getCollaboratorKey(), clientKeys.size());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (this.receiverCounts.get(request.getCollaboratorKey()) > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(clientKeys), this.rootBranchCount, this.treeBranchCount);
			// Is sending exception existed. 05/18/2017, Bing Li
			boolean isSendingNormal = true;
			// After the tree is constructed, the root only needs to send requests to its immediate children only. The loop does that by getting the root's children from the tree and sending the request one by one. 11/28/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/28/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/28/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/28/2014, Bing Li
					remoteServerIPs = new HashMap<String, IPAddress>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/28/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/28/2014, Bing Li
						remoteServerIPs.put(childrenKeyInTree, this.reader.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//					requestToBeSent = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, request);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/28/2014, Bing Li
//					while (allChildrenKeys.size() > 0)
					if (allChildrenKeys.size() > 0)
					{
						do
						{
							try
							{
								// Set the sending gets normal. 05/18/2017, Bing Li
								isSendingNormal = true;
								// Send the request to the immediate child of the root. 11/28/2014, Bing Li
								this.reader.notify(childrenKey, request);
								// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//								this.nodeCount.incrementAndGet();
								// Jump out the loop after sending the request successfully. 11/28/2014, Bing Li
								break;
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/28/2014, Bing Li
								 */
								// Remove the failed child key. 11/28/2014, Bing Li
								this.reader.removeClient(childrenKey);
								// Select one new node from all of the children of the immediate node of the root. 11/28/2014, Bing Li
								newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
								// Get the new selected node key by its index. 11/28/2014, Bing Li
								childrenKey = allChildrenKeys.get(newParentNodeIndex);
								// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
								allChildrenKeys.remove(newParentNodeIndex);
								// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/28/2014, Bing Li
								remoteServerIPs.remove(childrenKey);
								// Reset the updated the children's IPs in the message to be sent. 11/28/2014, Bing Li
								request.setChildrenNodes(remoteServerIPs);
								// The count must be decremented for the failed node. 11/28/2014, Bing Li
//								this.nodeCount.decrementAndGet();
								this.decrementNode(request.getCollaboratorKey());
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
							}
						}
						while (!isSendingNormal);
					}
				}
				else
				{
					/*
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/28/2014, Bing Li
					 */

					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/28/2014, Bing Li
//					requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.reader.notify(childrenKey, request);
						// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//						this.nodeCount.incrementAndGet();
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.reader.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
						this.decrementNode(request.getCollaboratorKey());
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
//			requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : clientKeys)
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.reader.notify(childClientKey, request);
					// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//					this.nodeCount.incrementAndGet();
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.reader.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//					this.nodeCount.decrementAndGet();
					this.decrementNode(request.getCollaboratorKey());
				}
			}
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
		return this.waitForResponses(request.getCollaboratorKey());
	}

	public List<Response> anycast(Set<String> clientKeys, Request request, int n) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
		// The initial request to be sent. 11/28/2014, Bing Li
//		Request requestToBeSent;
		// Declare a tree to support the high efficient multicasting. 11/28/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/28/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/28/2014, Bing Li
		HashMap<String, IPAddress> remoteServerIPs;
		// An integer to keep the new parent node index. 11/28/2014, Bing Li
		int newParentNodeIndex;
		// Initialize the child count. For any cast, it is required since not all of children need to receive the message. 05/21/2017, Bing Li
//		this.nodeCount.set(0);
		if (clientKeys.size() > n)
		{
			this.receiverCounts.put(request.getCollaboratorKey(), n);
		}
		else
		{
			this.receiverCounts.put(request.getCollaboratorKey(), clientKeys.size());
		}
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
//		this.nodeCount.set(clientKeys.size());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (clientKeys.size() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(clientKeys), this.rootBranchCount, this.treeBranchCount);
			// Is sending exception existed. 05/18/2017, Bing Li
			boolean isSendingNormal = true;
			// After the tree is constructed, the root only needs to send requests to its immediate children only. The loop does that by getting the root's children from the tree and sending the request one by one. 11/28/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/28/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/28/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/28/2014, Bing Li
					remoteServerIPs = new HashMap<String, IPAddress>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/28/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/28/2014, Bing Li
						remoteServerIPs.put(childrenKeyInTree, this.reader.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//						requestToBeSent = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, messagedData);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/28/2014, Bing Li
//						while (allChildrenKeys.size() > 0)
					if (allChildrenKeys.size() > 0)
					{
						do
						{
							try
							{
								// Set the sending gets normal. 05/18/2017, Bing Li
								isSendingNormal = true;
								// Send the request to the immediate child of the root. 11/28/2014, Bing Li
								this.reader.notify(childrenKey, request);
								// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//								this.nodeCount.incrementAndGet();
								// Jump out the loop after sending the request successfully. 11/28/2014, Bing Li
								break;
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/28/2014, Bing Li
								 */
								// Remove the failed child key. 11/28/2014, Bing Li
								clientKeys.remove(childrenKey);
								// Remove the failed child key. 11/28/2014, Bing Li
								this.reader.removeClient(childrenKey);
								// Select one new node from all of the children of the immediate node of the root. 11/28/2014, Bing Li
								newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
								// Get the new selected node key by its index. 11/28/2014, Bing Li
								childrenKey = allChildrenKeys.get(newParentNodeIndex);
								// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
								allChildrenKeys.remove(newParentNodeIndex);
								// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/28/2014, Bing Li
								remoteServerIPs.remove(childrenKey);
								// Reset the updated the children's IPs in the message to be sent. 11/28/2014, Bing Li
								request.setChildrenNodes(remoteServerIPs);
								// The count must be decremented for the failed node. 11/28/2014, Bing Li
//									this.nodeCount.decrementAndGet();
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
							}
						}
						while (!isSendingNormal);
					}
				}
				else
				{
					/*
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/28/2014, Bing Li
					 */

					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/28/2014, Bing Li
//						requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), messagedData);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.reader.notify(childrenKey, request);
						// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//						this.nodeCount.incrementAndGet();
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						// Remove the failed child key. 11/28/2014, Bing Li
						clientKeys.remove(childrenKey);
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.reader.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//							this.nodeCount.decrementAndGet();
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
//				requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), messagedData);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : clientKeys)
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.reader.notify(childClientKey, request);
					// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//					this.nodeCount.incrementAndGet();
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the failed child key. 11/28/2014, Bing Li
					clientKeys.remove(childClientKey);
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.reader.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
				}
			}
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
		return this.waitForResponses(request.getCollaboratorKey());
	}
	
	/*
	 * Send the request to the specified node in the cluster. 11/28/2014, Bing Li
	 */
//	public Map<String, Response> disseminate(IPAddress rootAddress, String clientKey, RequestedData messagedData) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	public List<Response> unicast(String clientKey, Request request) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
//		this.nodeCount.set(0);
		this.receiverCounts.put(request.getCollaboratorKey(), 1);
		// Create the request without children's IPs. 11/28/2014, Bing Li
//		Request requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
		try
		{
			// Send the request to the immediate node of the root. 11/28/2014, Bing Li
			this.reader.notify(clientKey, request);
			// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//			this.nodeCount.incrementAndGet();
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/28/2014, Bing Li
			this.reader.removeClient(clientKey);
			// The count must be decremented for the failed node. 11/28/2014, Bing Li
//			this.nodeCount.decrementAndGet();
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
		return this.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Send the request to the randomly selected node in the cluster. 11/28/2014, Bing Li
	 */
	public List<Response> unicast(Request request) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
//		this.nodeCount.set(0);
		this.receiverCounts.put(request.getCollaboratorKey(), 1);
		String randomKey = Tools.getRandomSetElement(this.reader.getClientKeys());
		// Create the request without children's IPs. 11/28/2014, Bing Li
//		Request requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
		try
		{
			// Send the request to the immediate node of the root. 11/28/2014, Bing Li
			this.reader.notify(randomKey, request);
			// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//			this.nodeCount.incrementAndGet();
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/28/2014, Bing Li
			this.reader.removeClient(randomKey);
			// The count must be decremented for the failed node. 11/28/2014, Bing Li
//			this.nodeCount.decrementAndGet();
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
		return this.waitForResponses(request.getCollaboratorKey());
	}

	/*
	 * Send the request to the nearest node in the cluster. 11/28/2014, Bing Li
	 */
	public List<Response> unicastNearestly(String dataKey, Request request) throws InterruptedException, InstantiationException, IllegalAccessException, IOException
	{
//		this.nodeCount.set(0);
		this.receiverCounts.put(request.getCollaboratorKey(), 1);
		String nearestKey = Tools.getClosestKey(dataKey, this.reader.getClientKeys());
		// Create the request without children's IPs. 11/28/2014, Bing Li
//		Request requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
		try
		{
			// Send the request to the immediate node of the root. 11/28/2014, Bing Li
			this.reader.notify(nearestKey, request);
			// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//			this.nodeCount.incrementAndGet();
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/28/2014, Bing Li
			this.reader.removeClient(nearestKey);
			// The count must be decremented for the failed node. 11/28/2014, Bing Li
//			this.nodeCount.decrementAndGet();
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
		// Return the response collection. 11/28/2014, Bing Li
//		return this.responses;
		return this.waitForResponses(request.getCollaboratorKey());
	}
}
