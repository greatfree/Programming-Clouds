package org.greatfree.demo.message;

import org.greatfree.message.container.Request;

// Created: 01/24/2018, Bing Li
public class MerchandiseRequest extends Request
{
	private static final long serialVersionUID = -6890889757752125416L;
	
	private String name;

	public MerchandiseRequest(String name)
	{
		super(ApplicationID.MERCHANDISE_REQUEST);
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
