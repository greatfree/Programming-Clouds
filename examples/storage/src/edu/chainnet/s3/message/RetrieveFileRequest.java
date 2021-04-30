package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

/*
 * This is the message sent from the meta server to the storage cluster to retrieve the file a client wants to read. This is a broadcast request. The design is different from the document of S3. In the design of S3, retrieving files is performed on the meta server. It is not a so crucial issue. 07/13/2020, Bing Li
 */

// Created: 07/13/2020, Bing Li
public class RetrieveFileRequest extends Request
{
	private static final long serialVersionUID = -6898843567864496514L;
	
	private String fileName;

	public RetrieveFileRequest(String fileName)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, S3AppID.RETRIEVE_FILE_REQUEST);
		this.fileName = fileName;
	}

	public String getFileName()
	{
		return this.fileName;
	}
}
