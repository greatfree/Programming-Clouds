package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

/*
 * Now the strategy is changed. The slices are randomly persisted in the storage cluster. So broadcasting request is required. 07/19/2020, Bing Li
 * 
 * The request is sent to the EDSA cluster through unicasting. 07/18/2020, Bing Li
 */

// Created: 07/18/2020, Bing Li
public class DownloadDecodedSliceRequest extends Request
{
	private static final long serialVersionUID = 8182281698178629874L;
	
	private String sessionKey;
	private String sliceKey;

	public DownloadDecodedSliceRequest(String sessionKey, String sliceKey, int partitionIndex)
	{
		/*
		 * Now the strategy is changed. The slices are randomly persisted in the storage cluster. So broadcasting request is required. 07/19/2020, Bing Li
		 */
//		super(sessionKey, S3AppID.DOWNLOAD_DECODED_SLICE_REQUEST);
		super(MulticastMessageType.BROADCAST_REQUEST, S3AppID.DOWNLOAD_DECODED_SLICE_REQUEST, partitionIndex);
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
