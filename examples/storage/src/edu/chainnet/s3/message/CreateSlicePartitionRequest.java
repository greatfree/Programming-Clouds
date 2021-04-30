package edu.chainnet.s3.message;

import org.greatfree.message.container.Request;

/*
 * The message encloses the slice and its partition index. The information is required when downloading the slice from the storage cluster. The information is retained on the meta server. 09/09/2020, Bing Li
 */

// Created: 09/09/2020, Bing Li
public class CreateSlicePartitionRequest extends Request
{
	private static final long serialVersionUID = 3927751317729335125L;
	
	private String sessionKey;
	private String sliceKey;

	public CreateSlicePartitionRequest(String sessionKey, String sliceKey)
	{
		super(S3AppID.CREATE_SLICE_PARTITION_REQUEST);
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
