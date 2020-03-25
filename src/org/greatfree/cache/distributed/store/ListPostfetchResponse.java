package org.greatfree.cache.distributed.store;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.Pointing;

// Created: 02/25/2018, Bing Li
public class ListPostfetchResponse<Resource extends Pointing> extends ServerMessage
{
	private static final long serialVersionUID = -6542450019906212841L;
	
	private String cacheKey;
	private Resource rsc;

	public ListPostfetchResponse(int type, String cacheKey, Resource rsc)
	{
		super(type);
		this.cacheKey = cacheKey;
		this.rsc = rsc;
	}

	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public Resource getResource()
	{
		return this.rsc;
	}
}
