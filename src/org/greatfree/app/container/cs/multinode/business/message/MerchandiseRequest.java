package org.greatfree.app.container.cs.multinode.business.message;

import org.greatfree.message.container.Request;

// Created: 01/24/2019, Bing Li
public class MerchandiseRequest extends Request
{
	private static final long serialVersionUID = -6890889757752125416L;

	private String merchandiseName;

	public MerchandiseRequest(String merchandiseName)
	{
		super(ApplicationID.MERCHANDISE_REQUEST);
		this.merchandiseName = merchandiseName;
	}

	public String getMerchandiseName()
	{
		return this.merchandiseName;
	}
}
