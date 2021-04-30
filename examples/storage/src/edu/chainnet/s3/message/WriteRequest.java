package edu.chainnet.s3.message;

import org.greatfree.message.container.Request;

/*
 * The client sends the message to the meta server to start the writing procedure. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
public class WriteRequest extends Request
{
	private static final long serialVersionUID = 3255493663015723105L;

	private String sessionKey;
	private String fileName;
	private long size;
	private int k;
	private int maxSliceSize;

	public WriteRequest(String sessionKey, String fileName, long size, int k, int maxSliceSize)
	{
		super(S3AppID.WRITE_REQUEST);
		this.sessionKey = sessionKey;
		this.fileName = fileName;
		this.size = size;
		this.k = k;
		this.maxSliceSize = maxSliceSize;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}

	public String getFileName()
	{
		return this.fileName;
	}
	
	public long getSize()
	{
		return this.size;
	}
	
	public int getK()
	{
		return this.k;
	}
	
	public int getMaxSliceSize()
	{
		return this.maxSliceSize;
	}
}
