package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/24/2020, Bing Li
public class RetrieveBCNodeUponNameRequest extends Request
{
	private static final long serialVersionUID = 8375992101457122746L;
	
	private String nodeName;
	private boolean isHistorical;

	public RetrieveBCNodeUponNameRequest(String nodeName, boolean isHistorical)
	{
		super(BCNode.getKey(nodeName), SCAppID.RETRIEVE_BC_NODE_UPON_NAME_REQUEST);
		this.nodeName = nodeName;
		this.isHistorical = isHistorical;
	}

	public String getName()
	{
		return this.nodeName;
	}
	
	public boolean isHistorical()
	{
		return this.isHistorical;
	}
}
