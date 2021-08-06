package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.cs.multinode.server.Transaction;
import org.greatfree.message.ServerMessage;

// Created: 12/20/2017, Bing Li
public class CheckVendorTransactionResponse extends ServerMessage
{
	private static final long serialVersionUID = 1205661569084538898L;
	
	private Map<String, Transaction> transactions;

	public CheckVendorTransactionResponse(Map<String, Transaction> transactions)
	{
		super(BusinessMessageType.CHECK_VENDOR_TRANSACTION_RESPONSE);
		this.transactions = transactions;
	}

	public Map<String, Transaction> getTransactions()
	{
		return this.transactions;
	}
}
