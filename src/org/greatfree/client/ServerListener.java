package org.greatfree.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.util.UtilConfig;

// The class acts as the listener to wait for a client's connection. To be more powerful, it involves a thread pool and the concurrency control mechanism. 07/30/2014, Bing Li

// Created: 07/17/2014, Bing Li
//public class ServerListener extends ThreadPool
// public abstract class ServerListener implements Runnable
public abstract class ServerListener extends RunnerTask
{
	// The TCP ServerSocket. 07/30/2014, Bing Li
	private ServerSocket serverSocket;
	// Since it is necessary to control the count of connected clients, the Collaborator is used. 07/30/2014, Bing Li
	private Sync collaborator;
	// A thread pool that is responsible for executing an instance of ServerIO concurrently. 02/27/2016, Bing Li
	private ThreadPool pool;
	
//	public ServerListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	public ServerListener(ServerSocket serverSocket, ThreadPool pool)
	{
		// Set parameters for the parent class, ThreadPool. 07/30/2014, Bing Li
//		super(threadPoolSize, keepAliveTime);
		this.serverSocket = serverSocket;
		this.collaborator = new Sync();
		this.pool = pool;
	}

	/*
	 * Shutdown the listener and the associated thread pool. 07/30/2014, Bing Li
	 */
	@Override
//	public void shutdown()
	public void dispose() throws InterruptedException
	{
//		this.collaborator.setShutdown();
//		this.collaborator.signalAll();

		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();
//		super.shutdown();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();
	}
	
	@Override
	public int getWorkload()
	{
		return UtilConfig.ZERO;
	}

	/*
	 * Wait for connections. 07/30/2014, Bing Li
	 */
	public Socket accept() throws IOException
	{
		return this.serverSocket.accept();
	}

	/*
	 * Check whether the listener is shutdown. 07/30/2014, Bing Li
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}

	/*
	 * Expose the collaborator. 07/30/2014, Bing Li
	 */
	public Sync getCollaborator()
	{
		return this.collaborator;
	}

	/*
	 * Wait for the available lock. 07/30/2014, Bing Li
	 */
	public void holdOn() throws InterruptedException
	{
		this.collaborator.holdOn();
	}

	/*
	 * Execute a thread. Usually, the thread is a ServerMessagePipe. It is possible some other tasks that need to be executed concurrently, such as connecting to the remote server for an eventing local server. 07/30/2014, Bing Li
	 */
	public void execute(Runnable thread)
	{
//		super.execute(thread);
		this.pool.execute(thread);
	}
	
	public int getActiveCount()
	{
		return this.pool.getActiveCount();
	}
}
