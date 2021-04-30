package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/17/2020, Bing Li
public class SmartContractNodeRegistryRequest extends Request
{
	private static final long serialVersionUID = 6219404741222992422L;

	private DSNode node;

	public SmartContractNodeRegistryRequest(DSNode node)
	{
		super(node.getKey(), SCAppID.SMART_CONTRACT_NODE_REGISTRY_REQUEST);
		this.node = node;
	}
	
	public DSNode getNode()
	{
		return this.node;
	}
}
