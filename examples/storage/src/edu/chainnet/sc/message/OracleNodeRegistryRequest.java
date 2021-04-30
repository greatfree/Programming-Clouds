package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/17/2020, Bing Li
public class OracleNodeRegistryRequest extends Request
{
	private static final long serialVersionUID = -4335628207348858152L;

	private DSNode node;

	public OracleNodeRegistryRequest(DSNode node)
	{
		super(node.getKey(), SCAppID.ORACLE_NODE_REGISTRY_REQUEST);
		this.node = node;
	}
	
	public DSNode getNode()
	{
		return this.node;
	}
}
