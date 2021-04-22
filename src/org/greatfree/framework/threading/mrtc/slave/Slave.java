package org.greatfree.framework.threading.mrtc.slave;

import java.io.IOException;
import java.util.Map;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.threading.Distributer;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.mrtc.NodeIDs;
import org.greatfree.message.container.Notification;
import org.greatfree.util.IPPort;

// Created: 09/22/2019, Bing Li
class Slave
{
	private Distributer dt;

	private Slave()
	{
	}
	
	private static Slave instance = new Slave();
	
	public static Slave THREADING()
	{
		if (instance == null)
		{
			instance = new Slave();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Stop the slave. 01/08/2020, Bing Li
	 */
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		Scheduler.GREATFREE().shutdown(timeout);
		this.dt.stop(timeout);
	}

	/*
	 * Start the slave. 01/08/2020, Bing Li
	 */
	public void start() throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		this.dt = new Distributer.DistributerBuilder()
				.name(NodeIDs.ID().getLocalName())
				.port(ThreadConfig.THREAD_PORT)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.isMaster(false)
				.task(new MRSlaveTask())
				.build();
//		this.dt.start(NodeIDs.ID().getLocalKey(), new MRSlaveTask(), false);
//		this.dt.start(new MRSlaveTask(), false);
		this.dt.start();
	}

	/*
	 * Set the master name. 01/08/2020, Bing Li
	 */
	public void setMasterName(String name)
	{
		this.dt.setMasterName(name);
	}

	/*
	 * Get the master name. 01/08/2020, Bing Li
	 */
	public String getMasterName()
	{
		return this.dt.getMasterName();
	}
	
	/*
	 * Add the IP addresses of specified slaves. 01/08/2020, Bing Li
	 */
	public void addIPs(Map<String, IPPort> ips)
	{
		for (Map.Entry<String, IPPort> entry : ips.entrySet())
		{
			this.dt.addSlaveIPs(entry.getKey(), entry.getValue());
		}
	}

	/*
	 * Set the IP address of the master. 01/08/2020, Bing Li
	 */
	public void setMasterIP(IPPort ip)
	{
		this.dt.setMasterIP(ip);
	}

	/*
	 * Add one task locally to process as a slave. 01/08/2020, Bing Li
	 */
	public void addTask(ThreadTask task)
	{
		this.dt.addTask(task);
	}

	/*
	 * Detect whether one thread on a slave is alive. 01/08/2020, Bing Li
	 */
	public boolean isAlive(String slaveKey, String threadKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.dt.isAlive(slaveKey, threadKey);
	}

	/*
	 * Execute one thread on one slave. 01/08/2020, Bing Li
	 */
	public void execute(String slaveKey, String threadKey)
	{
		this.dt.execute(slaveKey, threadKey);
	}

	/*
	 * Assign tasks to one slave. 01/08/2020, Bing Li
	 */
	public void assignTask(String slaveKey, TaskNotification task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		this.dt.assignTask(slaveKey, task);
	}

	/*
	 * Notify the master synchronously. 01/08/2020, Bing Li
	 */
	public void syncNotifyMaster(Notification notification) throws IOException, InterruptedException
	{
		this.dt.syncNotifyMaster(notification);
	}

	/*
	 * Notify the master asynchronously. 01/08/2020, Bing Li
	 */
	public void asyncNotifyMaster(Notification notification) throws IOException, InterruptedException
	{
		this.dt.asyncNotifyMaster(notification);
	}
	
}
