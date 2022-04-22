package org.greatfree.framework.threading.ttc.dt.master;

import java.io.IOException;
import java.util.Set;

import org.greatfree.concurrency.threading.Distributer;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 09/22/2019, Bing Li
class Master
{
	private Distributer master;

	private Master()
	{
	}
	
	private static Master instance = new Master();
	
	public static Master THREADING()
	{
		if (instance == null)
		{
			instance = new Master();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.master.killAll(timeout);
		this.master.shutdownSlave(timeout);
		this.master.stop(timeout);
	}

	public void start(ServerTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.master = new Distributer.DistributerBuilder()
			.name(ThreadConfig.MASTER)
			.port(ThreadConfig.THREAD_PORT)
			.task(task)
			.isMaster(true)
			.slaveName(ThreadConfig.SLAVE)
			.build();

//		this.master.startAsMaster(ThreadConfig.SLAVE, task);
		this.master.start();
	}
	
	public Set<String> obtainThreadKeys(int size) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.master.reuseThreads(size);
	}
	
	public void execute(String threadKey)
	{
		this.master.execute(threadKey);
	}
	
	public void assignTask(TaskNotification task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		this.master.assignTask(task);
	}
	
	public boolean isAlive(String threadKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.master.isAlive(threadKey);
	}
}
