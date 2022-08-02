package org.greatfree.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.admin.AdminConfig;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.ServerStatus;

/*
 * Different from the class, Server, a singleton, the ServerInstance is an instance of server. It can be used as a underlying substrate to program further by developers. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class OldServer<CSServerDispatcher extends ServerDispatcher<ServerMessage>, ManDispatcher extends ServerDispatcher<ServerMessage>>
{
	// The ServerSocket waits for clients' connecting. The socket serves the server in the sense that it not only responds to clients' requests but also notifies clients even without clients' requests. 08/10/2014, Bing Li
	private ServerSocket mySocket;
	// The port number for socket. 08/10/2014, Bing Li
	private int myPort;
	// The ServerSocket waits for administrator's connecting. 01/20/2016, Bing Li
	private ServerSocket manSocket;
	// The port number for the administration socket. 01/20/2016, Bing Li
	private int manPort;

	// The list keeps all of the threads that listen to connecting from clients of the server. When the server is shutdown, those threads can be killed to avoid possible missing. 08/10/2014, Bing Li
//	private List<Runner<CSListener<CSServerDispatcher>, CSListenerDisposer<CSServerDispatcher>>> listenerRunnerList;
	private List<Runner<CSListener<CSServerDispatcher>>> listenerRunnerList;
	
	// Declare one runner for the administration. Since the load is lower, it is not necessary to initialize multiple threads to listen to potential connections. 01/20/2016, Bing Li
//	private Runner<CSListener<ManDispatcher>, CSListenerDisposer<ManDispatcher>> manListenerRunner;
	private Runner<CSListener<ManDispatcher>> manListenerRunner;

	// Declare the server message producer, which is the important part of the server. Application developers can work on that directly through programming Dispatcher. 04/17/2017, Bing Li
	private ServerMessageProducer<CSServerDispatcher> messageProducer;
	private ServerMessageProducer<ManDispatcher> manProducer;
	
	private ServerIORegistry<CSServerIO<CSServerDispatcher>> ioRegistry;

	private ServerIORegistry<CSServerIO<ManDispatcher>> manRegistry;

	/*
	 * Start the server and relevant listeners with concurrent threads for potential busy connecting. 08/10/2014, Bing Li
	 * Dispatcher is the interface that is visible to developers to program their applications. 04/17/2017, Bing Li
	 */
//	public void start(int myPort, int manPort, CSServerDispatcher csDispatcher, ManDispatcher manDispatcher)
	public void start(int myPort, int manPort, int maxIOCount, CSServerDispatcher csDispatcher, ManDispatcher manDispatcher)
	{
		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		// Initialize the message producer to dispatcher messages. 11/23/2014, Bing Li
		this.messageProducer = new ServerMessageProducer<CSServerDispatcher>();
		this.messageProducer.init(csDispatcher);
		
		this.manProducer = new ServerMessageProducer<ManDispatcher>();
		this.manProducer.init(manDispatcher);

		// Initialize and start the server sockets. 08/10/2014, Bing Li
		this.myPort = myPort;
		try
		{
			this.mySocket = new ServerSocket(this.myPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize and start the admin server sockets. 01/20/2016, Bing Li
		this.manPort = manPort;
		try
		{
			this.manSocket = new ServerSocket(this.manPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize a disposer which collects the server listener. 08/10/2014, Bing Li
//		CSListenerDisposer<CSServerDispatcher> disposer = new CSListenerDisposer<CSServerDispatcher>();
		// Initialize the runner list. 11/25/2014, Bing Li
//		this.listenerRunnerList = new ArrayList<Runner<CSListener<CSServerDispatcher>, CSListenerDisposer<CSServerDispatcher>>>();
		this.listenerRunnerList = new ArrayList<Runner<CSListener<CSServerDispatcher>>>();
		
		this.ioRegistry = new ServerIORegistry<CSServerIO<CSServerDispatcher>>();
		this.manRegistry = new ServerIORegistry<CSServerIO<ManDispatcher>>();
		
		// Start up the threads to listen to connecting from clients which send requests as well as receive notifications. 08/10/2014, Bing Li
//		Runner<CSListener<CSServerDispatcher>, CSListenerDisposer<CSServerDispatcher>> runner;
		Runner<CSListener<CSServerDispatcher>> runner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
//			runner = new Runner<MyServerListener, MyServerListenerDisposer>(new MyServerListener(this.mySocket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), disposer, true);
//			runner = new Runner<CSListener<CSServerDispatcher>, CSListenerDisposer<CSServerDispatcher>>(new CSListener<CSServerDispatcher>(this.mySocket, SharedThreadPool.SHARED().getPool(), this.messageProducer, this.ioRegistry), disposer, true);
//			runner = new Runner<CSListener<CSServerDispatcher>>(new CSListener<CSServerDispatcher>(this.mySocket, SharedThreadPool.SHARED().getPool(), this.messageProducer, this.ioRegistry), true);
			runner = new Runner<CSListener<CSServerDispatcher>>(new CSListener<CSServerDispatcher>(this.mySocket, SharedThreadPool.SHARED().getPool(), this.messageProducer, this.ioRegistry, maxIOCount), true);
			this.listenerRunnerList.add(runner);
			runner.start();
		}
		
		// Initialize a disposer which collects the administration listener. 01/20/2016, Bing Li
//		CSListenerDisposer<ManDispatcher> manDisposer = new CSListenerDisposer<ManDispatcher>();
		// Initialize the runner to listen to connecting from the administrator which sends and notifications to the coordinator. 01/20/2016, Bing Li
//		this.manListenerRunner = new Runner<P2PListener<ManDispatcher>, DispatchingListenerDisposer<ManDispatcher>>(new P2PListener<ManDispatcher>(this.manSocket, SharedThreadPool.SHARED().getPool(), this.manProducer, this.manRegistry), manDisposer, true);
//		this.manListenerRunner = new Runner<CSListener<ManDispatcher>, CSListenerDisposer<ManDispatcher>>(new CSListener<ManDispatcher>(this.manSocket, SharedThreadPool.SHARED().getPool(), this.manProducer, this.manRegistry), manDisposer, true);
		this.manListenerRunner = new Runner<CSListener<ManDispatcher>>(new CSListener<ManDispatcher>(this.manSocket, SharedThreadPool.SHARED().getPool(), this.manProducer, this.manRegistry, maxIOCount), true);
		// Start up the runner. 01/20/2016, Bing Li
		this.manListenerRunner.start();

		// Initialize the ServerStatus to keep the nodes' status in the distributed system. 02/06/2016, Bing Li
		ServerStatus.FREE().addServerIDs(AdminConfig.getServerIDs());
		
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the server IO registry. 11/07/2014, Bing Li
//		CSServerIORegistry.REGISTRY().init();
		// Initialize the administrator server IO registry. 01/20/2016, Bing Li
//		ManIORegistry.REGISTRY().init();
		// Initialize a client pool, which is used by the server to connect to the remote end. 09/17/2014, Bing Li
//		ClientPool.SERVER().init();
	}

	/*
	 * Shutdown the server. 08/10/2014, Bing Li
	 */
	public void stop(long timeout) throws IOException, InterruptedException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
//		TerminateSignal.SIGNAL().notifyAllTermination();
		// Close the socket for the server. 08/10/2014, Bing Li
		this.mySocket.close();
		// Close the socket for the administrator server. 01/20/2016, Bing Li
		this.manSocket.close();
		
		// Stop all of the threads that listen to clients' connecting to the server. 08/10/2014, Bing Li
//		for (Runner<CSListener<CSServerDispatcher>, CSListenerDisposer<CSServerDispatcher>> runner : this.listenerRunnerList)
		for (Runner<CSListener<CSServerDispatcher>> runner : this.listenerRunnerList)
		{
			runner.stop();
		}
		
		// Stop the administration runner. 01/20/2016, Bing Li
		this.manListenerRunner.stop();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Dispose the message producer. 11/23/2014, Bing Li
		this.messageProducer.dispose(timeout);
		
		this.manProducer.dispose(timeout);
		
		// Shutdown the IO registry. 11/07/2014, Bing Li
		this.ioRegistry.removeAllIOs();
		
		// Shutdown the administration IO registry. 01/20/2016, Bing Li
		this.manRegistry.removeAllIOs();
		
		// Shut down the client pool. 09/17/2014, Bing Li
//		ClientPool.SERVER().dispose();
		
		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().dispose();
	}
	
}
