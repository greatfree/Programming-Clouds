package com.greatfree.testing.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import com.greatfree.concurrency.Runner;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.TerminateSignal;

/*
 * The class is a testing case for relevant server classes. It is responsible for starting up and shutting down the server. Since it is the unique entry and exit of the server, it is implemented in the form of a singleton. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class Server
{
	// The ServerSocket waits for clients' connecting. The socket serves the server in the sense that it not only responds to clients' requests but also notifies clients even without clients' requests. 08/10/2014, Bing Li
	private ServerSocket socket;
	// The port number for socket. 08/10/2014, Bing Li
	private int port;

	// The list keeps all of the threads that listen to connecting from clients of the server. When the server is shutdown, those threads can be killed to avoid possible missing. 08/10/2014, Bing Li
	private List<Runner<MyServerListener, MyServerListenerDisposer>> listenerRunnerList;

	/*
	 * A singleton is designed for the server's startup and shutdown interface. 08/10/2014, Bing Li
	 */
	private Server()
	{
	}
	
	private static Server instance = new Server();
	
	public static Server FREE()
	{
		if (instance == null)
		{
			instance = new Server();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Start the server and relevant listeners with concurrent threads for potential busy connecting. 08/10/2014, Bing Li
	 */
	public void start(int port)
	{
		// Initialize and start the server sockets. 08/10/2014, Bing Li
		this.port = port;
		try
		{
			this.socket = new ServerSocket(this.port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize a disposer which collects the server listener. 08/10/2014, Bing Li
		MyServerListenerDisposer disposer = new MyServerListenerDisposer();
		// Initialize the runner list. 11/25/2014, Bing Li
		this.listenerRunnerList = new ArrayList<Runner<MyServerListener, MyServerListenerDisposer>>();
		
		// Start up the threads to listen to connecting from clients which send requests as well as receive notifications. 08/10/2014, Bing Li
		Runner<MyServerListener, MyServerListenerDisposer> runner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
			runner = new Runner<MyServerListener, MyServerListenerDisposer>(new MyServerListener(this.socket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), disposer, true);
			this.listenerRunnerList.add(runner);
			runner.start();
		}

		// Initialize the server IO registry. 11/07/2014, Bing Li
		MyServerIORegistry.REGISTRY().init();
		// Initialize a client pool, which is used by the server to connect to the remote end. 09/17/2014, Bing Li
		ClientPool.SERVER().init();
	}

	/*
	 * Shutdown the server. 08/10/2014, Bing Li
	 */
	public void stop() throws IOException, InterruptedException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		// Close the socket for the server. 08/10/2014, Bing Li
		this.socket.close();
		
		// Stop all of the threads that listen to clients' connecting to the server. 08/10/2014, Bing Li
		for (Runner<MyServerListener, MyServerListenerDisposer> runner : this.listenerRunnerList)
		{
			runner.stop();
		}

		// Shutdown the IO registry. 11/07/2014, Bing Li
		MyServerIORegistry.REGISTRY().dispose();
		
		// Shut down the client pool. 09/17/2014, Bing Li
		ClientPool.SERVER().dispose();
	}
}
