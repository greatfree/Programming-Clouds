package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/19/2020, Bing Li
public class RetrieveBCNodesUponVersionResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1526898986833288233L;
	
	private List<BCNode> nodes;

	public RetrieveBCNodesUponVersionResponse(List<BCNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_BC_NODES_UPON_VERSION_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<BCNode> getNodes()
	{
		return this.nodes;
	}
}
