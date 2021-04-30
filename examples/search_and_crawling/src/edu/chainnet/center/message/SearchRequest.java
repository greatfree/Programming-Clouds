package edu.chainnet.center.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 04/29/2021, Bing Li
public class SearchRequest extends Request
{
	private static final long serialVersionUID = -3837585762406811594L;
	
	private String query;

	public SearchRequest(String query)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, CenterApplicationID.SEARCH_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
