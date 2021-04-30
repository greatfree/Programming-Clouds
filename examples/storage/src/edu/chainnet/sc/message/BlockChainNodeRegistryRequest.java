package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/17/2020, Bing Li
public class BlockChainNodeRegistryRequest extends Request
{
	private static final long serialVersionUID = -9197073706517781271L;

	private BCNode node;

	public BlockChainNodeRegistryRequest(BCNode node)
	{
		super(node.getKey(), SCAppID.BLOCK_CHAIN_NODE_REGISTRY_REQUEST);
		this.node = node;
	}
	
	public BCNode getNode()
	{
		return this.node;
	}
}
