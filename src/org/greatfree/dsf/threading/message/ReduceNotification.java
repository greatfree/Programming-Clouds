package org.greatfree.dsf.threading.message;

import java.util.Set;

import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.dsf.threading.TaskConfig;

// Created: 09/19/2019, Bing Li
public class ReduceNotification extends TaskNotification
{
	private static final long serialVersionUID = -4390055296144045986L;
	
//	private String path;
	
	// The simulated time consumed by one task. 01/08/2020, Bing Li
	private long sleepTime;
	// The RP slave key. 01/08/2020, Bing Li
	private String rpSlaveKey;
	// The RP thread key on the RP slave. 01/08/2020, Bing Li
	private String rpThreadKey;
	// The current hops accomplished in one MR game. 01/08/2020, Bing Li
	private int currentHop;
	// The predefined maximum hop, which is used to check whether it is time to finish the MR game. 01/08/2020, Bing Li
	private int maxHop;
	// The final RP is the master such that it is not necessary to keep it in the message. 09/24/2019, Bing Li
//	private String finalRP;
	// CD represents the Concurrency Degree. 09/24/2019, Bing Li
	private int cd;
	// The key of one MR game. It is used to identify it from multiple MR games. 01/08/2020, Bing Li
	private String mrSessionKey;
//	private boolean isRPChanged;

//	public ReduceNotification(String threadKey, String path, long sleepTime, String rendezvousPoint, int ch, int mh, String frp)
	/*
	public ReduceNotification(String mrKey, String threadKey, String path, long sleepTime, String rpSlaveKey, String rpThreadKey, int ch, int mh, int cd)
	{
		super(threadKey, TaskConfig.REDUCE_TASK_KEY);
		this.path = path;
		this.sleepTime = sleepTime;
		this.rpSlaveKey = rpSlaveKey;
		this.rpThreadKey = rpThreadKey;
		this.currentHop = ch;
		this.maxHop = mh;
		// The final RP is the master such that it is not necessary to keep it in the message. 09/24/2019, Bing Li
//		this.finalRP = frp;
		this.cd = cd;
//		this.mrKey = super.getInstructKey()
		this.mrKey = mrKey;
	}
	*/

//	public ReduceNotification(String mrKey, Set<String> threadKeys, String path, long sleepTime, String rpSlaveKey, String rpThreadKey, int ch, int mh, int cd, boolean isRPChanged)
	public ReduceNotification(String mrKey, Set<String> threadKeys, long sleepTime, String rpSlaveKey, String rpThreadKey, int ch, int mh, int cd)
	{
		super(threadKeys, TaskConfig.REDUCE_TASK_KEY);
//		this.path = path;
		this.sleepTime = sleepTime;
		this.rpSlaveKey = rpSlaveKey;
		this.rpThreadKey = rpThreadKey;
		this.currentHop = ch;
		this.maxHop = mh;
//		System.out.println("ReduceNotification-init(): maxHop = " + this.maxHop);
		// The final RP is the master such that it is not necessary to keep it in the message. 09/24/2019, Bing Li
//		this.finalRP = frp;
		this.cd = cd;
//		this.mrKey = super.getInstructKey()
		this.mrSessionKey = mrKey;
//		this.isRPChanged = isRPChanged;
	}

	/*
	public String getPath()
	{
		return this.path;
	}
	*/
	
	public long getSleepTime()
	{
		return this.sleepTime;
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

	/*
	 * The final RP is the master such that it is not necessary to keep it in the message. 09/24/2019, Bing Li
	 * 
	public String getFinalRP()
	{
		return this.finalRP;
	}
	*/
	
	public int getCD()
	{
		return this.cd;
	}
	
	public String getMRSessionKey()
	{
		return this.mrSessionKey;
	}

	/*
	public boolean isRPChanged()
	{
		return this.isRPChanged;
	}
	*/
}
