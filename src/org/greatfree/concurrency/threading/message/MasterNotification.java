package org.greatfree.concurrency.threading.message;

import org.greatfree.message.container.Notification;

// Created: 10/05/2019, Bing Li
public class MasterNotification extends Notification
{
	private static final long serialVersionUID = -8689041464950263959L;
	
	private String masterName;
	private String masterKey;
	private String masterIP;
	private int masterPort;

	public MasterNotification(String masterName, String masterKey, String masterIP, int masterPort)
	{
		super(ATMMessageType.MASTER_NOTIFICATION);
		this.masterName = masterName;
		this.masterKey = masterKey;
		this.masterIP = masterIP;
		this.masterPort = masterPort;
	}

	public String getMasterName()
	{
		return this.masterName;
	}
	
	public String getMasterKey()
	{
		return this.masterKey;
	}
	
	public String getMasterIP()
	{
		return this.masterIP;
	}
	
	public int getMasterPort()
	{
		return this.masterPort;
	}
}
