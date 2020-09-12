package org.greatfree.multicast.rp.root;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.multicast.RendezvousPoint;
import org.greatfree.multicast.Tree;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

// Created: 10/15/2018, Bing Li
class RootSyncMulticastor
{
	private SyncRemoteEventer<ServerMessage> eventer;
	
	// The root node's branch count, which can be understood as the capacity of the root node to send messages concurrently. The root is the broadcast request initial sender. 11/28/2014, Bing Li
	private int rootBranchCount;
	// The other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11//2014, Bing Li
	private int treeBranchCount;
	
	// The distributed rendezvous point for multicasting. 09/19/2018, Bing Li
//	private RendezvousPoint rp;
	private RendezvousPoint rp;
	
	public RootSyncMulticastor(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount)
	{
		this.eventer = new SyncRemoteEventer<ServerMessage>(clientPool);
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
	}

	public void dispose() throws IOException, InterruptedException
	{
		// Dispose the synchronous eventer
		this.eventer.dispose();
	}
	
	public void setRP(RendezvousPoint rp)
	{
		this.rp = rp;
	}

	public void randomNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/10/2014, Bing Li
//		if (this.clientPool.getClientSize() > this.rootBranchCount)
//		{
		String randomKey = Tools.getRandomSetElement(this.eventer.getClientKeys());
		
//		System.out.println("BaseBroadcastNotifier-randomDisseminate(): IP = " + this.clientPool.getIP(randomKey) + ": " + this.clientPool.getIPAddress(randomKey).getPort());
		/*
		 * If the root has sufficient capacity to send the message concurrently, i.e., the root branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/10/2014, Bing Li
		 */
		
		// Create the message without children's IPs. 11/10/2014, Bing Li
//		Message msg = this.messageCreator.createInstanceWithoutChildren(obj);
		try
		{
			// Send the message to the immediate node of the root. 11/10/2014, Bing Li
			this.eventer.notify(randomKey, notification);
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/10/2014, Bing Li
			this.eventer.removeClient(randomKey);
			throw new DistributedNodeFailedException(randomKey);
		}
	}

	public void nearestNotify(String key, MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		String nearestKey = Tools.getClosestKey(key, this.eventer.getClientKeys());
		// Create the message without children's IPs. 11/10/2014, Bing Li
//		Message msg = this.messageCreator.createInstanceWithoutChildren(obj);
		try
		{
			// Send the message to the immediate node of the root. 11/10/2014, Bing Li
			this.eventer.notify(nearestKey, notification);
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/10/2014, Bing Li
			this.eventer.removeClient(nearestKey);
			throw new DistributedNodeFailedException(nearestKey);
		}
	}

	public void notify(MulticastNotification notification, String childKey) throws IOException, DistributedNodeFailedException
	{
		// Create the message without children's IPs. 11/10/2014, Bing Li
//		Message msg = this.messageCreator.createInstanceWithoutChildren(obj);
		try
		{
			// Send the message to the immediate node of the root. 11/10/2014, Bing Li
			this.eventer.notify(childKey, notification);
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/10/2014, Bing Li
			this.eventer.removeClient(childKey);
			throw new DistributedNodeFailedException(childKey);
		}
	}

	public void notify(MulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException
	{
		// Check whether the children nodes are valid. 11/10/2014, Bing Li
		if (childrenKeys != UtilConfig.NO_NODE_KEYS)
		{
			// Declare a message to contain the instance of ObjectedData, object. 11/10/2014, Bing Li
//			Message msg;
			// Declare a tree to support the high efficiency multicasting. 11/10/2014, Bing Li
			Map<String, List<String>> tree;
			// Declare a list to take children keys. 11/10/2014, Bing Li
			List<String> allChildrenKeys;
			// Declare a list to take children keys. 11/10/2014, Bing Li
			HashMap<String, IPAddress> remoteNodeIPs;
			// An integer to keep the new parent node index. 11/10/2014, Bing Li
			int newParentNodeIndex;
			// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/10/2014, Bing Li
			if (childrenKeys.size() > this.rootBranchCount)
			{
				// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send messages concurrently out of its capacity. To lower its load, the tree is required. 11/10/2014, Bing Li
				tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(childrenKeys), this.rootBranchCount, this.treeBranchCount);
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
							remoteNodeIPs.put(childrenKeyInTree, this.eventer.getIPAddress(childrenKeyInTree));
						}
						// Create the message to the immediate child of the root. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the root. 11/10/2014, Bing Li
//						msg = this.messageCreator.createInstanceWithChildren(remoteNodeIPs, obj);
						notification.setChildrenNodes(remoteNodeIPs);
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
									this.eventer.notify(childrenKey, notification);
									// Jump out the loop after sending the message successfully. 11/10/2014, Bing Li
									break;
								}
								catch (IOException e)
								{
									/*
									 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/10/2014, Bing Li
									 */
									
									// Remove the failed client from the pool. 11/10/2014, Bing Li
									this.eventer.removeClient(childrenKey);
									// Select one new node from all of the children of the immediate node of the root. 11/10/2014, Bing Li
									newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
									// Get the new selected node key by its index. 11/10/2014, Bing Li
									childrenKey = allChildrenKeys.get(newParentNodeIndex);
									// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
									allChildrenKeys.remove(newParentNodeIndex);
									// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/10/2014, Bing Li
									remoteNodeIPs.remove(childrenKey);
									// Reset the updated the children's IPs in the message to be sent. 11/10/2014, Bing Li
									notification.setChildrenNodes(remoteNodeIPs);
									// Set the sending gets exceptional. 05/18/2017, Bing Li
									isSendingNormal = false;
									throw new DistributedNodeFailedException(childrenKey);
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
//						msg = this.messageCreator.createInstanceWithoutChildren(obj);
						notification.setChildrenNodes(UtilConfig.NO_IPS);
						try
						{
							// Send the message to the immediate node of the root. 11/10/2014, Bing Li
							this.eventer.notify(childrenKey, notification);
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
							 */
							
							// Remove the instance of FreeClient. 11/10/2014, Bing Li
							this.eventer.removeClient(childrenKey);
							throw new DistributedNodeFailedException(childrenKey);
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
				notification.setChildrenNodes(UtilConfig.NO_IPS);
//				msg = this.messageCreator.createInstanceWithoutChildren(obj);
				// Send the message one by one to the immediate nodes of the root. 11/10/2014, Bing Li
				for (String clientKey : childrenKeys)
				{
					try
					{
						// Send the message to the immediate node of the root. 11/10/2014, Bing Li
						this.eventer.notify(clientKey, notification);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/10/2014, Bing Li
						this.eventer.removeClient(clientKey);
						throw new DistributedNodeFailedException(clientKey);
					}
				}
			}
		}
	}
	
	/*
	 * Multicast data in a synchronous way. It could be a broadcast or an anycast. 09/15/2018, Bing Li
	 */
	public void notify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		// Declare a tree to support the high efficient multicasting. 11/10/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/10/2014, Bing Li
		List<String> allChildrenKeys;
		// Declare a map to take remote nodes' IPs. 11/10/2014, Bing Li
		HashMap<String, IPAddress> remoteNodeIPs;
		// An integer to keep the new parent node index. 11/10/2014, Bing Li
		int newParentNodeIndex;
		
//		System.out.println("RootEventer-disseminate(): client size = " + this.eventer.getClientSize());
		
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/10/2014, Bing Li
		if (this.eventer.getClientSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send messages concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/10/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.eventer.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
			// Is sending exception existed. 05/18/2017, Bing Li
			boolean isSendingNormal = true;
			// After the tree is constructed, the root only needs to send messages to its immediate children only. The loop does that by getting the root's children from the tree and sening the message one by one. 11/10/2014, Bing Li
			
//			System.out.println("1) Root's children count = " + tree.get(UtilConfig.ROOT_KEY).size());
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
						remoteNodeIPs.put(childrenKeyInTree, this.eventer.getIPAddress(childrenKeyInTree));
					}
//					System.out.println("2) RootEventer-disseminate(): remoteNodeIPs size = " + remoteNodeIPs.size());
					// Create the message to the immediate child of the root. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the root. 11/10/2014, Bing Li
//					msg = this.messageCreator.createInstanceWithChildren(remoteNodeIPs, obj);
					notification.setChildrenNodes(remoteNodeIPs);
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
//								System.out.println("2) BaseBroadcastNotifier: disseminate(): data to be sent ..."); 
								this.eventer.notify(childrenKey, notification);
								// Jump out the loop after sending the message successfully. 11/10/2014, Bing Li
//								System.out.println("3) BaseBroadcastNotifier: disseminate(): data is sent ..."); 
								break;
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the root from all of children of the immediate child of the root. 11/10/2014, Bing Li
								 */
								
								// Remove the failed client from the pool. 11/10/2014, Bing Li
								this.eventer.removeClient(childrenKey);
								// Select one new node from all of the children of the immediate node of the root. 11/10/2014, Bing Li
								newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
								// Get the new selected node key by its index. 11/10/2014, Bing Li
								childrenKey = allChildrenKeys.get(newParentNodeIndex);
								// Remove the newly selected parent node key from the children keys of the immediate child of the root. 11/11/2014, Bing Li
								allChildrenKeys.remove(newParentNodeIndex);
								// Remove the new selected node key from the children's IPs of the immediate node of the root. 11/10/2014, Bing Li
								remoteNodeIPs.remove(childrenKey);
								// Reset the updated the children's IPs in the message to be sent. 11/10/2014, Bing Li
								notification.setChildrenNodes(remoteNodeIPs);
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
								throw new DistributedNodeFailedException(childrenKey);
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
//					System.out.println("3) RootEventer-disseminate(): notification is sent without children");
					
					// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the root. 11/10/2014, Bing Li
//					msg = this.messageCreator.createInstanceWithoutChildren(obj);
					notification.setChildrenNodes(UtilConfig.NO_IPS);
					try
					{
//						System.out.println("4) BaseBroadcastNotifier: disseminate(): data to be sent ...");
						// Send the message to the immediate node of the root. 11/10/2014, Bing Li
						this.eventer.notify(childrenKey, notification);
//						System.out.println("5) BaseBroadcastNotifier: disseminate(): data is sent ..."); 
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/10/2014, Bing Li
						this.eventer.removeClient(childrenKey);
						throw new DistributedNodeFailedException(childrenKey);
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
			notification.setChildrenNodes(UtilConfig.NO_IPS);
//			msg = this.messageCreator.createInstanceWithoutChildren(obj);
//			notification.setChildrenNodes(null);
//			System.out.println("4) RootEventer-disseminate(): notification is sent without children");
			// Send the message one by one to the immediate nodes of the root. 11/10/2014, Bing Li
			for (String childClientKey : this.eventer.getClientKeys())
			{
				try
				{
//					System.out.println("6) BaseBroadcastNotifier: disseminate(): data to be sent ...");
					// Send the message to the immediate node of the root. 11/10/2014, Bing Li
					this.eventer.notify(childClientKey, notification);
//					System.out.println("7) BaseBroadcastNotifier: disseminate(): data is sent ...");
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/10/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/10/2014, Bing Li
					this.eventer.removeClient(childClientKey);
					throw new DistributedNodeFailedException(childClientKey);
				}
			}
		}
	}

	public void randomRead(RPMulticastRequest request) throws IOException, DistributedNodeFailedException
	{
//		this.nodeCount.set(0);
//		this.receiverCounts.put(request.getCollaboratorKey(), UtilConfig.ONE);
//		this.rp.addPoint(request.getCollaboratorKey(), UtilConfig.ONE);
		this.rp.setReceiverSize(request.getCollaboratorKey(), UtilConfig.ONE);
		String randomKey = Tools.getRandomSetElement(this.eventer.getClientKeys());
		// Create the request without children's IPs. 11/28/2014, Bing Li
//		Request requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
		try
		{
			// Send the request to the immediate node of the root. 11/28/2014, Bing Li
			this.eventer.notify(randomKey, request);
			// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//			this.nodeCount.incrementAndGet();
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/28/2014, Bing Li
			this.eventer.removeClient(randomKey);
			// The count must be decremented for the failed node. 11/28/2014, Bing Li
//			this.nodeCount.decrementAndGet();
//			this.rp.decrementNode(request.getCollaboratorKey());
			this.rp.decrementReceiverSize(request.getCollaboratorKey());
			throw new DistributedNodeFailedException(randomKey);
		}
	}
	
	public void nearestRead(Set<String> dataKeys, RPMulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		Set<String> nearestClientKeys = Sets.newHashSet();
		for (String dataKey : dataKeys)
		{
			nearestClientKeys.add(Tools.getClosestKey(dataKey, this.eventer.getClientKeys()));
		}
		this.read(nearestClientKeys, request);
	}
	
	public void nearestRead(String dataKey, RPMulticastRequest request) throws IOException, DistributedNodeFailedException
	{
//		this.nodeCount.set(0);
//		this.receiverCounts.put(request.getCollaboratorKey(), UtilConfig.ONE);
//		this.rp.addPoint(request.getCollaboratorKey(), UtilConfig.ONE);
		this.rp.setReceiverSize(request.getCollaboratorKey(), UtilConfig.ONE);
		String nearestKey = Tools.getClosestKey(dataKey, this.eventer.getClientKeys());
		// Create the request without children's IPs. 11/28/2014, Bing Li
//		Request requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
		try
		{
			// Send the request to the immediate node of the root. 11/28/2014, Bing Li
			this.eventer.notify(nearestKey, request);
			// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//			this.nodeCount.incrementAndGet();
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/28/2014, Bing Li
			this.eventer.removeClient(nearestKey);
			// The count must be decremented for the failed node. 11/28/2014, Bing Li
//			this.nodeCount.decrementAndGet();
//			this.rp.decrementNode(request.getCollaboratorKey());
			this.rp.decrementReceiverSize(request.getCollaboratorKey());
			throw new DistributedNodeFailedException(nearestKey);
		}
	}
	
	public void read(String childKey, RPMulticastRequest request) throws IOException, DistributedNodeFailedException
	{
//		this.nodeCount.set(0);
//		this.receiverCounts.put(request.getCollaboratorKey(), UtilConfig.ONE);
//		this.rp.addPoint(request.getCollaboratorKey(), UtilConfig.ONE);
		this.rp.setReceiverSize(request.getCollaboratorKey(), UtilConfig.ONE);
		// Create the request without children's IPs. 11/28/2014, Bing Li
//		Request requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
		try
		{
			// Send the request to the immediate node of the root. 11/28/2014, Bing Li
			this.eventer.notify(childKey, request);
			// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//			this.nodeCount.incrementAndGet();
		}
		catch (IOException e)
		{
			/*
			 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
			 */
			
			// Remove the instance of FreeClient. 11/28/2014, Bing Li
			this.eventer.removeClient(childKey);
			// The count must be decremented for the failed node. 11/28/2014, Bing Li
//			this.nodeCount.decrementAndGet();
//			this.rp.decrementNode(request.getCollaboratorKey());
			this.rp.decrementReceiverSize(request.getCollaboratorKey());
			throw new DistributedNodeFailedException(childKey);
		}
	}
	
	public void read(RPMulticastRequest request, int n) throws IOException, DistributedNodeFailedException
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
//		this.receiverCounts.put(request.getCollaboratorKey(), n);
//		this.rp.addPoint(request.getCollaboratorKey(), n);
		this.rp.setReceiverSize(request.getCollaboratorKey(), n);
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
//		this.nodeCount.set(this.clientPool.getClientSize());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (this.eventer.getClientSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.eventer.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
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
						remoteServerIPs.put(childrenKeyInTree, this.eventer.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//						requestToBeSent = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, request);
					request.setChildrenNodes(remoteServerIPs);
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
								this.eventer.notify(childrenKey, request);
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
								this.eventer.removeClient(childrenKey);
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
//								this.rp.decrementNode(request.getCollaboratorKey());
								this.rp.decrementReceiverSize(request.getCollaboratorKey());
								/*
								 * Since the parameter, n, represents the largest number of children to respond, it is not necessary to eliminate the value even though one of the children gets failed. Anyway, the fault-tolerance issue is not processed well in the current version. 09/15/2018, Bing Li
								 */
								// The count must be decremented for the failed node. 11/28/2014, Bing Li
//								this.nodeCount.decrementAndGet();
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
								throw new DistributedNodeFailedException(childrenKey);
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
					request.setChildrenNodes(UtilConfig.NO_IPS);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.eventer.notify(childrenKey, request);
						// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//						this.nodeCount.incrementAndGet();
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.eventer.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//							this.nodeCount.decrementAndGet();
//						this.rp.decrementNode(request.getCollaboratorKey());
						this.rp.decrementReceiverSize(request.getCollaboratorKey());
						throw new DistributedNodeFailedException(childrenKey);
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
			request.setChildrenNodes(UtilConfig.NO_IPS);
//				requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : this.eventer.getClientKeys())
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.eventer.notify(childClientKey, request);
					// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//					this.nodeCount.incrementAndGet();
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.eventer.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
//					this.rp.decrementNode(request.getCollaboratorKey());
					this.rp.decrementReceiverSize(request.getCollaboratorKey());
					throw new DistributedNodeFailedException(childClientKey);
				}
			}
		}
	}
	
	public void read(Set<String> childrenKeys, RPMulticastRequest request) throws DistributedNodeFailedException, IOException
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
//		this.receiverCounts.put(request.getCollaboratorKey(), clientKeys.size());
//		this.rp.addPoint(request.getCollaboratorKey(), childrenKeys.size());
		this.rp.setReceiverSize(request.getCollaboratorKey(), childrenKeys.size());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
//		if (this.receiverCounts.get(request.getCollaboratorKey()) > this.rootBranchCount)
//		if (this.rp.getPointSize(request.getCollaboratorKey()) > this.rootBranchCount)
		if (this.rp.getReceiverSize(request.getCollaboratorKey()) > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(childrenKeys), this.rootBranchCount, this.treeBranchCount);
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
						remoteServerIPs.put(childrenKeyInTree, this.eventer.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//					requestToBeSent = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, request);
					request.setChildrenNodes(remoteServerIPs);
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
								this.eventer.notify(childrenKey, request);
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
								this.eventer.removeClient(childrenKey);
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
//								this.decrementNode(request.getCollaboratorKey());
//								this.rp.decrementNode(request.getCollaboratorKey());
								this.rp.decrementReceiverSize(request.getCollaboratorKey());
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
								throw new DistributedNodeFailedException(childrenKey);
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
					request.setChildrenNodes(UtilConfig.NO_IPS);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.eventer.notify(childrenKey, request);
						// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//						this.nodeCount.incrementAndGet();
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.eventer.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
//						this.decrementNode(request.getCollaboratorKey());
//						this.rp.decrementNode(request.getCollaboratorKey());
						this.rp.decrementReceiverSize(request.getCollaboratorKey());
						throw new DistributedNodeFailedException(childrenKey);
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
			request.setChildrenNodes(UtilConfig.NO_IPS);
//			requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), request);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childKey : childrenKeys)
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.eventer.notify(childKey, request);
					// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//					this.nodeCount.incrementAndGet();
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.eventer.removeClient(childKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//					this.nodeCount.decrementAndGet();
//					this.decrementNode(request.getCollaboratorKey());
//					this.rp.decrementNode(request.getCollaboratorKey());
					this.rp.decrementReceiverSize(request.getCollaboratorKey());
					throw new DistributedNodeFailedException(childKey);
				}
			}
		}
	}
	
	public void read(Set<String> childrenKeys, RPMulticastRequest request, int n) throws IOException, DistributedNodeFailedException
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
		if (childrenKeys.size() > n)
		{
//			this.receiverCounts.put(request.getCollaboratorKey(), n);
//			this.rp.addPoint(request.getCollaboratorKey(), n);
			this.rp.setReceiverSize(request.getCollaboratorKey(), n);
		}
		else
		{
//			this.receiverCounts.put(request.getCollaboratorKey(), clientKeys.size());
//			this.rp.addPoint(request.getCollaboratorKey(), childrenKeys.size());
			this.rp.setReceiverSize(request.getCollaboratorKey(), childrenKeys.size());
		}
		// Keep the count of nodes that must respond the request. 11/28/2014, Bing Li
//		this.nodeCount.set(clientKeys.size());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (childrenKeys.size() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(childrenKeys), this.rootBranchCount, this.treeBranchCount);
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
						remoteServerIPs.put(childrenKeyInTree, this.eventer.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//						requestToBeSent = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, messagedData);
					request.setChildrenNodes(remoteServerIPs);
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
								this.eventer.notify(childrenKey, request);
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
								childrenKeys.remove(childrenKey);
								// Remove the failed child key. 11/28/2014, Bing Li
								this.eventer.removeClient(childrenKey);
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
//								this.rp.decrementNode(request.getCollaboratorKey());
								this.rp.decrementReceiverSize(request.getCollaboratorKey());
								isSendingNormal = false;
								throw new DistributedNodeFailedException(childrenKey);
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
					request.setChildrenNodes(UtilConfig.NO_IPS);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.eventer.notify(childrenKey, request);
						// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//						this.nodeCount.incrementAndGet();
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						// Remove the failed child key. 11/28/2014, Bing Li
						childrenKeys.remove(childrenKey);
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.eventer.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//							this.nodeCount.decrementAndGet();
//						this.rp.decrementNode(request.getCollaboratorKey());
						this.rp.decrementReceiverSize(request.getCollaboratorKey());
						throw new DistributedNodeFailedException(childrenKey);
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
			request.setChildrenNodes(UtilConfig.NO_IPS);
//				requestToBeSent = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), messagedData);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : childrenKeys)
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.eventer.notify(childClientKey, request);
					// Calculate the count of the children in the cluster. 05/21/2017, Bing Li
//					this.nodeCount.incrementAndGet();
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the failed child key. 11/28/2014, Bing Li
					childrenKeys.remove(childClientKey);
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.eventer.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
//					this.rp.decrementNode(request.getCollaboratorKey());
					this.rp.decrementReceiverSize(request.getCollaboratorKey());
					throw new DistributedNodeFailedException(childClientKey);
				}
			}
		}
	}
	
	public void read(RPMulticastRequest request) throws DistributedNodeFailedException, IOException
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
//		this.receiverCounts.put(request.getCollaboratorKey(), this.reader.getClientSize());
//		this.rp.addPoint(request.getCollaboratorKey(), this.eventer.getClientSize());
		this.rp.setReceiverSize(request.getCollaboratorKey(), this.eventer.getClientSize());
		// Check whether the FreeClient pool has the count of nodes than the root capacity. 11/28/2014, Bing Li
		if (this.eventer.getClientSize() > this.rootBranchCount)
		{
			// Construct a tree if the count of nodes is larger than the capacity of the root. Without the tree, the root node has to send requests concurrently out of its capacity. To lower its load, the tree is required. All the nodes to received the multicast data are from the FreeClient pool. 11/28/2014, Bing Li
			tree = Tree.constructTree(UtilConfig.ROOT_KEY, new LinkedList<String>(this.eventer.getClientKeys()), this.rootBranchCount, this.treeBranchCount);
			/*
			 * The line is used for testing ONLY. 12/03/2018, Bing Li
			 */
			Tree.printRootTree(tree, this.eventer);

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
						remoteServerIPs.put(childrenKeyInTree, this.eventer.getIPAddress(childrenKeyInTree));
					}
					// Create the request to the immediate child of the root. The request is created by enclosing the object to be sent, the collaborator key and the IPs of all of the children nodes of the immediate child of the root. 11/28/2014, Bing Li
//					request = this.requestCreator.createInstanceWithChildren(this.collaborator.getKey(), remoteServerIPs, messagedData);
					request.setChildrenNodes(remoteServerIPs);
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
								this.eventer.notify(childrenKey, request);
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
								this.eventer.removeClient(childrenKey);
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
//								this.decrementNode(request.getCollaboratorKey());
//								this.rp.decrementNode(request.getCollaboratorKey());
								this.rp.decrementReceiverSize(request.getCollaboratorKey());
								// Set the sending gets exceptional. 05/18/2017, Bing Li
								isSendingNormal = false;
								throw new DistributedNodeFailedException(childrenKey);
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
					request.setChildrenNodes(UtilConfig.NO_IPS);
					try
					{
						// Send the request to the immediate node of the root. 11/28/2014, Bing Li
						this.eventer.notify(childrenKey, request);
					}
					catch (IOException e)
					{
						/*
						 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
						 */
						
						// Remove the instance of FreeClient. 11/28/2014, Bing Li
						this.eventer.removeClient(childrenKey);
						// The count must be decremented for the failed node. 11/28/2014, Bing Li
//						this.nodeCount.decrementAndGet();
//						this.decrementNode(request.getCollaboratorKey());
//						this.rp.decrementNode(request.getCollaboratorKey());
						this.rp.decrementReceiverSize(request.getCollaboratorKey());
						throw new DistributedNodeFailedException(childrenKey);
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
			request.setChildrenNodes(UtilConfig.NO_IPS);
//			request = this.requestCreator.createInstanceWithoutChildren(this.collaborator.getKey(), messagedData);
			// Send the request one by one to the immediate nodes of the root. 11/28/2014, Bing Li
			for (String childClientKey : this.eventer.getClientKeys())
			{
				try
				{
					// Send the request to the immediate node of the root. 11/28/2014, Bing Li
					this.eventer.notify(childClientKey, request);
				}
				catch (IOException e)
				{
					/*
					 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/28/2014, Bing Li
					 */
					
					// Remove the instance of FreeClient. 11/28/2014, Bing Li
					this.eventer.removeClient(childClientKey);
					// The count must be decremented for the failed node. 11/28/2014, Bing Li
//					this.nodeCount.decrementAndGet();
//					this.decrementNode(request.getCollaboratorKey());
//					this.rp.decrementNode(request.getCollaboratorKey());
					this.rp.decrementReceiverSize(request.getCollaboratorKey());
					throw new DistributedNodeFailedException(childClientKey);
				}
			}
		}
		// The requesting procedure is blocked until all of the responses are received or it has waited for sufficiently long time. 11/28/2014, Bing Li
//		this.collaborator.holdOn(this.waitTime);
//		return this.waitForResponses(request.getCollaboratorKey());
	}
}
