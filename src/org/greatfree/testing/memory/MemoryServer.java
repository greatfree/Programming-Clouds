package org.greatfree.testing.memory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.UtilConfig;

/*
 * This is the server to save a large amount of data into its storage system. It is one node to take one portion of the data. In the sample, the data is saved in the memory for fast accessing. Developers can follow it to keep a large amount of data either in a persistent or in a volatile way. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryServer
{
	private ServerSocket serverSocket;
	private int serverPort;
	
//	private List<Runner<MemServerListener, MemServerListenerDisposer>> listeners;
	private List<Runner<MemServerListener>> listeners;
	
	private MemoryServer()
	{
	}

	/*
	 * A singleton implementation since this is the unique entry and exit of the crawler. 11/24/2014, Bing Li
	 */
	private static MemoryServer instance = new MemoryServer();
	
	public static MemoryServer STORE()
	{
		if (instance == null)
		{
			instance = new MemoryServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void start(int port)
	{
		// On JD7, the sorting algorithm is replaced with TimSort rather than MargeSort. To run correctly, it is necessary to use the old one. the following line sets that up. 11/28/2014, Bing Li
		System.setProperty(UtilConfig.MERGE_SORT, UtilConfig.TRUE);
		
		SharedThreadPool.SHARED().init(MemConfig.MEMORY_LISTENER_THREAD_POOL_SIZE, MemConfig.MEMORY_LISTENER_THREAD_ALIVE_TIME);

		// Assign the memory server port. 11/28/2014, Bing Li
		this.serverPort = port;
		try
		{
			// Initialize the socket of the crawler. 11/28/2014, Bing Li
			this.serverSocket = new ServerSocket(this.serverPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize a list to contain the listeners. 11/28/2014, Bing Li
//		this.listeners = new ArrayList<Runner<MemServerListener, MemServerListenerDisposer>>();
		this.listeners = new ArrayList<Runner<MemServerListener>>();
		// Initialize a disposer that collects the listeners. 11/28/2014, Bing Li
//		MemServerListenerDisposer disposer = new MemServerListenerDisposer();
		// The runner contains the listener and listener disposer to start the listeners concurrently. 11/28/2014, Bing Li
//		Runner<MemServerListener, MemServerListenerDisposer> runner;
		Runner<MemServerListener> runner;
		// Initialize and start a certain number of listeners concurrently. 11/28/2014, Bing Li
		for (int i = 0; i < ServerConfig.MAX_CLIENT_LISTEN_THREAD_COUNT; i++)
		{
			// Initialize the runner that contains the listener and its disposer. 11/24/2014, Bing Li
//			runner = new Runner<MemServerListener, MemServerListenerDisposer>(new MemServerListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), disposer, true);
			runner = new Runner<MemServerListener>(new MemServerListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), true);
			// Put the runner into a list for management. 11/24/2014, Bing Li
			this.listeners.add(runner);
			// Start up the runner. 11/24/2014, Bing Li
			runner.start();
		}

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the memory server IO registry. 11/28/2014, Bing Li
		MemoryIORegistry.REGISTRY().init();
		// Initialize the client pool that is used to connect the coordinator. 11/28/2014, Bing Li
		ClientPool.STORE().init();
		// Initialize the sub client pool that is used to connect the children of the memory server. 11/28/2014, Bing Li
		SubClientPool.STORE().init();
		// Initialize the memory eventer that sends notifications to the coordinator. For example, the crawled links are sent to the coordinator asynchronously by the eventer. 11/28/2014, Bing Li
		MemoryEventer.NOTIFY().init(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_MEMORY);
		// Initialize the message producer which dispatches received requests and notifications from the coordinator. 11/28/2014, Bing Li
		MemoryMessageProducer.STORE().Init();

		// Initialize the memory. 11/28/2014, Bing Li
		LinkPond.STORE().init();

		// Start the multicastor. 11/29/2014, Bing Li
		MemoryMulticastor.STORE().init();
		
		try
		{
			// Notify the coordinator that the memory server is online. 11/28/2014, Bing Li
			MemoryEventer.NOTIFY().notifyOnline();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Stop the crawler. 11/28/2014, Bing Li
	 */
	public void stop() throws InterruptedException, IOException, ClassNotFoundException
	{
		// Set the terminating signal. The long time running task needs to be interrupted when the signal is set. 11/28/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		// Stop each listener one by one. 11/28/2014, Bing Li
//		for (Runner<MemServerListener, MemServerListenerDisposer> runner : this.listeners)
		for (Runner<MemServerListener> runner : this.listeners)
		{
			runner.stop(ClientConfig.TIME_TO_WAIT_FOR_THREAD_TO_DIE);
		}

		// Close the socket of the memory server. 11/28/2014, Bing Li
		this.serverSocket.close();

		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Dispose the memory. 11/28/2014, Bing Li
		LinkPond.STORE().dispose();
		
		// Unregister the memory eventer. 11/28/2014, Bing Li
		MemoryEventer.NOTIFY().unregister();
		// Dispose the memory eventer. 11/28/2014, Bing Li
		MemoryEventer.NOTIFY().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		
		// Dispose the message producer. 11/28/2014, Bing Li
		MemoryMessageProducer.STORE().dispose();
		// Shutdown the memory server IOs. 11/28/2014, Bing Li
		MemoryIORegistry.REGISTRY().dispose();
		// Dispose the client pool. 11/28/2014, Bing Li
		ClientPool.STORE().dispose();
		// Dispose the sub client pool/ 11/28/2014, Bing Li
		SubClientPool.STORE().dispose();
		// Shutdown the multicastor. 11/29/2014, Bing Li
		MemoryMulticastor.STORE().dispose();
		
		// Dispose the shared thread pool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
	}
}
