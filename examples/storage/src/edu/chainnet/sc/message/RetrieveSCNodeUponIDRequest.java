package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/18/2020, Bing Li
public class RetrieveSCNodeUponIDRequest extends Request
{
	private static final long serialVersionUID = 6055977720387710385L;

	private String nodeKey;
	private boolean isHistorical;

	public RetrieveSCNodeUponIDRequest(String nodeKey, boolean isHistorical)
	{
		super(nodeKey, SCAppID.RETRIEVE_SC_NODE_UPON_ID_REQUEST);
		this.nodeKey = nodeKey;
		this.isHistorical = isHistorical;
	}

	public String getNodeID()
	{
		return this.nodeKey;
	}
	
	public boolean isHistorical()
	{
		return this.isHistorical;
	}
}
