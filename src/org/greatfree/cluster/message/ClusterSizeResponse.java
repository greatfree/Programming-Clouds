package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

/*
 * The message is an internal one, like the PartitionSizeRequest/PartitionSizeResponse, which is processed by the cluster root only. Programmers do not need to do anything but send it. So it inherits ServerMessage. 09/12/2020, Bing Li
 */

// Created: 09/12/2020, Bing Li
public class ClusterSizeResponse extends ServerMessage
{
	private static final long serialVersionUID = -1240883890032855427L;
	
	private int size;

	public ClusterSizeResponse(int size)
	{
		super(ClusterMessageType.CLUSTER_SIZE_RESPONSE);
		this.size = size;
	}

	public int getSize()
	{
		return this.size;
	}
}
