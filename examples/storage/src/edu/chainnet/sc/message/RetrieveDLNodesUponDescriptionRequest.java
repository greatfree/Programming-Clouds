package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 10/18/2020, Bing Li
public class RetrieveDLNodesUponDescriptionRequest extends Request
{
	private static final long serialVersionUID = -3766121355865085540L;
	
	private String description;
	private boolean isHistorical;

	public RetrieveDLNodesUponDescriptionRequest(String description, boolean isHistorical)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SCAppID.RETRIEVE_DL_NODES_UPON_DESCRIPTION_REQUEST);
		this.description = description;
		this.isHistorical = isHistorical;
	}

	public String getDescription()
	{
		return this.description;
	}
	
	public boolean isHistorical()
	{
		return this.isHistorical;
	}
}
