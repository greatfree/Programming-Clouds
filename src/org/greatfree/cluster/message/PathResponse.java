package org.greatfree.cluster.message;

import org.greatfree.message.multicast.container.ChildRootResponse;

// Created: 09/23/2021, Bing Li
public class PathResponse extends ChildRootResponse
{
	private static final long serialVersionUID = -5752032226380628523L;
	
	private String absolutePath;

	public PathResponse(String absolutePath)
	{
		super(ClusterMessageType.PATH_RESPONSE);
		this.absolutePath = absolutePath;
	}

	public String getAbsolutePath()
	{
		return this.absolutePath;
	}
}
