package edu.chainnet.s3.message;

import org.greatfree.message.ServerMessage;

// Created: 07/14/2020, Bing Li
public class DownloadEncodedFileResponse extends ServerMessage
{
	private static final long serialVersionUID = 233943880079102703L;
	
	private boolean isSucceeded;

	public DownloadEncodedFileResponse(boolean isSucceeded)
	{
		super(S3AppID.DOWNLOAD_ENCODED_FILE_RESPONSE);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
