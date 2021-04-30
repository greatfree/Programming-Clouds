package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/18/2020, Bing Li
public class RetrieveDLNodeUponIDResponse extends MulticastResponse
{
	private static final long serialVersionUID = 5615232624412358094L;
	
	private DSNode node;
	private List<DSNode> nodes;

	public RetrieveDLNodeUponIDResponse(DSNode node, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_DL_NODE_UPON_ID_RESPONSE, collaboratorKey);
		this.node = node;
		this.nodes = null;
	}

	public RetrieveDLNodeUponIDResponse(List<DSNode> nodes, String collaboratorKey)
	{
		super(SCAppID.RETRIEVE_DL_NODE_UPON_ID_RESPONSE, collaboratorKey);
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
