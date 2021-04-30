package edu.chainnet.s3.meta.table;

import org.greatfree.util.UniqueKey;

/*
 * The code keeps the mapping between the slice key and the partition index. When downloading the slice, the partition index is required to raise the higher performance than normal broadcasting querying. 09/09/2020, Bing Li
 */

// Created: 09/09/2020, Bing Li
class SlicePartition extends UniqueKey
{
	private static final long serialVersionUID = -1681420276311371796L;

	private String sessionKey;
	private String sliceKey;
	private int partitionIndex;
	
	public SlicePartition(String sessionKey, String sliceKey, int partitionIndex)
	{
		super(sliceKey);
		this.sessionKey = sessionKey;
		this.sliceKey = sliceKey;
		this.partitionIndex = partitionIndex;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public String getSliceKey()
	{
		return this.sliceKey;
	}

	public int getPartitionIndex()
	{
		return this.partitionIndex;
	}
}
