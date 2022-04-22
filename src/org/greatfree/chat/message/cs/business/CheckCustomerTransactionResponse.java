package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.cs.multinode.server.MyTransaction;
import org.greatfree.message.ServerMessage;

// Created: 12/20/2017, Bing Li
public class CheckCustomerTransactionResponse extends ServerMessage
{
	private static final long serialVersionUID = 103432444438550286L;
	
	private Map<String, MyTransaction> transactions;

	public CheckCustomerTransactionResponse(Map<String, MyTransaction> transactions)
	{
		super(BusinessMessageType.CHECK_CUSTOMER_TRANSACTION_RESPONSE);
		this.transactions = transactions;
	}

	public Map<String, MyTransaction> getTransactions()
	{
		return this.transactions;
	}
}
