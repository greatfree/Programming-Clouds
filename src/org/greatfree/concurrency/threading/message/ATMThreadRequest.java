package org.greatfree.concurrency.threading.message;

import org.greatfree.message.container.Request;

// Created: 09/10/2019, Bing Li
public class ATMThreadRequest extends Request
{
	private static final long serialVersionUID = -7369972353171873867L;
	
	private int count;

	public ATMThreadRequest(int count)
	{
		super(ATMMessageType.ATM_THREAD_REQUEST);
		this.count = count;
	}
	
	public ATMThreadRequest()
	{
		super(ATMMessageType.ATM_THREAD_REQUEST);
		this.count = 1;
	}

	public int getCount()
	{
		return this.count;
	}
}

