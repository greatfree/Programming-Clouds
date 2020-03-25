package org.greatfree.demo.cps.message;

import org.greatfree.message.container.Request;

// Created: 01/28/2019, Bing Li
public class MerchandiseRequest extends Request
{
	private static final long serialVersionUID = -739739220884417077L;
	
	private String query;

	public MerchandiseRequest(String query)
	{
		super(ApplicationID.MERCHANDISE_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
