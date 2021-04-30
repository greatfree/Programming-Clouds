package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/24/2020, Bing Li
public class RetrieveONNodeUponNameRequest extends Request
{
	private static final long serialVersionUID = 1355761229653374291L;
	
	private String nodeName;
	private boolean isHistorical;

	public RetrieveONNodeUponNameRequest(String nodeName, boolean isHistorical)
	{
		super(DSNode.getKey(nodeName), SCAppID.RETRIEVE_ON_NODE_UPON_NAME_REQUEST);
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
