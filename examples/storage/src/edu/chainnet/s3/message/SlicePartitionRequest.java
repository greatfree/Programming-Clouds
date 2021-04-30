package edu.chainnet.s3.message;

import org.greatfree.message.container.Request;

/*
 * The request gets the partition index from the meta server. 09/09/2020, Bing Li
 */

// Created: 09/09/2020, Bing Li
public class SlicePartitionRequest extends Request
{
	private static final long serialVersionUID = -6109639945156858277L;
	
	private String sessionKey;
	private String sliceKey;

	public SlicePartitionRequest(String sessionKey, String sliceKey)
	{
		super(S3AppID.SLICE_PARTITION_REQUEST);
		this.sessionKey = sessionKey;
		this.sliceKey = sliceKey;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public String getSliceKey()
	{
		return this.sliceKey;
	}
}
