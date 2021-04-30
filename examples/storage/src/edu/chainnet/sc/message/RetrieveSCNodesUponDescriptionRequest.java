package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 10/18/2020, Bing Li
public class RetrieveSCNodesUponDescriptionRequest extends Request
{
	private static final long serialVersionUID = -5249255684899839383L;
	
	private String description;
	private boolean isHistorical;

	public RetrieveSCNodesUponDescriptionRequest(String description, boolean isHistorical)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SCAppID.RETRIEVE_SC_NODES_UPON_DESCRIPTION_REQUEST);
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
