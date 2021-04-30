package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/24/2020, Bing Li
public class RetrieveSCNodeUponNameResponse extends MulticastResponse
{
	private static final long serialVersionUID = -1620345889397375103L;
	
	private DSNode node;
	private List<DSNode> nodes;

	public RetrieveSCNodeUponNameResponse(DSNode node, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_SC_NODE_UPON_NAME_RESPONSE, collaboratorKey);
		this.node = node;
		this.nodes = null;
	}

	public RetrieveSCNodeUponNameResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_SC_NODE_UPON_NAME_RESPONSE, collaboratorKey);
		this.node = null;
		this.nodes = nodes;
	}

	public DSNode getNode()
	{
		return this.node;
	}
	
	public List<DSNode> getNodes()
	{
		return this.nodes;
	}
}
