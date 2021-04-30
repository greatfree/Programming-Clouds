package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/24/2020, Bing Li
public class RetrieveBCNodeUponNameResponse extends MulticastResponse
{
	private static final long serialVersionUID = 6716911821891362868L;
	
	private BCNode node;
	private List<BCNode> nodes;

	public RetrieveBCNodeUponNameResponse(BCNode node, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_BC_NODE_UPON_NAME_RESPONSE, collaboratorKey);
		this.node = node;
		this.nodes = null;
	}
	
	public RetrieveBCNodeUponNameResponse(List<BCNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_BC_NODE_UPON_NAME_RESPONSE, collaboratorKey);
		this.node = null;
		this.nodes = nodes;
	}

	public BCNode getNode()
	{
		return this.node;
	}
	
	public List<BCNode> getNodes()
	{
		return this.nodes;
	}
}
