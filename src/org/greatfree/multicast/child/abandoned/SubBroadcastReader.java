package org.greatfree.multicast.child.abandoned;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.multicast.Tree;
import org.greatfree.util.HashFreeObject;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

// Created: 05/13/2017, Bing Li
abstract class SubBroadcastReader<Request extends OldMulticastRequest, RequestCreator extends ChildBroadcastRequestCreatable<Request>> extends HashFreeObject
{
	// Declare an instance of FreeClient pool, which is used to manage instances of FreeClient to send messages to relevant nodes. 11/11/2014, Bing Li
	private FreeClientPool clientPool;
	// Declare the node's branch count, which can be understood as the capacity of the node to send messages concurrently. In this case, each node has the same capacity. 11/11/2014, Bing Li
	private int treeBranchCount;
	// Different from RootObjectMulticastor, the root node connects each node when the node is online. The node within the multicast tree might not have connections to its children. If so, it is required to provide the port to connect. 11/11/2014, Bing Li
//	private int clusterNodePort;
	// The creator to generate multicast messages. 11/11/2014, Bing Li
	private RequestCreator messageCreator;
	// The local IP key. The key is used to avoid the local node sends messages to itself. 05/19/2017, Bing Li
	private final String localIPKey;
	
	/*
	 * Initialize the multicastor. It is noted that the pool to manage instances of FreeClient is from the outside of the multicastor. It denotes that the multicastor shares the pool with others. 11/11/2014, Bing Li
	 */
//	public SubBroadcastReader(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, RequestCreator messageCreator)
	public SubBroadcastReader(String localIPKey, FreeClientPool clientPool, int treeBranchCount, RequestCreator messageCreator)
	{
		this.localIPKey = localIPKey;
		this.clientPool = clientPool;
		this.treeBranchCount = treeBranchCount;
//		this.clusterNodePort = clusterServerPort;
		this.messageCreator = messageCreator;
	}
	
	/*
	 * Since all of the resources are shared with others, it is unreasonable to dispose them inside the multicastor. Just leave the method for future revisions. 11/11/2014, Bing li
	 */
	public void dispose()
	{
	}
	
	/*
	 * Disseminate the instance of Message. The message here is the one which is just received. It must be forwarded by the local client. 11/11/2014, Bing Li
	 */
	public void disseminate(Request request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Declare a message to be forwarded, which contains data in the received message. 11/11/2014, Bing Li
		Request forwardedRequest;
		// Declare a tree to support the high efficient multicasting. 11/11/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/11/2014, Bing Li
		List<String> allChildrenKeys;
		// Since the notification is set with new created children. It is required to keep the children from the root to avoid being overwritten. 12/03/2018, Bing Li
		Map<String, IPAddress> childrenFromRoot = request.getChildrenIPs();
		// Declare a map to take remote nodes' IPs. 11/11/2014, Bing Li
		HashMap<String, IPAddress> remoteNodeIPs;
		// An integer to keep the new parent node index. 11/11/2014, Bing Li
		int newParentNodeIndex;
		// Check whether the message contains nodes which are waiting for the message. 11/11/2014, Bing Li
//		if (request.getChildrenIPs() != UtilConfig.NO_IPS)
		if (childrenFromRoot != UtilConfig.NO_IPS)
		{
			// If the nodes exist, it needs to check whether the count of the nodes is greater than the branch count. 11/11/2014, Bing Li
//			if (request.getChildrenIPs().size() > this.treeBranchCount)
			if (childrenFromRoot.size() > this.treeBranchCount)
			{
				// If the count of the nodes is larger than that of the branch, it denotes that the load to forward the message exceeds the capacity of the local node. Thus, it is required to construct a tree to lower the load. 11/11/2014, Bing Li
//				tree = Tree.constructTree(UtilConfig.LOCAL_KEY, new LinkedList<String>(request.getChildrenIPs().keySet()), this.treeBranchCount);
				tree = Tree.constructTree(UtilConfig.LOCAL_KEY, new LinkedList<String>(childrenFromRoot.keySet()), this.treeBranchCount);
				// Is sending exception existed. 05/18/2017, Bing Li
				boolean isSendingNormal = true;
				// Forward the received message to the local node's immediate children one by one. 11/11/2014, Bing Li
				for (String childrenKey : tree.get(UtilConfig.LOCAL_KEY))
				{
					// Get all of the children keys of the immediate child of the local node. 11/11/2014, Bing Li
					allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
					// Check if the children keys are valid. 11/11/2014, Bing Li
					if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
					{
						// Initialize a map to keep the IPs of those children nodes of the immediate child of the local node. 11/11/2014, Bing Li
						remoteNodeIPs = new HashMap<String, IPAddress>();
						// Retrieve the IP of each of the child node of the immediate child of the local node and save the IPs into the map. 11/11/2014, Bing Li
						for (String childrenKeyInTree : allChildrenKeys)
						{
							// Retrieve the IP of a child node of the immediate child of the local node and save the IP into the map. 11/11/2014, Bing Li
//							remoteNodeIPs.put(childrenKeyInTree, request.getIP(childrenKeyInTree));
							remoteNodeIPs.put(childrenKeyInTree, childrenFromRoot.get(childrenKeyInTree));
						}
						// Create the message to the immediate child of the local node. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the local node. 11/11/2014, Bing Li
						forwardedRequest = this.messageCreator.createInstanceWithChildren(remoteNodeIPs, request);
						// Check if the instance of FreeClient of the immediate child of the local node is valid and all of the children keys of the immediate child of the local node are not empty. If both of the conditions are true, the loop continues. 11/11/2014, Bing Li
//						while (allChildrenKeys.size() > 0)
						if (allChildrenKeys.size() > 0)
						{
							do
							{
								try
								{
									// Set the sending gets normal. 05/18/2017, Bing Li
									isSendingNormal = true;
									// Send the message to the immediate child of the local node. 11/11/2014, Bing Li
//									this.clientPool.send(new IPPort(request.getIP(childrenKey)), forwardedRequest);
									this.clientPool.send(new IPResource(childrenFromRoot.get(childrenKey)), forwardedRequest);
									// Jump out the loop after sending the message successfully. 11/11/2014, Bing Li
									break;
								}
								catch (IOException e)
								{
									/*
									 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the local node from all of children of the immediate child of the local node. 11/11/2014, Bing Li
									 */
									
									// Remove the failed client from the local node. 11/11/2014, Bing Li
									this.clientPool.removeClient(childrenKey);
									// Select one new node from all of the children of the immediate node of the local node. 11/11/2014, Bing Li
									newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
									// Get the new selected node key by its index. 11/11/2014, Bing Li
									childrenKey = allChildrenKeys.get(newParentNodeIndex);
									// Remove the newly selected parent node key from the children keys of the immediate child of the local node. 11/11/2014, Bing Li
									allChildrenKeys.remove(newParentNodeIndex);
									// Remove the new selected node key from the children's IPs of the immediate node of the local node. 11/11/2014, Bing Li
									remoteNodeIPs.remove(childrenKey);
									// Reset the updated the children's IPs in the message to be forwarded. 11/11/2014, Bing Li
									forwardedRequest.setChildrenNodes(remoteNodeIPs);
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
						 * When the line is executed, it indicates that the immediate child of the local node has no children. 11/11/2014, Bing Li
						 */
						
						// Check whether the instance of FreeClient is valid. 11/11/2014, Bing Li
						try
						{
							// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the local node. 11/11/2014, Bing Li
//							this.clientPool.send(new IPPort(request.getIP(childrenKey)), request);
							this.clientPool.send(new IPResource(childrenFromRoot.get(childrenKey)), request);
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. However, it does not need to forward the message since the immediate node has no children. 11/11/2014, Bing Li
							 */
							
							// Remove the instance of FreeClient. 11/11/2014, Bing Li
							this.clientPool.removeClient(childrenKey);
						}
					}
				}
			}
			else
			{
				/*
				 * If the local node has sufficient capacity to forward the message concurrently, i.e., the tree branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/11/2014, Bing Li
				 */
//				Map<String, IPAddress> ips = request.getChildrenIPs();
				request.setChildrenNodes(UtilConfig.NO_IPS);
				
				// Create the message without children's IPs. 11/11/2014, Bing Li
//				for (Map.Entry<String, IPAddress> serverAddressEntry : request.getChildrenIPs().entrySet())
				for (Map.Entry<String, IPAddress> entry : childrenFromRoot.entrySet())
				{
					if (!entry.getKey().equals(this.localIPKey))
					{
						try
						{
							// Send the message to the immediate node of the local node. 11/11/2014, Bing Li
//							this.clientPool.send(new IPPort(request.getIP(entry.getKey())), request);
							this.clientPool.send(new IPResource(entry.getValue()), request);
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/11/2014, Bing Li
							 */
							
							// Remove the instance of FreeClient. 11/11/2014, Bing Li
							this.clientPool.removeClient(entry.getKey());
						}
					}
				}
			}
		}
	}
}
