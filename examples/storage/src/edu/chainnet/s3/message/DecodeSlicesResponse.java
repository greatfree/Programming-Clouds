package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2020, Bing Li
public class DecodeSlicesResponse extends MulticastResponse
{
	private static final long serialVersionUID = -305214311605951900L;
	
	private boolean isSucceeded;

	public DecodeSlicesResponse(boolean isSucceeded, String collaboratorKey)
	{
		super(S3AppID.DECODE_SLICES_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
