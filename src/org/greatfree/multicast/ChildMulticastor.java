package org.greatfree.multicast;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.message.ServerMulticastMessage;
import org.greatfree.util.HashFreeObject;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

/*
 * This is the multicasting code to run on a child node. 11/11/2014, Bing Li
 */

// Created: 11/10/2014, Bing Li
public abstract class ChildMulticastor<Message extends ServerMulticastMessage, MessageCreator extends ChildMulticastMessageCreatable<Message>> extends HashFreeObject
{
	// Declare an instance of FreeClient pool, which is used to manage instances of FreeClient to send messages to relevant nodes. 11/11/2014, Bing Li
	private FreeClientPool clientPool;
	// Declare the node's branch count, which can be understood as the capacity of the node to send messages concurrently. In this case, each node has the same capacity. 11/11/2014, Bing Li
	private int treeBranchCount;
	// Different from RootObjectMulticastor, the root node connects each node when the node is online. The node within the multicast tree might not have connections to its children. If so, it is required to provide the port to connect. 11/11/2014, Bing Li
	private int clusterNodePort;
	// The creator to generate multicast messages. 11/11/2014, Bing Li
	private MessageCreator messageCreator;
	
	/*
	 * Initialize the multicastor. It is noted that the pool to manage instances of FreeClient is from the outside of the multicastor. It denotes that the multicastor shares the pool with others. 11/11/2014, Bing Li
	 */
	public ChildMulticastor(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, MessageCreator messageCreator)
	{
		this.clientPool = clientPool;
		this.treeBranchCount = treeBranchCount;
		this.clusterNodePort = clusterServerPort;
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
	public void disseminate(Message message) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Declare a message to be forwarded, which contains data in the received message. 11/11/2014, Bing Li
		Message forwardMessage;
		// Declare a tree to support the high efficient multicasting. 11/11/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/11/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/11/2014, Bing Li
		HashMap<String, String> remoteNodeIPs;
		// An integer to keep the new parent node index. 11/11/2014, Bing Li
		int newParentNodeIndex;
		// Check whether the message contains nodes which are waiting for the message. 11/11/2014, Bing Li
		if (message.getChildrenNodes() != UtilConfig.NO_NODES)
		{
			// If the nodes exist, it needs to check whether the count of the nodes is greater than the branch count. 11/11/2014, Bing Li
			if (message.getChildrenNodes().size() > this.treeBranchCount)
			{
				// If the count of the nodes is larger than that of the branch, it denotes that the load to forward the message exceeds the capacity of the local node. Thus, it is required to construct a tree to lower the load. 11/11/2014, Bing Li
				tree = Tree.constructTree(UtilConfig.LOCAL_KEY, new LinkedList<String>(message.getChildrenNodes().keySet()), this.treeBranchCount);
				// Forward the received message to the local node's immediate children one by one. 11/11/2014, Bing Li
				for (String childrenKey : tree.get(UtilConfig.LOCAL_KEY))
				{
					// Get all of the children keys of the immediate child of the local node. 11/11/2014, Bing Li
					allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
					// Check if the children keys are valid. 11/11/2014, Bing Li
					if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
					{
						// Initialize a map to keep the IPs of those children nodes of the immediate child of the local node. 11/11/2014, Bing Li
						remoteNodeIPs = new HashMap<String, String>();
						// Retrieve the IP of each of the child node of the immediate child of the local node and save the IPs into the map. 11/11/2014, Bing Li
						for (String childrenKeyInTree : allChildrenKeys)
						{
							// Retrieve the IP of a child node of the immediate child of the local node and save the IP into the map. 11/11/2014, Bing Li
							remoteNodeIPs.put(childrenKeyInTree, message.getChildrenNodeIP(childrenKeyInTree));
						}
						// Create the message to the immediate child of the local node. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the local node. 11/11/2014, Bing Li
						forwardMessage = this.messageCreator.createInstanceWithChildren(message, remoteNodeIPs);
						// Check if the instance of FreeClient of the immediate child of the local node is valid and all of the children keys of the immediate child of the local node are not empty. If both of the conditions are true, the loop continues. 11/11/2014, Bing Li
						while (allChildrenKeys.size() > 0)
						{
							try
							{
								// Send the message to the immediate child of the local node. 11/11/2014, Bing Li
								this.clientPool.send(new IPResource(message.getChildrenNodeIP(childrenKey), this.clusterNodePort), forwardMessage);
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
								forwardMessage.setChildrenNodes(remoteNodeIPs);
							}
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
							this.clientPool.send(new IPResource(message.getChildrenNodeIP(childrenKey), this.clusterNodePort), message);
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
				
				// Create the message without children's IPs. 11/11/2014, Bing Li
				for (Map.Entry<String, String> serverAddressEntry : message.getChildrenNodes().entrySet())
				{
					try
					{
						// Send the message to the immediate node of the local node. 11/11/2014, Bing Li
						this.clientPool.send(new IPResource(message.getChildrenNodeIP(serverAddressEntry.getValue()), this.clusterNodePort), message);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/11/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/11/2014, Bing Li
						this.clientPool.removeClient(serverAddressEntry.getKey());
					}
				}
			}
		}
	}
}