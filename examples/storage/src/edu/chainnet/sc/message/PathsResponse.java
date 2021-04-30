package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.ChildRootResponse;

import edu.chainnet.sc.collaborator.ChildPath;

// Created: 10/20/2020, Bing Li
public class PathsResponse extends ChildRootResponse
{
	private static final long serialVersionUID = 2168621375733124622L;
	
	private ChildPath path;

	public PathsResponse(ChildPath path)
	{
		super(SCAppID.PATHS_RESPONSE);
		this.path = path;
	}

	public ChildPath getPath()
	{
		return this.path;
	}
}
