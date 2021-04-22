package org.greatfree.framework.threading.message;

import java.util.Set;

import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.framework.threading.TaskConfig;

// Created: 10/02/2019, Bing Li
public class ReduceInvokeNotification extends TaskInvokeNotification
{
	private static final long serialVersionUID = 8183470595790986627L;
	
	private String rpSlaveKey;
	private String rpThreadKey;
	private int currentHop;
	private int maxHop;
	private int cd;
	private String mrSessionKey;

	public ReduceInvokeNotification(String mrKey, Set<String> threadKeys, String rpSlaveKey, String rpThreadKey, int ch, int mh, int cd)
	{
		super(threadKeys, TaskConfig.REDUCE_TASK_KEY);
		this.rpSlaveKey = rpSlaveKey;
		this.rpThreadKey = rpThreadKey;
		this.currentHop = ch;
		this.maxHop = mh;
		this.cd = cd;
		this.mrSessionKey = mrKey;
	}
	
	public String getRPSlaveKey()
	{
		return this.rpSlaveKey;
	}
	
	public String getRPThreadKey()
	{
		return this.rpThreadKey;
	}
	
	public int getCurrentHop()
	{
		return this.currentHop;
	}
	
	public int getMaxHop()
	{
		return this.maxHop;
	}
	
	public int getCD()
	{
		return this.cd;
	}
	
	public String getMRSessionKey()
	{
		return this.mrSessionKey;
	}
}
