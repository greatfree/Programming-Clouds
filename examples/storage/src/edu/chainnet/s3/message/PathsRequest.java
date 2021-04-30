package edu.chainnet.s3.message;

import org.greatfree.message.multicast.container.ChildRootRequest;

/*
 * The message is sent from a child to the root. It is used to synchronize multiple children for partitioning and file accessing if they runs on the same machine. 09/14/2020, Bing Li
 */

// Created: 09/14/2020, Bing Li
public class PathsRequest extends ChildRootRequest
{
	private static final long serialVersionUID = -1793092469464150464L;
	
	private String sssPath;
	private String filePath;

	public PathsRequest(String sssPath, String filePath)
	{
		super(S3AppID.PATHS_REQUEST);
		this.sssPath = sssPath;
		this.filePath = filePath;
	}

	public String getSSSPath()
	{
		return this.sssPath;
	}
	
	public String getFilePath()
	{
		return this.filePath;
	}
}
