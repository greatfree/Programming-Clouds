package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/18/2020, Bing Li
public class RetrieveONNodesUponDescriptionResponse extends MulticastResponse
{
	private static final long serialVersionUID = -6103513070903776726L;

	private List<DSNode> nodes;

	public RetrieveONNodesUponDescriptionResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_ON_NODES_UPON_DESCRIPTION_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<DSNode> getNodes()
	{
		return this.nodes;
	}
}
