package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/18/2020, Bing Li
public class RetrieveBCNodesUponDescriptionResponse extends MulticastResponse
{
	private static final long serialVersionUID = 825285428063729758L;
	
	private List<BCNode> nodes;

	public RetrieveBCNodesUponDescriptionResponse(List<BCNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_BC_NODES_UPON_DESCRIPTION_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<BCNode> getNodes()
	{
		return this.nodes;
	}
}
