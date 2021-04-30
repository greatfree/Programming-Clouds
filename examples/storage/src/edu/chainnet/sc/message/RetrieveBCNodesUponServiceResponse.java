package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/17/2020, Bing Li
public class RetrieveBCNodesUponServiceResponse extends MulticastResponse
{
	private static final long serialVersionUID = -19256799371458170L;
	
	private List<BCNode> nodes;

	public RetrieveBCNodesUponServiceResponse(List<BCNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_BC_NODES_UPON_SERVICE_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<BCNode> getNodes()
	{
		return this.nodes;
	}
}
