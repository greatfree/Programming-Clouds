package org.greatfree.concurrency.threading.message;

import java.util.Set;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.UtilConfig;

// Created: 09/10/2019, Bing Li
public class ATMThreadResponse extends ServerMessage
{
	private static final long serialVersionUID = 8467830324219168081L;
	
	private String threadKey;
//	private boolean isAvailable;
	
//	private Map<String, Boolean> threadKeys;
	private Set<String> threadKeys;

//	public NotificationThreadResponse(String threadKey, boolean isAvailable)
	public ATMThreadResponse(String threadKey)
	{
		super(ATMMessageType.ATM_THEAD_RESPONSE);
		this.threadKey = threadKey;
//		this.isAvailable = isAvailable;
		this.threadKeys = null;
	}
	
//	public NotificationThreadResponse(Map<String, Boolean> threadKeys)
	public ATMThreadResponse(Set<String> threadKeys)
	{
		super(ATMMessageType.ATM_THEAD_RESPONSE);
		this.threadKey = UtilConfig.EMPTY_STRING;
//		this.isAvailable = false;
		this.threadKeys = threadKeys;
	}

	public String getThreadKey()
	{
		return this.threadKey;
	}

	/*
	public boolean isAvailable()
	{
		return this.isAvailable;
	}
	*/
	
//	public Map<String, Boolean> getThreadKeys()
	public Set<String> getThreadKeys()
	{
		return this.threadKeys;
	}
}

