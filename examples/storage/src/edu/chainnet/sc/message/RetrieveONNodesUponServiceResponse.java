package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/19/2020, Bing Li
public class RetrieveONNodesUponServiceResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1501743398002501119L;
	
	private List<DSNode> nodes;

	public RetrieveONNodesUponServiceResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_ON_NODES_UPON_SERVICE_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<DSNode> getNodes()
	{
		return this.nodes;
	}
}
