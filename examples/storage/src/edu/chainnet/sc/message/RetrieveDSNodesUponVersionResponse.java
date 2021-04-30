package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/19/2020, Bing Li
public class RetrieveDSNodesUponVersionResponse extends MulticastResponse
{
	private static final long serialVersionUID = 6621329337745957180L;
	
	private List<DSNode> nodes;

	public RetrieveDSNodesUponVersionResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_DS_NODES_UPON_VERSION_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<DSNode> getNodes()
	{
		return this.nodes;
	}
}
