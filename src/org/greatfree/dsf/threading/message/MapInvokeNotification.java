package org.greatfree.dsf.threading.message;

import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.dsf.threading.TaskConfig;

// Created: 10/02/2019, Bing Li
public class MapInvokeNotification extends TaskInvokeNotification
{
	private static final long serialVersionUID = -4143062732690255962L;
	
	private String path;
	private int currentHop;
	private int maxHop;
	private int cd;
	private String mrSessionKey;

	public MapInvokeNotification(String mrKey, String threadKey, String path, int ch, int mh, int cd)
	{
		super(threadKey, TaskConfig.MAP_TASK_KEY);
		this.path = path;
		this.currentHop = ch;
		this.maxHop = mh;
		this.cd = cd;
		this.mrSessionKey = mrKey;
	}
	
	public String getMRSessionKey()
	{
		return this.mrSessionKey;
	}

	public String getPath()
	{
		return this.path;
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
}
