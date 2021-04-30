package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/17/2020, Bing Li
public class RetrieveBCNodeUponIDRequest extends Request
{
	private static final long serialVersionUID = -8490284404655022121L;
	
	private String nodeID;
	private boolean isHistorical;

	public RetrieveBCNodeUponIDRequest(String nodeKey, boolean isHistorical)
	{
		super(nodeKey, SCAppID.RETRIEVE_BC_NODE_UPON_ID_REQUEST);
		this.nodeID = nodeKey;
		this.isHistorical = isHistorical;
	}

	public String getNodeID()
	{
		return this.nodeID;
	}
	
	public boolean isHistorical()
	{
		return this.isHistorical;
	}
}
