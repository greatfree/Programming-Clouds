package org.greatfree.dsf.threading.message;

import org.greatfree.message.container.Notification;

// Created: 09/19/2019, Bing Li
public class MRFinalNotification extends Notification
{
	private static final long serialVersionUID = -8132556059958959188L;
	
	private String path;
	private int currentHop;
	private int maxHop;
	private int cd;
	private String mrKey;
	private boolean isDone;

	public MRFinalNotification(String path, int currentHop, int maxHop, int cd, String mrKey, boolean isDone)
	{
		super(TaskApplicationID.MR_FINAL_NOTIFICATION);
		this.path = path;
		this.currentHop = currentHop;
		this.maxHop = maxHop;
		this.cd = cd;
		this.mrKey = mrKey;
		this.isDone = isDone;
	}

	/*
	public MRFinalNotification(String path, int maxHop, int cd, String mrKey)
	{
		super(TaskApplicationID.MR_FINAL_NOTIFICATION);
		this.path = path;
//		this.currentHop = currentHop;
//		this.maxHop = maxHop;
		this.currentHop = maxHop;
		this.maxHop = maxHop;
		this.cd = cd;
		this.mrKey = mrKey;
	}
	*/

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
	
	public String getMRKey()
	{
		return this.mrKey;
	}
	
	public boolean isDone()
	{
		return this.isDone;
	}
}
