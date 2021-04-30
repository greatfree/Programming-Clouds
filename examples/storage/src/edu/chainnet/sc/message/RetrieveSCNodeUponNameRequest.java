package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/24/2020, Bing Li
public class RetrieveSCNodeUponNameRequest extends Request
{
	private static final long serialVersionUID = 5254635446694659L;
	
	private String nodeName;
	private boolean isHistorical;

	public RetrieveSCNodeUponNameRequest(String nodeName, boolean isHistorical)
	{
		super(DSNode.getKey(nodeName), SCAppID.RETRIEVE_SC_NODE_UPON_NAME_REQUEST);
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
