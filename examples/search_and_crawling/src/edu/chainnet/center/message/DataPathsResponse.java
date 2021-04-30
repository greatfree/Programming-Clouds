package edu.chainnet.center.message;

import org.greatfree.message.multicast.container.ChildRootResponse;

import edu.chainnet.center.coordinator.manage.DataChildPath;

// Created: 04/27/2021, Bing Li
public class DataPathsResponse extends ChildRootResponse
{
	private static final long serialVersionUID = -3217625121031707369L;
	
	private DataChildPath path;

	public DataPathsResponse(DataChildPath path)
	{
		super(CenterApplicationID.DATA_PATHS_RESPONSE);
		this.path = path;
	}

	public DataChildPath getPath()
	{
		return this.path;
	}
}
