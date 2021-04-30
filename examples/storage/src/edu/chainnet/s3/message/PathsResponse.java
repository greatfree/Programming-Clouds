package edu.chainnet.s3.message;

import org.greatfree.message.multicast.container.ChildRootResponse;

import edu.chainnet.s3.storage.root.table.ChildPath;

// Created: 09/14/2020, Bing Li
public class PathsResponse extends ChildRootResponse
{
	private static final long serialVersionUID = -5338900425735010551L;
	
	private ChildPath path;

	public PathsResponse(ChildPath path)
	{
		super(S3AppID.PATHS_RESPONSE);
		this.path = path;
	}

	public ChildPath getPath()
	{
		return this.path;
	}
}
