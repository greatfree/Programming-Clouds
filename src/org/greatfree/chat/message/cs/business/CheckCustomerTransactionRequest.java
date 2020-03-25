package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/20/2017, Bing Li
public class CheckCustomerTransactionRequest extends ServerMessage
{
	private static final long serialVersionUID = -6925362119038181706L;
	
	private String customerKey;

	public CheckCustomerTransactionRequest(String customerKey)
	{
		super(BusinessMessageType.CHECK_CUSTOMER_TRANSACTION_REQUEST);
		this.customerKey = customerKey;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
}
