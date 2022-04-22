package org.greatfree.multicast.rp.child;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.rp.child.ChildPeer;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.message.multicast.RPMulticastResponse;
import org.greatfree.multicast.RendezvousPoint;
import org.greatfree.multicast.Tree;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

// Created: 10/10/2018, Bing Li
class ChildSyncMulticastor
{
	private SyncRemoteEventer<ServerMessage> eventer;
	
	// The other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11//2014, Bing Li
	private int treeBranchCount;

	private String localIPKey;
	
	// The distributed rendezvous point for multicasting. 09/19/2018, Bing Li
//	private OldRendezvousPoint rp;
	private RendezvousPoint rp;

	public ChildSyncMulticastor(String localIPKey, FreeClientPool clientPool, int treeBranchCount)
	{
		this.localIPKey = localIPKey;
		this.eventer = new SyncRemoteEventer<ServerMessage>(clientPool);
		this.treeBranchCount = treeBranchCount;
	}
	
	public void dispose() throws IOException
	{
		this.eventer.dispose();
	}

//	public void setRP(OldRendezvousPoint rp)
	public void setRP(RendezvousPoint rp)
	{
		this.rp = rp;
	}
	
	public void notify(String rpIP, int rpPort, RPMulticastResponse responses) throws IOException, InterruptedException
	{
		this.eventer.notify(rpIP, rpPort, responses);
	}
	
	public void notify(String rpIP, int rpPort, MulticastResponse response) throws IOException, InterruptedException
	{
		this.eventer.notify(rpIP, rpPort, response);
	}

	/*
	 * Disseminate the instance of Message synchronously. 11/11/2014, Bing Li
	 */
	public void notify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		// Declare a message to be forwarded, which contains data in the received message. 11/11/2014, Bing Li
//		Message forwardMessage;
		// Declare a tree to support the high efficient multicasting. 11/11/2014, Bing Li
		Map<String, List<String>> tree;
		// Declare a list to take children keys. 11/11/2014, Bing Li
		List<String> allChildrenKeys;
		// Since the notification is set with new created children. It is required to keep the children from the root to avoid being overwritten. 12/03/2018, Bing Li
		Map<String, IPAddress> childrenFromRoot = notification.getChildrenIPs();
		// Declare a map to take remote nodes' IPs. 11/11/2014, Bing Li
		HashMap<String, IPAddress> remoteNodeIPs;
		// An integer to keep the new parent node index. 11/11/2014, Bing Li
		int newParentNodeIndex;
		// Check whether the message contains nodes which are waiting for the message. 11/11/2014, Bing Li
//		if (notification.getChildrenIPs() != UtilConfig.NO_IPS)
		if (childrenFromRoot != UtilConfig.NO_IPS)
		{
//			System.out.println("ChildEventer-disseminate(): children count = " + notification.getChildrenIPs().size());
			// If the nodes exist, it needs to check whether the count of the nodes is greater than the branch count. 11/11/2014, Bing Li
//			if (notification.getChildrenIPs().size() > this.treeBranchCount)
			if (childrenFromRoot.size() > this.treeBranchCount)
			{
				// If the count of the nodes is larger than that of the branch, it denotes that the load to forward the message exceeds the capacity of the local node. Thus, it is required to construct a tree to lower the load. 11/11/2014, Bing Li
//				tree = Tree.constructTree(UtilConfig.LOCAL_KEY, new LinkedList<String>(notification.getChildrenIPs().keySet()), this.treeBranchCount);
				tree = Tree.constructTree(UtilConfig.LOCAL_KEY, new LinkedList<String>(childrenFromRoot.keySet()), this.treeBranchCount);
				// Is sending exception existed. 05/18/2017, Bing Li
				boolean isSendingNormal = true;
				// Forward the received message to the local node's immediate children one by one. 11/11/2014, Bing Li
				for (String childrenKey : tree.get(UtilConfig.LOCAL_KEY))
				{
					if (!childrenKey.equals(this.localIPKey))
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
//								remoteNodeIPs.put(childrenKeyInTree, notification.getIP(childrenKeyInTree));
								remoteNodeIPs.put(childrenKeyInTree, childrenFromRoot.get(childrenKeyInTree));
							}
							// Create the message to the immediate child of the local node. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the local node. 11/11/2014, Bing Li
//							forwardMessage = this.messageCreator.createInstanceWithChildren(remoteNodeIPs, message);
							notification.setChildrenNodes(remoteNodeIPs);
							// Check if the instance of FreeClient of the immediate child of the local node is valid and all of the children keys of the immediate child of the local node are not empty. If both of the conditions are true, the loop continues. 11/11/2014, Bing Li
//							while (allChildrenKeys.size() > 0)
							if (allChildrenKeys.size() > 0)
							{
								do
								{
									try
									{
										// Set the sending gets normal. 05/18/2017, Bing Li
										isSendingNormal = true;
										// Send the message to the immediate child of the local node. 11/11/2014, Bing Li
//										this.eventer.notify(new IPPort(notification.getIP(childrenKey)), notification);
										IPAddress ip = childrenFromRoot.get(childrenKey);
//										this.eventer.notify(new IPResource(ip.getPeerKey(), ip.getPeerName(), ip.getIP(), ip.getPort()), notification);
										this.eventer.notify(new IPResource(ip.getIP(), ip.getPort()), notification);
										// Jump out the loop after sending the message successfully. 11/11/2014, Bing Li
										break;
									}
									catch (IOException e)
									{
										/*
										 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the local node from all of children of the immediate child of the local node. 11/11/2014, Bing Li
										 */
										
										// Remove the failed client from the local node. 11/11/2014, Bing Li
										this.eventer.removeClient(childrenKey);
										// Select one new node from all of the children of the immediate node of the local node. 11/11/2014, Bing Li
										newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
										// Get the new selected node key by its index. 11/11/2014, Bing Li
										childrenKey = allChildrenKeys.get(newParentNodeIndex);
										// Remove the newly selected parent node key from the children keys of the immediate child of the local node. 11/11/2014, Bing Li
										allChildrenKeys.remove(newParentNodeIndex);
										// Remove the new selected node key from the children's IPs of the immediate node of the local node. 11/11/2014, Bing Li
										remoteNodeIPs.remove(childrenKey);
										// Reset the updated the children's IPs in the message to be forwarded. 11/11/2014, Bing Li
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
							 * When the line is executed, it indicates that the immediate child of the local node has no children. 11/11/2014, Bing Li
							 */
							notification.setChildrenNodes(UtilConfig.NO_IPS);
							
							// Check whether the instance of FreeClient is valid. 11/11/2014, Bing Li
							try
							{
								// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the local node. 11/11/2014, Bing Li
//								this.eventer.notify(new IPPort(notification.getIP(childrenKey)), notification);
								IPAddress ip = childrenFromRoot.get(childrenKey);
//								this.eventer.notify(new IPResource(ip.getPeerKey(), ip.getPeerName(), ip.getIP(), ip.getPort()), notification);
								this.eventer.notify(new IPResource(ip.getIP(), ip.getPort()), notification);
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. However, it does not need to forward the message since the immediate node has no children. 11/11/2014, Bing Li
								 */
								
								// Remove the instance of FreeClient. 11/11/2014, Bing Li
								this.eventer.removeClient(childrenKey);
								throw new DistributedNodeFailedException(childrenKey);
							}
						}
					}
				}
			}
			else
			{
				/*
				 * If the local node has sufficient capacity to forward the message concurrently, i.e., the tree branch count being greater than that of its immediate children, it is not necessary to construct a tree to lower the load. 11/11/2014, Bing Li
				 */
				
//				Map<String, IPAddress> ips = notification.getChildrenIPs();
				notification.setChildrenNodes(UtilConfig.NO_IPS);
				
				// Create the message without children's IPs. 11/11/2014, Bing Li
//				for (Map.Entry<String, IPAddress> serverAddressEntry : message.getChildrenIPs().entrySet())
				for (Map.Entry<String, IPAddress> entry : childrenFromRoot.entrySet())
				{
					if (!entry.getKey().equals(this.localIPKey))
					{
//						System.out.println("localIPKey = " + this.localIPKey);
//						System.out.println(entry.getKey() + ": " + entry.getValue().getIP() + "-" + entry.getValue().getPort());
//						System.out.println("SubBroadcastNotifier: DescendantPeer.CLUSTER().getChildID() = " + DescendantPeer.CLUSTER().getChildID());
//						System.out.println("SubBroadcastNotifier: serverAddressEntry.getKey() = " + serverAddressEntry.getKey());
//						System.out.println("SubBroadcastNotifier: local port = " + DescendantPeer.CLUSTER().getPeerPort());
//						System.out.println("SubBroadcastNotifier: remote port = " + message.getIP(serverAddressEntry.getKey()).getPort());
						try
						{
							// Send the message to the immediate node of the local node. 11/11/2014, Bing Li
//							this.clientPool.send(new IPPort(message.getIP(serverAddressEntry.getKey())), message);
//							this.eventer.notify(new IPResource(entry.getValue().getPeerKey(), entry.getValue().getPeerName(), entry.getValue().getIP(), entry.getValue().getPort()), notification);
							this.eventer.notify(new IPResource(entry.getValue().getIP(), entry.getValue().getPort()), notification);
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/11/2014, Bing Li
							 */
							
							// Remove the instance of FreeClient. 11/11/2014, Bing Li
							this.eventer.removeClient(entry.getKey());
							throw new DistributedNodeFailedException(entry.getKey());
						}
					}
				}
			}
		}
		else
		{
//			System.out.println("ChildEventer-disseminate(): NO children!");
		}
	}

	
	/*
	 * Disseminate the instance of Message. The message here is the one which is just received. It must be forwarded by the local client. 11/11/2014, Bing Li
	 */
	public void read(RPMulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		// Declare a message to be forwarded, which contains data in the received message. 11/11/2014, Bing Li
//		Request forwardedRequest;
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
			// Since the asynchronous invocation for the current method, the receiver size needs to be set before reading is performed asynchronously. It is done outside the current method. 12/17/2018, Bing Li
			// The size of children IPs is not just its immediate children. The size should be the count of all of the nodes in the subtree, whose root is the current local node. 10/13/2018, Bing Li
			// The RP needs to wait for a couple of responses from its children. The size is the total of its immediate children and the one of its own. 10/10/2018, Bing Li 
//			this.rp.addPoint(request.getCollaboratorKey(), request.getChildrenIPs().size() + 1);
//			this.rp.addPoint(request.getCollaboratorKey(), childrenFromRoot.size() + 1);
//			this.rp.setReceiverSize(request.getCollaboratorKey(), childrenFromRoot.size() + 1);
			
//			System.out.println("ChildSyncMulticastor-read(): collaborator key = " + request.getCollaboratorKey());
			
			// If the nodes exist, it needs to check whether the count of the nodes is greater than the branch count. 11/11/2014, Bing Li
//			if (request.getChildrenIPs().size() > this.treeBranchCount)
			if (childrenFromRoot.size() > this.treeBranchCount)
			{
				// If the count of the nodes is larger than that of the branch, it denotes that the load to forward the message exceeds the capacity of the local node. Thus, it is required to construct a tree to lower the load. 11/11/2014, Bing Li
//				tree = Tree.constructTree(UtilConfig.LOCAL_KEY, new LinkedList<String>(request.getChildrenIPs().keySet()), this.treeBranchCount);
				tree = Tree.constructTree(UtilConfig.LOCAL_KEY, new LinkedList<String>(childrenFromRoot.keySet()), this.treeBranchCount);

				/*
				 * The line is used for testing ONLY. 12/03/2018, Bing Li
				 */
				Tree.printSubTree(ChildPeer.CHILD().getLocalIP().getIP(), ChildPeer.CHILD().getLocalIP().getPort(), tree, childrenFromRoot);
				
				// Is sending exception existed. 05/18/2017, Bing Li
				boolean isSendingNormal = true;
				// Forward the received message to the local node's immediate children one by one. 11/11/2014, Bing Li
				for (String childrenKey : tree.get(UtilConfig.LOCAL_KEY))
				{
					if (!childrenKey.equals(this.localIPKey))
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
//								remoteNodeIPs.put(childrenKeyInTree, request.getIP(childrenKeyInTree));
								remoteNodeIPs.put(childrenKeyInTree, childrenFromRoot.get(childrenKeyInTree));
							}
							// Create the message to the immediate child of the local node. The message is created by enclosing the object to be sent and the IPs of all of the children nodes of the immediate child of the local node. 11/11/2014, Bing Li
//							forwardedRequest = this.messageCreator.createInstanceWithChildren(remoteNodeIPs, request);
							request.setChildrenNodes(remoteNodeIPs);
							// Check if the instance of FreeClient of the immediate child of the local node is valid and all of the children keys of the immediate child of the local node are not empty. If both of the conditions are true, the loop continues. 11/11/2014, Bing Li
//							while (allChildrenKeys.size() > 0)
							if (allChildrenKeys.size() > 0)
							{
								do
								{
									try
									{
										// Set the sending gets normal. 05/18/2017, Bing Li
										isSendingNormal = true;
										// Send the message to the immediate child of the local node. 11/11/2014, Bing Li
//										this.eventer.notify(new IPPort(request.getIP(childrenKey)), request);
										IPAddress ip = childrenFromRoot.get(childrenKey);
//										this.eventer.notify(new IPResource(ip.getPeerKey(), ip.getPeerName(), ip.getIP(), ip.getPort()), request);
										this.eventer.notify(new IPResource(ip.getIP(), ip.getPort()), request);
										// Jump out the loop after sending the message successfully. 11/11/2014, Bing Li
										break;
									}
									catch (IOException e)
									{
										/*
										 * The exception denotes that the remote end gets something wrong. It is required to select another immediate child for the local node from all of children of the immediate child of the local node. 11/11/2014, Bing Li
										 */
										
										// Remove the failed client from the local node. 11/11/2014, Bing Li
										this.eventer.removeClient(childrenKey);
										// Select one new node from all of the children of the immediate node of the local node. 11/11/2014, Bing Li
										newParentNodeIndex = Rand.getRandom(allChildrenKeys.size());
										// Get the new selected node key by its index. 11/11/2014, Bing Li
										childrenKey = allChildrenKeys.get(newParentNodeIndex);
										// Remove the newly selected parent node key from the children keys of the immediate child of the local node. 11/11/2014, Bing Li
										allChildrenKeys.remove(newParentNodeIndex);
										// Remove the new selected node key from the children's IPs of the immediate node of the local node. 11/11/2014, Bing Li
										remoteNodeIPs.remove(childrenKey);
										// Reset the updated the children's IPs in the message to be forwarded. 11/11/2014, Bing Li
										request.setChildrenNodes(remoteNodeIPs);
										// Set the sending gets exceptional. 05/18/2017, Bing Li
//										this.rp.decrementNode(request.getCollaboratorKey());
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
							 * When the line is executed, it indicates that the immediate child of the local node has no children. 11/11/2014, Bing Li
							 */
							request.setChildrenNodes(UtilConfig.NO_IPS);
							
							// Check whether the instance of FreeClient is valid. 11/11/2014, Bing Li
							try
							{
								// If the instance of FreeClient is valid, a message can be created. Different from the above one, the message does not contain children IPs of the immediate node of the local node. 11/11/2014, Bing Li
//								this.eventer.notify(new IPPort(request.getIP(childrenKey)), request);
								IPAddress ip = childrenFromRoot.get(childrenKey);
//								this.eventer.notify(new IPResource(ip.getPeerKey(), ip.getPeerName(), ip.getIP(), ip.getPort()), request);
								this.eventer.notify(new IPResource(ip.getIP(), ip.getPort()), request);
							}
							catch (IOException e)
							{
								/*
								 * The exception denotes that the remote end gets something wrong. However, it does not need to forward the message since the immediate node has no children. 11/11/2014, Bing Li
								 */
								
								// Remove the instance of FreeClient. 11/11/2014, Bing Li
								this.eventer.removeClient(childrenKey);
//								this.rp.decrementNode(request.getCollaboratorKey());
								this.rp.decrementReceiverSize(request.getCollaboratorKey());
								throw new DistributedNodeFailedException(childrenKey);
							}
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
//							this.eventer.notify(new IPResource(entry.getValue().getPeerKey(), entry.getValue().getPeerName(), entry.getValue().getIP(), entry.getValue().getPort()), request);
							this.eventer.notify(new IPResource(entry.getValue().getIP(), entry.getValue().getPort()), request);
						}
						catch (IOException e)
						{
							/*
							 * The exception denotes that the remote end gets something wrong. However, it does not need to send the message since the immediate node has no children. 11/11/2014, Bing Li
							 */
//							this.rp.decrementNode(request.getCollaboratorKey());
							this.rp.decrementReceiverSize(request.getCollaboratorKey());
							
							// Remove the instance of FreeClient. 11/11/2014, Bing Li
							this.eventer.removeClient(entry.getKey());
							throw new DistributedNodeFailedException(entry.getKey());
						}
					}
				}
			}
		}
	}
}
