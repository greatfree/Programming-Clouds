package org.greatfree.framework.threading.tr.slave;

import java.io.IOException;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.threading.Distributer;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.AddTask;
import org.greatfree.util.TerminateSignal;

/*
 * The TR/tr represents the thread reading. 09/28/2019, Bing Li
 */

// Created: 09/28/2019, Bing Li
class Slave
{
	private Distributer slave;

	private Slave()
	{
	}
	
	private static Slave instance = new Slave();
	
	public static Slave SLAVE()
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
	
	public void stop(long timeout) throws InterruptedException, ClassNotFoundException, IOException, RemoteReadException
	{
//		TerminateSignal.SIGNAL().setTerminated();
		Scheduler.PERIOD().shutdown(timeout);
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.slave.stop(timeout);
	}

	public void start() throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.slave = new Distributer.DistributerBuilder()
			.name(ThreadConfig.SLAVE)
			.port(ThreadConfig.THREAD_PORT)
			.masterName(ThreadConfig.MASTER)
			.scheduler(Scheduler.PERIOD().getScheduler())
			.isMaster(false)
			.build();

		this.slave.start();
		this.slave.addTask(new AddTask());
	}
}

