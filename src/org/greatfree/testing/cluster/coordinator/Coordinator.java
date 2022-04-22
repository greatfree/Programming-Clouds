package org.greatfree.testing.cluster.coordinator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.admin.AdminConfig;
import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.cluster.coordinator.admin.AdminIORegistry;
import org.greatfree.testing.cluster.coordinator.admin.AdminListener;
import org.greatfree.testing.cluster.coordinator.admin.AdminMulticastor;
import org.greatfree.testing.cluster.coordinator.client.ClientIORegistry;
import org.greatfree.testing.cluster.coordinator.client.ClientListener;
import org.greatfree.testing.cluster.coordinator.dn.DNIORegistry;
import org.greatfree.testing.cluster.coordinator.dn.DNListener;
import org.greatfree.testing.cluster.coordinator.dn.DNServerClientPool;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

/*
 * This is a sample of the coordinator which is responsible for managing all of the distributed nodes in the form of a cluster. 11/19/2016, Bing Li
 * 
 * The class is responsible for starting up and shutting down the coordinator. Since it is the unique entry and exit of the coordinator, it is implemented in the form of a singleton. 11/19/2016, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class Coordinator
{
	// The ServerSocket waits for clients' connecting. 11/19/2016, Bing Li
	private ServerSocket clientSocket;
	// The port number for the client socket. 11/19/2016, Bing Li
	private int clientPort;
	
	// The ServerSocket waits for DN servers' connecting. The socket serves the coordinator in the sense that it not only responds to client servers' requests but also notifies client servers. 11/19/2016, Bing Li
	private ServerSocket dnSocket;
	// The port number for the cluster socket. 11/19/2016, Bing Li
	private int dnPort;
	
	// The ServerSocket waits for the administrator's connecting. 11/19/2016, Bing Li
	private ServerSocket adminServerSocket;
	// The port number for the administration socket. 11/19/2016, Bing Li
	private int adminPort;

	// The list keeps all of the threads that listen to connecting from clients of the cluster. When the coordinator is shutdown, those threads can be killed to avoid possible missing. 11/25/2014, Bing Li
//	private List<Runner<ClientListener, ClientListenerDisposer>> clientListenerRunnerList;
	private List<Runner<ClientListener>> clientListenerRunnerList;
	
	// The list keeps all of the threads that listen to connecting from DN servers of the cluster. When the coordinator is shutdown, those threads can be killed to avoid possible missing. 11/28/2014, Bing Li
//	private List<Runner<DNListener, DNListenerDisposer>> dnListenerRunnerList;
	private List<Runner<DNListener>> dnListenerRunnerList;

	// Declare one runner for the administration. Since the load is lower, it is not necessary to initialize multiple threads to listen to potential connections. 11/29/2014, Bing Li
//	private Runner<AdminListener, AdminListenerDisposer> adminListenerRunner;
	private Runner<AdminListener> adminListenerRunner;
	
	/*
	 * A singleton is designed for the coordinator's startup and shutdown interface. 11/25/2014, Bing Li
	 */
	private Coordinator()
	{
	}
	
	private static Coordinator instance = new Coordinator();
	
	public static Coordinator COORDINATOR()
	{
		if (instance == null)
		{
			instance = new Coordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Start the coordinator and relevant listeners with concurrent threads for potential busy connecting. 11/25/2014, Bing Li
	 */
	public void start(int clientPort, int dnPort, int adminPort)
	{
		// Initialize the shared thread pool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().init(ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME);

		// Initialize and start the crawling socket. 11/25/2014, Bing Li
		this.clientPort = clientPort;
		try
		{
			this.clientSocket = new ServerSocket(this.clientPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize and start the memory socket. 11/28/2014, Bing Li
		this.dnPort = dnPort;
		try
		{
			this.dnSocket = new ServerSocket(this.dnPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize and start administration socket. 11/29/2014, Bing Li
		this.adminPort = adminPort;
		try
		{
			this.adminServerSocket = new ServerSocket(this.adminPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize a disposer which collects the client listener. 11/25/2014, Bing Li
//		ClientListenerDisposer clientDisposer = new ClientListenerDisposer();
		// Initialize the runner list. 11/25/2014, Bing Li
//		this.clientListenerRunnerList = new ArrayList<Runner<ClientListener, ClientListenerDisposer>>();
		this.clientListenerRunnerList = new ArrayList<Runner<ClientListener>>();
		// Start up the threads to listen to connecting from clients which send and receive messages from the coordinator. 11/25/2014, Bing Li
//		Runner<ClientListener, ClientListenerDisposer> clientRunner;
		Runner<ClientListener> clientRunner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
//			clientRunner = new Runner<ClientListener, ClientListenerDisposer>(new ClientListener(this.clientSocket, SharedThreadPool.SHARED().getPool()), clientDisposer, true);
			clientRunner = new Runner<ClientListener>(new ClientListener(this.clientSocket, SharedThreadPool.SHARED().getPool()), true);
			this.clientListenerRunnerList.add(clientRunner);
			clientRunner.start();
		}
		
		// Initialize a disposer which collects the DN listener. 11/28/2014, Bing Li
//		DNListenerDisposer dnDisposer = new DNListenerDisposer();
		// Initialize the runner list. 11/28/2014, Bing Li
//		this.dnListenerRunnerList = new ArrayList<Runner<DNListener, DNListenerDisposer>>();
		this.dnListenerRunnerList = new ArrayList<Runner<DNListener>>();
		// Start up the threads to listen to connecting from DN servers which send and receive messages from the coordinator. 11/28/2014, Bing Li
//		Runner<DNListener, DNListenerDisposer> dnRunner;
		Runner<DNListener> dnRunner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
//			dnRunner = new Runner<DNListener, DNListenerDisposer>(new DNListener(this.dnSocket, SharedThreadPool.SHARED().getPool()), dnDisposer, true);
			dnRunner = new Runner<DNListener>(new DNListener(this.dnSocket, SharedThreadPool.SHARED().getPool()), true);
			this.dnListenerRunnerList.add(dnRunner);
			dnRunner.start();
		}

		// Initialize a disposer which collects the administration listener. 11/29/2014, Bing Li
//		AdminListenerDisposer adminDisposer = new AdminListenerDisposer();
		// Initialize the runner to listen to connecting from the administrator which sends and notifications to the coordinator. 11/29/2014, Bing Li
//		this.adminListenerRunner = new Runner<AdminListener, AdminListenerDisposer>(new AdminListener(this.adminServerSocket, SharedThreadPool.SHARED().getPool()), adminDisposer, true);
		this.adminListenerRunner = new Runner<AdminListener>(new AdminListener(this.adminServerSocket, SharedThreadPool.SHARED().getPool()), true);
		// Start up the runner. 11/29/2014, Bing Li
		this.adminListenerRunner.start();

		// Initialize the ServerStatus to keep the nodes' status in the distributed system. 02/06/2016, Bing Li
		ServerStatus.FREE().addServerIDs(AdminConfig.getClusterIDs());

		// Initialize the profile. 11/25/2014, Bing Li
		Profile.CONFIG().init();
		
		// Initialize the scheduler to do something periodically. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		// Initialize the client IO registry. 11/25/2014, Bing Li
		ClientIORegistry.REGISTRY().init();
		// Initialize a client pool, which is used by the coordinator to connect to the clients. 11/25/2014, Bing Li
		ClientPoolSingleton.SERVER().init();

		// Initialize the DN IO registry. 11/28/2014, Bing Li
		DNIORegistry.REGISTRY().init();
		// Initialize a DN client pool, which is used by the coordinator to connect to the remote DN server. 11/25/2014, Bing Li
		DNServerClientPool.COORDINATE().init();
		
		// Initialize the administration IO registry. 11/29/2014, Bing Li
		AdminIORegistry.REGISTRY().init();

		// Initialize the notification multicastor for the DN servers. 11/27/2014, Bing Li
		CoordinatorMulticastNotifier.COORDINATE().init();

		// Initialize the request/response multicastor for the DN servers. 11/27/2014, Bing Li
		CoordinatorMulticastReader.COORDINATE().init();
		
		// Initialize the administration multicastor. 11/27/2014, Bing Li
		AdminMulticastor.ADMIN().init();

		// Initialize the message producer to process incoming notifications and requests. 11/29/2015, Bing Li
		CoordinatorMessageProducer.SERVER().init();
	}
	
	/*
	 * Shutdown the coordinator. 11/25/2014, Bing Li
	 */
	public void stop() throws IOException, InterruptedException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		// Close the sockets for the coordinator. 11/25/2014, Bing Li
		this.clientSocket.close();
		this.dnSocket.close();
		this.adminServerSocket.close();

		// Stop all of the threads that listen to clients' connecting to the coordinator. 11/25/2014, Bing Li
//		for (Runner<ClientListener, ClientListenerDisposer> runner : this.clientListenerRunnerList)
		for (Runner<ClientListener> runner : this.clientListenerRunnerList)
		{
			runner.stop();
		}

		// Stop all of the threads that listen to DNs' connecting to the coordinator. 11/25/2014, Bing Li
//		for (Runner<DNListener, DNListenerDisposer> runner : this.dnListenerRunnerList)
		for (Runner<DNListener> runner : this.dnListenerRunnerList)
		{
			runner.stop();
		}
		
		// Stop the administration runner. 11/29/2014, Bing Li
		this.adminListenerRunner.stop();

		// Shutdown the message producer. 11/29/2015, Bing Li
		CoordinatorMessageProducer.SERVER().dispose();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();
		
		// Dispose the profile. 11/25/2014, Bing Li
		Profile.CONFIG().dispose();

		// Shutdown the IO registry. 11/25/2014, Bing Li
		DNIORegistry.REGISTRY().dispose();

		// Shutdown the client pool. 11/25/2014, Bing Li
		DNServerClientPool.COORDINATE().dispose();
		
		// Shutdown the IO registry. 11/25/2014, Bing Li
		ClientIORegistry.REGISTRY().dispose();
		
		// Shutdown the client pool. 11/25/2014, Bing Li
		ClientPoolSingleton.SERVER().dispose();

		// Shutdown the IO registry. 11/29/2014, Bing Li
		AdminIORegistry.REGISTRY().dispose();
		
		// Dispose the administration multicastor. 11/27/2014, Bing Li
		AdminMulticastor.ADMIN().dispose();
	
		// Dispose the multicast notifiers. 11/29/2014, Bing Li
		CoordinatorMulticastNotifier.COORDINATE().dispose();
		
		// Dispose the multicast readers. 11/29/2014, Bing Li
		CoordinatorMulticastReader.COORDINATE().dispose();
		
		// Dispose the shared thread pool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().dispose(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
}
