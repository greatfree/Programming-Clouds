package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/18/2020, Bing Li
public class RetrieveSCNodesUponDescriptionResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1267466397912929784L;

	private List<DSNode> nodes;

	public RetrieveSCNodesUponDescriptionResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_SC_NODES_UPON_DESCRIPTION_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<DSNode> getNodes()
	{
		return this.nodes;
	}
}
