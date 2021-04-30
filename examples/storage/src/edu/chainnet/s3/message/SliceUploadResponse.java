package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/09/2020, Bing Li
public class SliceUploadResponse extends MulticastResponse
{
	private static final long serialVersionUID = 5435673776793952477L;

	private boolean isSucceeded;

	public SliceUploadResponse(boolean isSucceeded, String collaboratorKey)
	{
		super(S3AppID.SLICE_UPLOAD_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
