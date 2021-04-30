package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/19/2020, Bing Li
public class RetrieveDLNodesUponServiceResponse extends MulticastResponse
{
	private static final long serialVersionUID = 5743489416496393965L;
	
	private List<DSNode> nodes;

	public RetrieveDLNodesUponServiceResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_DL_NODES_UPON_SERVICE_RESPONSE, collaboratorKey);
		this.nodes = nodes;
	}

	public List<DSNode> getNodes()
	{
		return this.nodes;
	}
}
