package org.greatfree.dip.threading.message;

import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.dip.threading.TaskConfig;

// Created: 09/19/2019, Bing Li
public class MapNotification extends TaskNotification
{
	private static final long serialVersionUID = 2257077165883083713L;
	
	private String path;
	private long sleepTime;
	private int currentHop;
	private int maxHop;
//	private String finalRendezvousPoint;
	// CD represents the Concurrency Degree. 09/24/2019, Bing Li
	private int cd;
	private String mrSessionKey;
//	private boolean isRPChanged;

//	public MapNotification(String threadKey, String path, long sleepTime, int ch, int mh, String frp)
//	public MapNotification(String mrKey, String threadKey, String path, long sleepTime, int ch, int mh, int cd, boolean isRPChanged)
	public MapNotification(String mrKey, String threadKey, String path, long sleepTime, int ch, int mh, int cd)
	{
		super(threadKey, TaskConfig.MAP_TASK_KEY);
		this.path = path;
		this.sleepTime = sleepTime;
		this.currentHop = ch;
		this.maxHop = mh;
//		this.finalRendezvousPoint = frp;
		this.cd = cd;
		this.mrSessionKey = mrKey;
//		this.isRPChanged = isRPChanged;
	}
	
	public String getMRSessionKey()
	{
		return this.mrSessionKey;
	}

	public String getPath()
	{
		return this.path;
	}
	
	public long getSleepTime()
	{
		return this.sleepTime;
	}
	
	public int getCurrentHop()
	{
		return this.currentHop;
	}
	
	public int getMaxHop()
	{
		return this.maxHop;
	}

	/*
	public String getFinalRendezvousPoint()
	{
		return this.finalRendezvousPoint;
	}
	*/
	
	public int getCD()
	{
		return this.cd;
	}

	/*
	public boolean isRPChanged()
	{
		return this.isRPChanged;
	}
	*/
}
