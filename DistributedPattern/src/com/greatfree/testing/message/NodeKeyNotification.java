package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This message sends to a client when it is online initially. A unique key is assigned to the client to identify each of them. 11/09/2014, Bing Li
 */

// Created: 09/19/2014, Bing Li
public class NodeKeyNotification extends ServerMessage
{
	private static final long serialVersionUID = -6132975872385412676L;

	// The key of the node. 11/09/2014, Bing Li
	private String nodeKey;

	/*
	 * Initialize the node key. 11/09/2014, Bing Li
	 */
	public NodeKeyNotification(String nodeKey)
	{
		super(MessageType.NODE_KEY_NOTIFICATION);
		this.nodeKey = nodeKey;
	}

	/*
	 * Expose the node key. 11/09/2014, Bing Li
	 */
	public String getNodeKey()
	{
		return this.nodeKey;
	}
}
