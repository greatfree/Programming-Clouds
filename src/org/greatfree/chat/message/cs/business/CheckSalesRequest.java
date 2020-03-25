package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

/*
 * Check current sales online for customers. 12/22/2017, Bing Li
 */

// Created: 12/22/2017, Bing Li
public class CheckSalesRequest extends ServerMessage
{
	private static final long serialVersionUID = 8510106544169277360L;

	public CheckSalesRequest()
	{
		super(BusinessMessageType.CHECK_SALES_REQUEST);
	}

}
