package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.cs.multinode.server.MyTransaction;
import org.greatfree.message.ServerMessage;

// Created: 12/20/2017, Bing Li
public class CheckVendorTransactionResponse extends ServerMessage
{
	private static final long serialVersionUID = 1205661569084538898L;
	
	private Map<String, MyTransaction> transactions;

	public CheckVendorTransactionResponse(Map<String, MyTransaction> transactions)
	{
		super(BusinessMessageType.CHECK_VENDOR_TRANSACTION_RESPONSE);
		this.transactions = transactions;
	}

	public Map<String, MyTransaction> getTransactions()
	{
		return this.transactions;
	}
}
