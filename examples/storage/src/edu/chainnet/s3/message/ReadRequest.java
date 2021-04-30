package edu.chainnet.s3.message;

import org.greatfree.message.container.Request;

/*
 * The message is sent from the client to the meta server. It results in the slices of a file is restored and returned to the client. 07/13/2020, Bing Li
 */

// Created: 07/13/2020, Bing Li
public class ReadRequest extends Request
{
	private static final long serialVersionUID = 349231537991561639L;

	private String fileName;	

	public ReadRequest(String fileName)
	{
		super(S3AppID.READ_REQUEST);
		this.fileName = fileName;
	}

	public String getFileName()
	{
		return this.fileName;
	}
}
