package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/18/2020, Bing Li
public class RetrieveONNodeUponIDRequest extends Request
{
	private static final long serialVersionUID = 7462705182358927610L;

	private String nodeKey;
	private boolean isHistorical;

	public RetrieveONNodeUponIDRequest(String nodeKey, boolean isHistorical)
	{
		super(nodeKey, SCAppID.RETRIEVE_ON_NODE_UPON_ID_REQUEST);
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
