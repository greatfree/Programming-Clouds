package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

/*
 * The message is designed for the scalability such that all of the current children are replaced by new coming ones. In the storage system, the current ones is full in the disk space. In the case, they have to be replaced. But in other cases, it depends on the application level how to raise the scale and deal with the existing children. The system level cannot help. 09/12/2020, Bing Li
 * 
 * The message is an internal one, like the PartitionSizeRequest/PartitionSizeResponse, which is processed by the cluster root only. Programmers do not need to do anything but send it. So it inherits ServerMessage. 09/12/2020, Bing Li
 */

// Created: 09/12/2020, Bing Li
public class ClusterSizeRequest extends ServerMessage
{
	private static final long serialVersionUID = 166480273655877651L;

	public ClusterSizeRequest()
	{
		super(ClusterMessageType.CLUSTER_SIZE_REQUEST);
	}

}
