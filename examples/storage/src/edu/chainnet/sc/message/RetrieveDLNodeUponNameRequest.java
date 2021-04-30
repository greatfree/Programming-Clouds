package edu.chainnet.sc.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/14/2020, Bing Li
public class RetrieveDLNodeUponNameRequest extends Request
{
	private static final long serialVersionUID = 8001146241830917473L;
	
	private String nodeName;
	private boolean isHistorical; 

	public RetrieveDLNodeUponNameRequest(String nodeName, boolean isHistorical)
	{
		super(DSNode.getKey(nodeName), SCAppID.RETRIEVE_DL_NODE_UPON_NAME_REQUEST);
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
