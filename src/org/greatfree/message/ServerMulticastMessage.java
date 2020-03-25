package org.greatfree.message;

import java.util.Map;

import org.greatfree.util.UtilConfig;


/*
 * This is the base class to implement the message that can be multicast among a bunch of nodes. 11/09/2014, Bing Li
 */

// Created: 11/09/2014, Bing Li
public class ServerMulticastMessage extends ServerMessage
{
	private static final long serialVersionUID = -6067174732413112924L;

	// The nodes to receive the message to be multicast. 11/09/2014, Bing Li
	private Map<String, String> childrenNodes;

	/*
	 * Initialize the message to be multicast. 11/10/2014, Bing Li
	 * 
	 * The argument, messageType, represents the type of the message;
	 * 
	 * The argument, key, represents the unique key of the message;
	 * 
	 * The argument, childrenNodes, keeps all of the nodes that receive the message. The key of the collection is the unique key of the client and the value is the IP address. Here, the port number is omitted since usually the port of all of the nodes in a cluster is the same.
	 * 
	 */
	public ServerMulticastMessage(int messageType, String key, Map<String, String> childrenNodes)
	{
		super(messageType, key);
		this.childrenNodes = childrenNodes;
	}

	/*
	 * Another constructor of the message. This is invoked by the node which has no grandsons. 11/10/2014, Bing Li
	 */
	public ServerMulticastMessage(int messageType, String key)
	{
		super(messageType, key);
		this.childrenNodes = UtilConfig.NO_NODES;
	}

	/*
	 * Set the children nodes. The method is usually invoked when it is failed to send a message to a particular node. Therefore, it is necessary to choose another node and reset the children nodes to it after removing the failed one. 11/10/2014, Bing Li
	 */
	public void setChildrenNodes(Map<String, String> childrenNodes)
	{
		this.childrenNodes = childrenNodes;
	}

	/*
	 * Get the IP of a specific child by its key. With the IP, it is convenient to connect the child by the FreeClient pool. 11/10/2014, Bing Li
	 */
	public String getChildrenNodeIP(String childrenKey)
	{
		// Check whether the child key exists. 11/10/2014, Bing Li
		if (this.childrenNodes.containsKey(childrenKey))
		{
			// Return the IP of the child. 11/10/2014, Bing Li
			return this.childrenNodes.get(childrenKey);
		}
		// Return null if the child key does not exist. 11/10/2014, Bing Li
		return UtilConfig.NO_IP;
	}

	/*
	 * Get the collection of all of the children. The method is invoked when it is received by a particular node and it is required for the node to keep transfer the message to those children. After getting the keys of the children, the node is able to construct a tree for those children to raise the multicast efficiency. 11/10/2014, Bing Li
	 */
	public Map<String, String> getChildrenNodes()
	{
		return this.childrenNodes;
	}
}
