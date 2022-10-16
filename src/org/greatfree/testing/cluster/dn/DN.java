package org.greatfree.testing.cluster.dn;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.admin.AdminConfig;
import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.client.SubClientPool;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.UtilConfig;

/*
 * The is an example to demonstrate the cluster. As a distributed node (DN) of the cluster, it not only receives notifications but also responds requests/responses in a multicasting manner. 11/22/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DN
{
	// The socket that waits for connections from other DNs and the coordinator. In this case, the DNs and the coordinator form a cluster such that it is possible to create a large scale distributed system. 11/24/2014, Bing Li
	private ServerSocket serverSocket;
	// The port to wait for connections. 11/24/2014, Bing Li
	private int serverPort;

	// The listener list contains all of the listeners to wait for connections. 11/24/2014, Bing Li
//	private List<Runner<DNListener, DNListenerDisposer>> listeners;
	private List<Runner<DNListener>> listeners;

	private DN()
	{
	}

	/*
	 * A singleton implementation since this is the unique entry and exit of the cluster. 11/24/2014, Bing Li
	 */
	private static DN instance = new DN();
	
	public static DN CLUSTER()
	{
		if (instance == null)
		{
			instance = new DN();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Start up the DN. 11/24/2014, Bing Li
	 */
	public void start(int serverPort)
	{
		// On JD7, the sorting algorithm is replaced with TimSort rather than MargeSort. To run correctly, it is necessary to use the old one. the following line sets that up. 11/23/2014, Bing Li
		System.setProperty(UtilConfig.MERGE_SORT, UtilConfig.TRUE);

		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Assign the DN port. 11/24/2014, Bing Li
		this.serverPort = serverPort;
		try
		{
			// Initialize the socket of the DN. 11/24/2014, Bing Li
			this.serverSocket = new ServerSocket(this.serverPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize a list to contain the listeners. 11/24/2014, Bing Li
//		this.listeners = new ArrayList<Runner<DNListener, DNListenerDisposer>>();
		this.listeners = new ArrayList<Runner<DNListener>>();
		// Initialize a disposer that collects the listeners. 11/24/2014, Bing Li
//		DNListenerDisposer disposer = new DNListenerDisposer();
		// The runner contains the listener and listener disposer to start the listeners concurrently. 11/24/2014, Bing Li
//		Runner<DNListener, DNListenerDisposer> runner;
		Runner<DNListener> runner;
		// Initialize and start a certain number of listeners concurrently. 11/24/2014, Bing Li
		for (int i = 0; i < ServerConfig.MAX_CLIENT_LISTEN_THREAD_COUNT; i++)
		{
			// Initialize the runner that contains the listener and its disposer. 11/24/2014, Bing Li
//			runner = new Runner<DNListener, DNListenerDisposer>(new DNListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), disposer, true);
			runner = new Runner<DNListener>(new DNListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), true);
			// Put the runner into a list for management. 11/24/2014, Bing Li
			this.listeners.add(runner);
			// Start up the runner. 11/24/2014, Bing Li
			runner.start();
		}
		
		ServerStatus.FREE().addServerIDs(AdminConfig.getClusterIDs());

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the DN server IO registry. 11/24/2014, Bing Li
		DNIORegistry.REGISTRY().init();
		// Initialize a client pool, which is used by the server to connect to the remote end. 09/17/2014, Bing Li
		ClientPoolSingleton.SERVER().init();
		// Initialize the sub client pool that is used to connect the children of the local DN. 11/27/2014, Bing Li
		SubClientPool.SERVER().init();
		// Initialize the DN eventer that sends notifications to the coordinator. 11/24/2014, Bing Li
		DNEventer.NOTIFY().init(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_DN_PORT);
		
		// Initialize the message producer which dispatches received requests and notifications from the coordinator. 11/24/2014, Bing Li
		DNMessageProducer.CLUSTER().init();

		// Initialize the DN multicastor. 12/01/2014, Bing Li
		DNMulticastor.CLUSTER().init();

		try
		{
			// Notify the coordinator that the DN is online. 11/24/2014, Bing Li
			DNEventer.NOTIFY().notifyOnline();
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}

	/*
	 * Stop the DN. 11/24/2014, Bing Li
	 */
	public void stop() throws InterruptedException, IOException, ClassNotFoundException
	{
		// Set the terminating signal. The long time running task needs to be interrupted when the signal is set. 11/24/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		
		// Stop each listener one by one. 11/24/2014, Bing Li
//		for (Runner<DNListener, DNListenerDisposer> runner : this.listeners)
		for (Runner<DNListener> runner : this.listeners)
		{
			runner.stop(ClientConfig.TIME_TO_WAIT_FOR_THREAD_TO_DIE);
		}

		// Close the socket of the DN server. 11/24/2014, Bing Li
		this.serverSocket.close();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();
		
		// Dispose the message producer. 11/23/2014, Bing Li
		DNMessageProducer.CLUSTER().dispose();
		
		// Unregister the DN eventer. 11/24/2014, Bing Li
		DNEventer.NOTIFY().unregister();
		// Dispose the DN eventer. 11/24/2014, Bing Li
		DNEventer.NOTIFY().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		
		// Shutdown the DN server IOs. 11/24/2014, Bing Li
		DNIORegistry.REGISTRY().dispose();
		
		// Dispose the client pool. 11/24/2014, Bing Li
		ClientPoolSingleton.SERVER().dispose();
		// Dispose the sub client pool. 11/27/2014, Bing Li
		SubClientPool.SERVER().dispose();
		// Dispose the DN multicastor. 12/01/2014, Bing Li
		DNMulticastor.CLUSTER().dispose();

		// Dispose the shared thread pool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
	}
}
