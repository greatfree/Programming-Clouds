package org.greatfree.app.search.container.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/14/2019, Bing Li
public class SearchMultiRequest extends ClusterRequest
{
	private static final long serialVersionUID = 1984831618162992478L;

	private String userKey;
	private String query;

	public SearchMultiRequest(String userKey, String query)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, SearchApplicationID.SEARCH_MULTI_REQUEST);
		this.userKey = userKey;
		this.query = query;
	}
	
	public String getUserKey()
	{
		return this.userKey;
	}

	public String getQuery()
	{
		return this.query;
	}
}
