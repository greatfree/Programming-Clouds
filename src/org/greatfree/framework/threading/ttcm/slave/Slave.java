package org.greatfree.framework.threading.ttcm.slave;

import java.io.IOException;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.threading.Distributer;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.threading.PingTask;
import org.greatfree.framework.threading.PongTask;
import org.greatfree.util.TerminateSignal;

// Created: 09/13/2019, Bing Li
class Slave
{
//	private ActorServer slave;
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
	
	public void stop(long timeout) throws InterruptedException, ClassNotFoundException, IOException, RemoteReadException, RemoteIPNotExistedException
	{
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		Scheduler.PERIOD().shutdown(timeout);
		this.slave.stop(timeout);
	}

	public void start() throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.slave = new Distributer.DistributerBuilder()
			.name(ThreadConfig.SLAVE)
			.port(ThreadConfig.THREAD_PORT)
			.masterName(ThreadConfig.MASTER)
//			.registryIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
//			.registryPort(RegistryConfig.PEER_REGISTRY_PORT)
			.scheduler(Scheduler.PERIOD().getScheduler())
			.isMaster(false)
			.build();

//		this.slave.startAsSlave();
		this.slave.start();
		
		this.slave.addTask(new PingTask());
		this.slave.addTask(new PongTask());
	}
}

