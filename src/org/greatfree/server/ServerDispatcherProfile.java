package org.greatfree.server;

/**
 * 
 * @author libing
 * 
 *         07/19/2022
 *
 */
public class ServerDispatcherProfile
{
	private int serverThreadPoolSize;
	private long serverThreadKeepAliveTime;
	private int schedulerPoolSize;
	private long schedulerKeepAliveTime;
	
	public ServerDispatcherProfile(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		this.serverThreadPoolSize = serverThreadPoolSize;
		this.serverThreadKeepAliveTime = serverThreadKeepAliveTime;
		this.schedulerPoolSize = schedulerPoolSize;
		this.schedulerKeepAliveTime = schedulerKeepAliveTime;
	}

	public int getServerThreadPoolSize()
	{
		return this.serverThreadPoolSize;
	}

	public long getServerThreadKeepAliveTime()
	{
		return this.serverThreadKeepAliveTime;
	}

	public int getSchedulerPoolSize()
	{
		return this.schedulerPoolSize;
	}

	public long getSchedulerKeepAliveTime()
	{
		return this.schedulerKeepAliveTime;
	}
}
