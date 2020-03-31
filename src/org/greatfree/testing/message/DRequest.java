package org.greatfree.testing.message;

import org.greatfree.message.container.Request;

// Created: 03/30/2020, Bing Li
public class DRequest extends Request
{
	private static final long serialVersionUID = -344325096750949900L;
	
	private String test;

	public DRequest(String test)
	{
		super(ApplicationID.D_REQUEST);
		this.test = test;
	}

	public String getTest()
	{
		return this.test;
	}
}
