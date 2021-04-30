package edu.chainnet.s3.message;

import org.greatfree.message.ServerMessage;

// Created: 09/09/2020, Bing Li
public class CreateSlicePartitionResponse extends ServerMessage
{
	private static final long serialVersionUID = 8949887245028644822L;
	
	private int partitionIndex;
	private boolean isSucceeded;

	public CreateSlicePartitionResponse(int partitionIndex, boolean isSucceeded)
	{
		super(S3AppID.CREATE_SLICE_PARTITION_RESPONSE);
		this.partitionIndex = partitionIndex;
		this.isSucceeded = isSucceeded;
	}

	public int getPartitionIndex()
	{
		return this.partitionIndex;
	}
	
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
