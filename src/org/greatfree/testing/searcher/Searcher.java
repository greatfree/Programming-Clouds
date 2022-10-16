package org.greatfree.testing.searcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.client.RemoteReader;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.client.ClientEventer;
import org.greatfree.testing.client.ClientListener;
import org.greatfree.testing.client.ClientPool;
import org.greatfree.testing.client.ClientServerIORegistry;
import org.greatfree.testing.client.ClientServerMessageProducer;
import org.greatfree.util.UtilConfig;

/*
 * The class intends to illustrate the sample code for a user end. Through it, a user sends search requests to the coordinator. And then, the coordinator searches within its clusters via anycast or broadcast and responds to the user. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class Searcher
{
	// A socket that waits for connections from remote nodes. In this case, it is used to wait for connections from the coordinator. 11/29/2014, Bing Li
	private ServerSocket serverSocket;
	// The port that is open to the coordinator. 11/29/2014, Bing Li
	private int port;
	// Multiple threads waiting for remote connections. 11/29/2014, Bing Li
//	private List<Runner<ClientListener, ClientListenerDisposer>> listenerRunners;
	private List<Runner<ClientListener>> listenerRunners;
	
	private Searcher()
	{
	}
	/*
	 * A singleton definition. 11/23/2014, Bing Li
	 */
	private static Searcher instance = new Searcher();
	
	public static Searcher CLIENT()
	{
		if (instance == null)
		{
			instance = new Searcher();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Start the client. 11/23/2014, Bing Li
	 */
	public void start(int port)
	{
		// On JD7, the sorting algorithm is replaced with TimSort rather than MargeSort. To run correctly, it is necessary to use the old one. the following line sets that up. 10/03/2014, Bing Li
		System.setProperty(UtilConfig.MERGE_SORT, UtilConfig.TRUE);

		// Initialize the shared thread pool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().init(ClientConfig.CLIENT_LISTENER_THREAD_POOL_SIZE, ClientConfig.CLIENT_LISTENER_THREAD_ALIVE_TIME);
		
		// Set the port number. 11/23/2014, Bing Li
		this.port = port;
		// Initialize a disposer. 11/23/2014, Bing Li
//		ClientListenerDisposer disposer = new ClientListenerDisposer();
		// Initialize a list to take all of the listeners to wait for remote connections concurrently. 11/23/2014, Bing Li
//		this.listenerRunners = new ArrayList<Runner<ClientListener, ClientListenerDisposer>>();
		this.listenerRunners = new ArrayList<Runner<ClientListener>>();
		// The runner is responsible for starting to wait for connections asynchronously. 11/23/2014, Bing Li
//		Runner<ClientListener, ClientListenerDisposer> listenerRunner;
		Runner<ClientListener> listenerRunner;
		try
		{
			// Initialize the socket to wait for remote connections. 11/23/2014, Bing Li
			this.serverSocket = new ServerSocket(this.port);
			// Start a bunch of threads to listen to connections. 11/23/2014, Bing Li
			for (int i = 0; i < ServerConfig.MAX_CLIENT_LISTEN_THREAD_COUNT; i++)
			{
				// Initialize the runner which contains the listener. 11/23/2014, Bing Li
//				listenerRunner = new Runner<ClientListener, ClientListenerDisposer>(new ClientListener(this.serverSocket), disposer, true);
//				listenerRunner = new Runner<ClientListener, ClientListenerDisposer>(new ClientListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), disposer, true);
				listenerRunner = new Runner<ClientListener>(new ClientListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), true);
				// Put the runner into a list for management. 11/23/2014, Bing Li
				this.listenerRunners.add(listenerRunner);
				// Start the runner. 11/23/2014, Bing Li
				listenerRunner.start();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the server IO registry. 11/23/2014, Bing Li
		ClientServerIORegistry.REGISTRY().init();
		// Initialize the client pool. 11/23/2014, Bing Li
		ClientPool.LOCAL().init();

		// Initialize the message producer to dispatcher messages. 11/23/2014, Bing Li
		ClientServerMessageProducer.CLIENT().init();

		// Initialize the eventer to notify the remote server. 11/23/2014, Bing Li
		ClientEventer.NOTIFY().init(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.SERVER_PORT);
		// Initialize the remote reader to send requests and receive responses from the remote server. 11/23/2014, Bing Li
		RemoteReader.REMOTE().init(ClientConfig.CLIENT_READER_POOL_SIZE);
		
		try
		{
			// The line tries to connect the CServer. Its IP and port number are saved on the server for that. Then, the RetrievablePool can work on the NodeKey to retrieve the IP and the port number. 10/03/2014, Bing Li
			ClientEventer.NOTIFY().notifyOnline();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		// Register the client on the remote server. 11/23/2014, Bing Li
		ClientEventer.NOTIFY().register();
	}

	/*
	 * Stop the client. 11/23/2014, Bing Li
	 */
	public void stop() throws InterruptedException, IOException, ClassNotFoundException
	{
		// Close the listeners. 11/23/2014, Bing Li
//		for (Runner<ClientListener, ClientListenerDisposer> runner : this.listenerRunners)
		for (Runner<ClientListener> runner : this.listenerRunners)
		{
			runner.stop(ClientConfig.TIME_TO_WAIT_FOR_THREAD_TO_DIE);
		}
		// Close the socket. 11/23/2014, Bing Li
		this.serverSocket.close();

		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Dispose the message producer. 11/23/2014, Bing Li
		ClientServerMessageProducer.CLIENT().dispose();

		// Dispose the client pool. 11/23/2014, Bing Li
		ClientPool.LOCAL().dispose();
		// Dispose the eventer. 11/23/2014, Bing Li
		ClientEventer.NOTIFY().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
		// Shutdown the remote reader. 11/23/2014, Bing Li
		RemoteReader.REMOTE().shutdown();
		// Dispose the server IO registry. 11/23/2014, Bing Li
		ClientServerIORegistry.REGISTRY().dispose();
		
		// Dispose the shared thread pool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
	}
}
