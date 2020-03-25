package org.greatfree.multicast.root.abandoned;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.multicast.Tree;
import org.greatfree.util.HashFreeObject;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The code is a core component to achieve the multicast goal among a bunch of nodes. The nodes are usually organized into a particular topology to raise the multicast efficiency. In the case, a tree is constructed for the nodes. For different situations, a more appropriate topology can be selected. 11/10/2014, Bing Li
 * 
 * In addition, the tree constructed is simple. The root node has a branch count and other nodes have the identical branch count. That indicates that the root node might have a different capacity to send messages concurrently from others. And, all of the nodes except the root has the same computing power and bandwidth. They work in a stable computing environment. The case is available in most enterprise level computing centers. 11/10/2014, Bing Li
 * 
 * Moreover, the multicastor provides an interface to send objects to a bunch of nodes. That is one of the reasons why it is named RootObjectMulticastor. 11/10/2014, Bing Li
 * 
 * Finally, this multicastor runs on the root node of the multicasting tree. Each child node has another design. 11/10/2014, Bing Li
 * 
 */

// Created: 05/12/2017, Bing Li
abstract class BaseBroadcastNotifier<ObjectedData extends Serializable, Message extends OldMulticastMessage, MessageCreator extends RootBroadcastNotificationCreatable<Message, ObjectedData>> extends HashFreeObject
{
	// Declare an instance of FreeClient pool, which is used to manage instances of FreeClient to send messages to relevant nodes. 11/10/2014, Bing Li
	private FreeClientPool clientPool;
	// Declare the root node's branch count, which can be understood as the capacity of the root node to send messages concurrently. 11/10/2014, Bing Li
	private int rootBranchCount;
	// Declare the other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11/10/2014, Bing Li
	private int treeBranchCount;
	// The creator to generate multicast messages. 11/10/2014, Bing Li
	private MessageCreator messageCreator;

	/*
	 * Initialize the multicastor. It is noted that the pool to manage instances of FreeClient is from the outside of the multicastor. It denotes that the multicastor shares the pool with others. 11/10/2014, Bing Li
	 */
	public BaseBroadcastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, MessageCreator messageCreator)
	{
		this.clientPool = clientPool;
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
		this.messageCreator = messageCreator;
	}

	/*
	 * Since all of the resources are shared with others, it is unreasonable to dispose them inside the multicastor. Just leave the method for future revisions. 11/10/2014, Bing li
	 */
	public void dispose()
	{
	}

	/*
	 * Disseminate the instance of ObjectedData. 11/10/2014, Bing Li
	 */
	public void disseminate(ObjectedData obj) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Declare a message to contain the instance of ObjectedData, object. 11/10/2014, Bing Li
		Message msg;
		// Declare a tree to support the high efficient multicasting. 11/10/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/10/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/10/2014, Bing Li
		HashMap<String, IPAddress> remoteNodeIPs;
		// An integer to keep the new parent node index. 11/10/2014, Bing Li
		int newParentNodeIndex;
		
//		System.out.println("1) BaseBroadcastNotifier: disseminate(): data to be sent ..."); 
//		System.out.println("1.5) BaseBroadcastNotifier: disseminate(): data to be sent: client size = " + this.clientPool.getClientSize()); 
		
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/10/2014, Bing Li
		if (this.clientPool.getClientSourceSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send messages concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/10/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.clientPool.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
			// Is sending exception existed. 05/18/2017, Bing Li
			boolean isSendingNormal = true;
			// After the tree is constructed, the root only needs to send messages to its immediate children only. The loop does that by getting the root's children from the tree and sening the message one by one. 11/10/2014, Bing Li
			for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
			{
				// Get all of the children keys of the immediate child of the root. 11/10/2014, Bing Li
				allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/10/2014, Bing Li
				if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/10/2014, Bing Li
					remoteNodeIPs = new HashMap<String, IPAddress>();
					// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/10/2014, Bing Li
					for (String childrenKeyInTree : allChildrenKeys)
					{
						// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/10/2014, Bing Li
						remoteNodeIPs.put(childrenKeyInTree, this.clientPool.getIPAddress(childrenKeyInTree));
					}
					// Create the message to the immediate child of the root. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the root. 11/10/2014, Bing Li
					msg = this.messageCreator.createInstanceWithChildren(remoteNodeIPs, obj);
					// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/10/2014, Bing Li
//					while (allChildrenKeys.size() > 0)
					if (allChildrenKeys.size() > 0)
					{
						do
						{
							try
							{
								// Set the sending gets normal. 05/18/2017, Bing Li
								isSendingNormal = true;
								// Send the message to the immediate child of the root. 11/10/2014, Bing Li
								System.out.println("2) BaseBroadcastNotifier: disseminate(): data to be sent ..."); 
								this.clientPool.send(childrenKey, msg);
								// Jump out the loop after sending the message successfully. 11/10/2014, Bing Li
								System.out.println("3) BaseBroadcastNotifier: disseminate(): data is sent ..."); 
								break;
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/10/2014, Bing Li
								 */
								
								// Remove the failed client from the pool. 11/10/2014, Bing Li
								this.clientPool.removeClient(childrenKey);
								// Select one new node from all of the children of the immediate node of the root. 11/10/2014, Bing Li
								newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
								// Get the new selected node key by its index. 11/10/2014, Bing Li
								childrenKey = allChildrenKeys.get(newParentNodeIndex);
								// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
								allChildrenKeys.remove(newParentNodeIndex);
								// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/10/2014, Bing Li
								remoteNodeIPs.remove(childrenKey);
								// Reset the updated the children's IPs in the message to be sent. 11/10/2014, Bing Li
								msg.setChildrenNodes(remoteNodeIPs);
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
					 * When the line is executed, it indicates that the immediate child of the root has no children. 11/10/2014, Bing Li
					 */
					
					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/10/2014, Bing Li
					msg = this.messageCreator.createInstanceWithoutChildren(obj);
					try
					{
						System.out.println("4) BaseBroadcastNotifier: disseminate(): data to be sent ..."); 
						// Send the message to the immediate node of the root. 11/10/2014, Bing Li
						this.clientPool.send(childrenKey, msg);
						System.out.println("5) BaseBroadcastNotifier: disseminate(): data is sent ..."); 
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/10/2014, Bing Li
						this.clientPool.removeClient(childrenKey);
					}
				}
			}
		}
		else
		{
			/*
			 * If the root has sufficient capacity to send the message concurrently, i.e., the root branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/10/2014, Bing Li
			 */
			
			// Create the message without children's IPs. 11/10/2014, Bing Li
			msg = this.messageCreator.createInstanceWithoutChildren(obj);
			// Send the message one by one to the immediate nodes of the root. 11/10/2014, Bing Li
			for (String childClientKey : this.clientPool.getClientKeys())
			{
				try
				{
					System.out.println("6) BaseBroadcastNotifier: disseminate(): data to be sent ..."); 
					// Send the message to the immediate node of the root. 11/10/2014, Bing Li
					this.clientPool.send(childClientKey, msg);
					System.out.println("7) BaseBroadcastNotifier: disseminate(): data is sent ..."); 
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/10/2014, Bing Li
					this.clientPool.removeClient(childClientKey);
				}
			}
		}
	}

	/*
	 * This method is similar to the above one. The difference is that the children nodes to receive the multicast data are given by the caller rather than obtaining from the FreeClient pool. 11/10/2014, Bing Li
	 */
	public void disseminate(ObjectedData obj, Set<String> clientKeys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Check whether the children nodes are valid. 11/10/2014, Bing Li
		if (clientKeys != UtilConfig.NO_NODE_KEYS)
		{
			// Declare a message to contain the instance of ObjectedData, object. 11/10/2014, Bing Li
			Message msg;
			// Declare a tree to support the high efficiency multicasting. 11/10/2014, Bing Li
			Map<String, List<String>> tree;
			// Declare a list to take children keys. 11/10/2014, Bing Li
			List<String> allChildrenKeys;
			// Declare a list to take children keys. 11/10/2014, Bing Li
			HashMap<String, IPAddress> remoteNodeIPs;
			// An integer to keep the new parent node index. 11/10/2014, Bing Li
			int newParentNodeIndex;
			// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/10/2014, Bing Li
			if (clientKeys.size() > this.rootBranchCount)
			{
				// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send messages concurrently out of its capacity. To lower its load, the tree is required. 11/10/2014, Bing Li
				tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(clientKeys), this.rootBranchCount, this.treeBranchCount);
				// Is sending exception existed. 05/18/2017, Bing Li
				boolean isSendingNormal = true;
				// After the tree is constructed, the root only needs to send messages to its immediate children only. The loop does that by getting the root's children from the tree and sening the message one by one. 11/10/2014, Bing Li
				for (String childrenKey : tree.get(UtilConfig.ROOT_KEY))
				{
					// Get all of the children keys of the immediate child of the root. 11/10/2014, Bing Li
					allChildrenKeys = Tree.getAllChildrenKeys(tree, childrenKey);
					// Check if the children keys are valid. 11/10/2014, Bing Li
					if (allChildrenKeys != UtilConfig.NO_CHILDREN_KEYS)
					{
						// Initialize a map to keep the IPs of those children nodes of the immediate child of the root. 11/10/2014, Bing Li
						remoteNodeIPs = new HashMap<String, IPAddress>();
						// Retrieve the IP of each of the child node of the immediate child of the root and save the IPs into the map. 11/10/2014, Bing Li
						for (String childrenKeyInTree : allChildrenKeys)
						{
							// Retrieve the IP of a child node of the immediate child of the root and save the IP into the map. 11/10/2014, Bing Li
							remoteNodeIPs.put(childrenKeyInTree, this.clientPool.getIPAddress(childrenKeyInTree));
						}
						// Create the message to the immediate child of the root. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the root. 11/10/2014, Bing Li
						msg = this.messageCreator.createInstanceWithChildren(remoteNodeIPs, obj);
						// Check if the instance of FreeClient of the immediate child of the root is valid and all of the children keys of the immediate child of the root are not empty. If both of the conditions are true, the loop continues. 11/10/2014, Bing Li
//						while (allChildrenKeys.size() > 0)
						if (allChildrenKeys.size() > 0)
						{
							do
							{
								try
								{
									// Set the sending gets normal. 05/18/2017, Bing Li
									isSendingNormal = true;
									// Send the message to the immediate child of the root. 11/10/2014, Bing Li
									this.clientPool.send(childrenKey, msg);
									// Jump out the loop after sending the message successfully. 11/10/2014, Bing Li
									break;
								}
								catch (IOException e)
								{
									/*
									 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/10/2014, Bing Li
									 */
									
									// Remove the failed client from the pool. 11/10/2014, Bing Li
									this.clientPool.removeClient(childrenKey);
									// Select one new node from all of the children of the immediate node of the root. 11/10/2014, Bing Li
									newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
									// Get the new selected node key by its index. 11/10/2014, Bing Li
									childrenKey = allChildrenKeys.get(newParentNodeIndex);
									// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
									allChildrenKeys.remove(newParentNodeIndex);
									// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/10/2014, Bing Li
									remoteNodeIPs.remove(childrenKey);
									// Reset the updated the children's IPs in the message to be sent. 11/10/2014, Bing Li
									msg.setChildrenNodes(remoteNodeIPs);
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
						 * When the line is executed, it indicates that the immediate child of the root has no children. 11/10/2014, Bing Li
						 */
						
						// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/10/2014, Bing Li
						msg = this.messageCreator.createInstanceWithoutChildren(obj);
						try
						{
							// Send the message to the immediate node of the root. 11/10/2014, Bing Li
							this.clientPool.send(childrenKey, msg);
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
							 */
							
							// Remove the instance of FreeClient. 11/10/2014, Bing Li
							this.clientPool.removeClient(childrenKey);
						}
					}
				}
			}
			else
			{
				/*
				 * If the root has sufficient capacity to send the message concurrently, i.e., the root branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/10/2014, Bing Li
				 */
				
				// Create the message without children's IPs. 11/10/2014, Bing Li
				msg = this.messageCreator.createInstanceWithoutChildren(obj);
				// Send the message one by one to the immediate nodes of the root. 11/10/2014, Bing Li
				for (String clientKey : clientKeys)
				{
					try
					{
						// Send the message to the immediate node of the root. 11/10/2014, Bing Li
						this.clientPool.send(clientKey, msg);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/10/2014, Bing Li
						this.clientPool.removeClient(clientKey);
					}
				}
			}
		}
	}

	/*
	 * This method is similar to the above one. The difference is that the children nodes to receive the multicast data are given by the caller rather than obtaining from the FreeClient pool. 11/10/2014, Bing Li
	 */
	public void disseminate(ObjectedData obj, String clientKey) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Create the message without children's IPs. 11/10/2014, Bing Li
		Message msg = this.messageCreator.createInstanceWithoutChildren(obj);
		try
		{
			// Send the message to the immediate node of the root. 11/10/2014, Bing Li
			this.clientPool.send(clientKey, msg);
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/10/2014, Bing Li
			this.clientPool.removeClient(clientKey);
		}
	}

	/*
	 * Disseminate the instance of ObjectedData to a nearest node in the cluster in terms of identification string closeness. 11/10/2014, Bing Li
	 */
	public void nearestDisseminate(String key, ObjectedData obj) throws IOException
	{
		String nearestKey = Tools.getClosestKey(key, this.clientPool.getClientKeys());
		// Create the message without children's IPs. 11/10/2014, Bing Li
		Message msg = this.messageCreator.createInstanceWithoutChildren(obj);
		try
		{
			// Send the message to the immediate node of the root. 11/10/2014, Bing Li
			this.clientPool.send(nearestKey, msg);
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/10/2014, Bing Li
			this.clientPool.removeClient(nearestKey);
		}
	}
	

	/*
	 * Disseminate the instance of ObjectedData to a randomly selected node in the cluster. 11/10/2014, Bing Li
	 */
	public void randomDisseminate(ObjectedData obj) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/10/2014, Bing Li
//		if (this.clientPool.getClientSize() > this.rootBranchCount)
//		{
		String randomKey = Tools.getRandomSetElement(this.clientPool.getClientKeys());
		
//		System.out.println("BaseBroadcastNotifier-randomDisseminate(): IP = " + this.clientPool.getIP(randomKey) + ": " + this.clientPool.getIPAddress(randomKey).getPort());
		
		
		/*
		 * If the root has sufficient capacity to send the message concurrently, i.e., the root branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/10/2014, Bing Li
		 */
		
		// Create the message without children's IPs. 11/10/2014, Bing Li
		Message msg = this.messageCreator.createInstanceWithoutChildren(obj);
		try
		{
			// Send the message to the immediate node of the root. 11/10/2014, Bing Li
			this.clientPool.send(randomKey, msg);
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/10/2014, Bing Li
			this.clientPool.removeClient(randomKey);
		}
//		}
	}
}
