package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/18/2020, Bing Li
public class RetrieveDLNodesUponDescriptionResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1792700830132715602L;

	private List<DSNode> nodes;

	public RetrieveDLNodesUponDescriptionResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_DL_NODES_UPON_DESCRIPTION_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<DSNode> getNodes()
	{
		return this.nodes;
	}
}
