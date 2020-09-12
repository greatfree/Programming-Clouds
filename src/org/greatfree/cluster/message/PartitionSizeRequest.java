package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

/*
 * The partition size is an important parameter to distribute and replicate data on the application level. Since the data distribution or replication strategies are defined by the upper level designer according to specific requirements. It is necessary to expose the information to the application level. 09/09/2020, Bing Li
 */

// Created: 09/09/2020, Bing Li
public class PartitionSizeRequest extends ServerMessage
{
	private static final long serialVersionUID = 1611300011061872449L;

	public PartitionSizeRequest()
	{
		super(ClusterMessageType.PARTITION_SIZE_REQUEST);
	}

}
