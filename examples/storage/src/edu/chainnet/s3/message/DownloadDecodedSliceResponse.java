package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/18/2020, Bing Li
public class DownloadDecodedSliceResponse extends MulticastResponse
{
	private static final long serialVersionUID = 9005281610615880914L;
	
	private byte[] slice;
	private boolean isSucceeded;

	public DownloadDecodedSliceResponse(byte[] slice, String collaboratorKey)
	{
		super(S3AppID.DOWNLOAD_DECODED_SLICE_RESPONSE, collaboratorKey);
		this.slice = slice;
		this.isSucceeded = true;
	}

	public DownloadDecodedSliceResponse(String collaboratorKey)
	{
		super(S3AppID.DOWNLOAD_DECODED_SLICE_RESPONSE, collaboratorKey);
		this.slice = null;
		this.isSucceeded = false;
	}

	public byte[] getSlice()
	{
		return this.slice;
	}
	
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
