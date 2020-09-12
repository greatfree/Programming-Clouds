package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

// Created: 09/09/2020, Bing Li
public class PartitionSizeResponse extends ServerMessage
{
	private static final long serialVersionUID = -7291440864737891037L;
	
	private int partitionSize;

	public PartitionSizeResponse(int partitionSize)
	{
		super(ClusterMessageType.PARTITION_SIZE_RESPONSE);
		this.partitionSize = partitionSize;
	}

	public int getPartitionSize()
	{
		return this.partitionSize;
	}
}
