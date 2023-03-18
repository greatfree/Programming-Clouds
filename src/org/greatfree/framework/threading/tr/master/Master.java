package org.greatfree.framework.threading.tr.master;

import java.io.IOException;
import java.util.Set;

import org.greatfree.concurrency.threading.Distributer;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.message.AddRequest;
import org.greatfree.framework.threading.message.AddResponse;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

/*
 * The TR/tr represents the thread reading. 09/28/2019, Bing Li
 */

// Created: 09/28/2019, Bing Li
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
	
	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.master.killAll(timeout);
		this.master.shutdownSlave(timeout);
		this.master.stop(timeout);
	}

	public void start(ServerTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.master = new Distributer.DistributerBuilder()
			.name(ThreadConfig.MASTER)
			.port(ThreadConfig.THREAD_PORT)
			.task(task)
			.slaveName(ThreadConfig.SLAVE)
			.isMaster(true)
			.build();

		this.master.start();
	}
	
	public Set<String> obtainThreadKeys(int size) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return this.master.reuseThreads(size);
	}
	
	public void execute(String threadKey)
	{
		this.master.execute(threadKey);
	}
	
	public AddResponse assignTask(AddRequest task, long timeout) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, ThreadAssignmentException
	{
		return (AddResponse)this.master.assignTask(task, timeout);
	}
	
	public boolean isAlive(String threadKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return this.master.isAlive(threadKey);
	}

}
