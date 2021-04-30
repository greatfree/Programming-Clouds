package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/17/2020, Bing Li
public class DataLakeNodeRegistryRequest extends Request
{
	private static final long serialVersionUID = 2841522952439616536L;

	private DSNode node;

	public DataLakeNodeRegistryRequest(DSNode node)
	{
		super(node.getKey(), SCAppID.DATA_LAKE_NODE_REGISTRY_REQUEST);
		this.node = node;
	}
	
	public DSNode getNode()
	{
		return this.node;
	}
}
