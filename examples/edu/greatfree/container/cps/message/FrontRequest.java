package edu.greatfree.container.cps.message;

import org.greatfree.message.container.Request;

// Created: 12/31/2018, Bing Li
public class FrontRequest extends Request
{
	private static final long serialVersionUID = -2445864654448479908L;
	
	private String query;

	public FrontRequest(String query)
	{
		super(CPSApplicationID.FRONT_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
