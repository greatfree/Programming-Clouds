package com.greatfree.remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.greatfree.concurrency.Collaborator;
import com.greatfree.concurrency.ThreadPool;

// The class acts as the listener to wait for a client's connection. To be more powerful, it involves a thread pool and the concurrency control mechanism. 07/30/2014, Bing Li

// Created: 07/17/2014, Bing Li
public class ServerListener extends ThreadPool
{
	// The TCP ServerSocket. 07/30/2014, Bing Li
	private ServerSocket serverSocket;
	// Since it is necessary to control the count of connected clients, the Collaborator is used. 07/30/2014, Bing Li
	private Collaborator collaborator;
	
	public ServerListener(ServerSocket serverSocket, int threadPoolSize, long keepAliveTime)
	{
		// Set parameters for the parent class, ThreadPool. 07/30/2014, Bing Li
		super(threadPoolSize, keepAliveTime);
		this.serverSocket = serverSocket;
		this.collaborator = new Collaborator();
	}

	/*
	 * Shutdown the listener and the associated thread pool. 07/30/2014, Bing Li
	 */
	public void shutdown()
	{
		this.collaborator.setShutdown();
		this.collaborator.signalAll();
		super.shutdown();
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
	public Collaborator getCollaborator()
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
		super.execute(thread);
	}
}
