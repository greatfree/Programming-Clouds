package edu.chainnet.s3.message;

import java.util.Map;

import org.greatfree.message.multicast.MulticastResponse;

import edu.chainnet.s3.storage.child.table.FileDescription;

// Created: 07/13/2020, Bing Li
public class RetrieveFileResponse extends MulticastResponse
{
	private static final long serialVersionUID = -1750082304241382992L;
	
	private Map<String, FileDescription> files;

	public RetrieveFileResponse(Map<String, FileDescription> files, String collaboratorKey)
	{
		super(S3AppID.RETRIEVE_FILE_RESPONSE, collaboratorKey);
		this.files = files;
	}

	public Map<String, FileDescription> getFiles()
	{
		return this.files;
	}
}
