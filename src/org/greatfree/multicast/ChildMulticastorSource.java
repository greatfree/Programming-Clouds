package org.greatfree.multicast;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ServerMulticastMessage;

/*
 * The class contains all of the data to create an instance of ChildMulticastor. That is why it is named ChildMulticastorSource. It is used by the resource pool to manage resources efficiently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public abstract class ChildMulticastorSource<MultiMessage extends ServerMulticastMessage, MessageCreator extends ChildMulticastMessageCreatable<MultiMessage>>
{
	// The pool for the resources of FreeClient. 11/27/2014, Bing Li
	private FreeClientPool clientPool;
	// The size of the tree branch. 11/27/2014, Bing Li
	private int treeBranchCount;
	// The port number of the children nodes. 11/27/2014, Bing Li
	private int serverPort;
	// The message creator that generates the multicast messages. 11/27/2014, Bing Li
	private MessageCreator creator;
	
	/*
	 * Initialize the source to provide arguments to create multicastors. 11/27/2014, Bing Li
	 */
	public ChildMulticastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, MessageCreator creator)
	{
		this.clientPool = clientPool;
		this.treeBranchCount = treeBranchCount;
		this.serverPort = serverPort;
		this.creator = creator;
	}
	
	/*
	 * Expose the client pool. 11/27/2014, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.clientPool;
	}
	
	/*
	 * Expose the tree branch count. 11/27/2014, Bing Li
	 */
	public int getTreeBranchCount()
	{
		return this.treeBranchCount;
	}

	/*
	 * Expose the children server port number. 11/27/2014, Bing Li
	 */
	public int getServerPort()
	{
		return this.serverPort;
	}
	
	/*
	 * Expose the message creator. 11/27/2014, Bing Li
	 */
	public MessageCreator getMessageCreator()
	{
		return this.creator;
	}
}
