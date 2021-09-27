package org.greatfree.cluster.message;

import org.greatfree.message.multicast.container.ChildRootRequest;

// Created: 09/23/2021, Bing Li
public class PathRequest extends ChildRootRequest
{
	private static final long serialVersionUID = -8255484168452664518L;
	
	private String relativePath;

	public PathRequest(String relativePath)
	{
		super(ClusterMessageType.PATH_REQUEST);
		this.relativePath = relativePath;
	}

	public String getRelativePath()
	{
		return this.relativePath;
	}
}
