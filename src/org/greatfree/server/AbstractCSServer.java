package org.greatfree.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.client.ServerIORegistry;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.CSServer.CSServerBuilder;
import org.greatfree.util.ServerStatus;

// Created: 07/08/2018, Bing Li
public abstract class AbstractCSServer<Dispatcher extends ServerDispatcher<ServerMessage>>
{
//	private final static Logger log = Logger.getLogger("org.greatfree.server");
	
	// The server ID. 04/21/2017, Bing Li
//	private final String id;
	private String serverKey;
	// The ServerSocket waits for clients' connecting. The socket serves the server in the sense that it not only responds to clients' requests but also notifies clients even without clients' requests. 08/10/2014, Bing Li
	private ServerSocket socket;
	// The port number for socket. 08/10/2014, Bing Li
	private int port;
	// The count of listeners. 04/21/2017, Bing Li
	private final int listenerCount;
	// The list keeps all of the threads that listen to connecting from clients of the server. When the server is shutdown, those threads can be killed to avoid possible missing. 08/10/2014, Bing Li
//	private List<Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>>> listenerRunnerList;
	private List<Runner<CSListener<Dispatcher>>> listenerRunnerList;
	// Declare the server message producer, which is the important part of the server. Application developers can work on that directly through programming Dispatcher. 04/17/2017, Bing Li
	private ServerMessageProducer<Dispatcher> messageProducer;
	// It keeps all of the instances of CSServerIO for resource management. 04/21/2017, Bing Li
	private ServerIORegistry<CSServerIO<Dispatcher>> ioRegistry;
	// The thread pool that is employed by server listeners. 04/21/2017, Bing Li
//	private ThreadPool listenerThreadPool;
	// The message dispatcher to handle incoming messages concurrently. 04/21/2017, Bing Li
	private Dispatcher dispatcher;
	// The size of the thread pool that manages the threads to listen the port. 05/11/2017, Bing Li
//	private final int serverThreadPoolSize;
	// The time to keep alive for threads that listen to the port. 05/11/2017, Bing Li
//	private final long serverThreadKeepAliveTime;
//	private ThreadPool pool;
	// The size of the thread pool for the scheduler. 05/11/2017, Bing Li
//	private final int schedulerPoolSize;
	// The time to keep alive for the threads in the scheduler. 05/11/2017, Bing Li
//	private final long schedulerKeepAliveTime;
	// The flag whether the server is started or not. 07/24/2017, Bing Li
	private final AtomicBoolean isStarted;
	// Timeout to check incoming messages. 05/28/2018, Bing Li
//	private final long timeout;
	
	/*
	 * The constructor is usually called by the server. 05/02/2017, Bing Li
	 */
//	public CSServer(int port, int listenerCount, ThreadPool listenerThreadPool, Dispatcher dispatcher) throws IOException
//	public CSServer(int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime, Dispatcher dispatcher) throws IOException
//	public CSServer(int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher, long timeout) throws IOException
	/*
	public AbstractCSServer(int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher) throws IOException
	{
		this.id = Tools.generateUniqueKey();
		this.port = port;
		this.socket = new ServerSocket(this.port);
		this.listenerCount = listenerCount;
//		this.listenerThreadPool = listenerThreadPool;
		this.listenerThreadPoolSize = listenerThreadPoolSize;
		this.listenerThreadKeepAliveTime = listenerThreadKeepAliveTime;
//		this.schedulerPoolSize = schedulerPoolSize;
//		this.schedulerKeepAliveTime = schedulerKeepAliveTime;
		this.ioRegistry = new ServerIORegistry<CSServerIO<Dispatcher>>();

//		this.listenerRunnerList = new ArrayList<Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>>>();
		this.listenerRunnerList = new ArrayList<Runner<CSListener<Dispatcher>>>();
		// Initialize the message producer to dispatcher messages. 11/23/2014, Bing Li
		this.messageProducer = new ServerMessageProducer<Dispatcher>();
		this.dispatcher = dispatcher;
		this.isStarted = new AtomicBoolean(false);
//		this.timeout = timeout;
	}
	*/

	/*
	 * The constructor is usually called by the peer. The port might be conflict. So it does not specify at this moment. 05/02/2017, Bing Li
	 */
//	public CSServer(int port, int listenerCount, ThreadPool listenerThreadPool, Dispatcher dispatcher, boolean isPeer) throws IOException
//	public CSServer(int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime, Dispatcher dispatcher, boolean isPeer) throws IOException
//	public AbstractCSServer(int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher, long timeout, boolean isPeer) throws IOException
//	public AbstractCSServer(int port, int listenerCount, Dispatcher dispatcher, boolean isPeer) throws IOException
//	public AbstractCSServer(int port, int listenerCount, int serverThreadPoolSize, long serverThreadKeepAliveTime, Dispatcher dispatcher, boolean isPeer) throws IOException
	public AbstractCSServer(int port, int listenerCount, Dispatcher dispatcher, boolean isPeer) throws IOException
	{
//		this.hashKey = Tools.generateUniqueKey();
		this.serverKey = dispatcher.getServerKey();
		
		this.port = port;
		if (!isPeer)
		{
			this.socket = new ServerSocket(this.port);
		}
		this.listenerCount = listenerCount;
//		this.listenerThreadPool = listenerThreadPool;
//		this.serverThreadPoolSize = serverThreadPoolSize;
//		this.serverThreadKeepAliveTime = serverThreadKeepAliveTime;
//		this.schedulerPoolSize = schedulerPoolSize;
//		this.schedulerKeepAliveTime = schedulerKeepAliveTime;
		this.ioRegistry = new ServerIORegistry<CSServerIO<Dispatcher>>();

//		this.listenerRunnerList = new ArrayList<Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>>>();
		this.listenerRunnerList = new ArrayList<Runner<CSListener<Dispatcher>>>();
		// Initialize the message producer to dispatcher messages. 11/23/2014, Bing Li
		this.messageProducer = new ServerMessageProducer<Dispatcher>();
		this.dispatcher = dispatcher;
//		System.out.println("AbstractCSServer-Constructor(): dispatcher hashCode = " + this.dispatcher.hashCode());
//		this.dispatcher.setServerKey(this.hashKey);
		this.isStarted = new AtomicBoolean(false);
//		this.timeout = timeout;
	}

	/*
	public CSServer(int listenerCount, ThreadPool listenerThreadPool, Dispatcher dispatcher)
	{
		this.id = Tools.generateUniqueKey();
		this.listenerCount = listenerCount;
		this.listenerThreadPool = listenerThreadPool;
		this.ioRegistry = new ServerIORegistry<CSServerIO<Dispatcher>>();

		this.listenerRunnerList = new ArrayList<Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>>>();
		// Initialize the message producer to dispatcher messages. 11/23/2014, Bing Li
		this.messageProducer = new ServerMessageProducer<Dispatcher>();
		this.dispatcher = dispatcher;
	}
	*/

	public AbstractCSServer(CSServerBuilder<Dispatcher> builder) throws IOException
	{
//		this.hashKey = Tools.generateUniqueKey();
		this.serverKey = builder.getDispatcher().getServerKey();
		this.port = builder.getPort();
		this.socket = new ServerSocket(this.port);
		this.listenerCount = builder.getListenerCount();
//		this.listenerThreadPool = builder.getListenerThreadPool();
//		this.serverThreadPoolSize = builder.getServerThreadPoolSize();
//		this.serverThreadKeepAliveTime = builder.geServerThreadKeepAliveTime();
		
//		this.schedulerPoolSize = builder.getSchedulerPoolSize();
//		this.schedulerKeepAliveTime = builder.getSchedulerKeepAliveTime();
		this.ioRegistry = new ServerIORegistry<CSServerIO<Dispatcher>>();
//		this.listenerRunnerList = new ArrayList<Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>>>();
		this.listenerRunnerList = new ArrayList<Runner<CSListener<Dispatcher>>>();
		this.messageProducer = new ServerMessageProducer<Dispatcher>();
		this.dispatcher = builder.getDispatcher();
//		this.dispatcher.setServerKey(this.hashKey);
		this.isStarted = new AtomicBoolean(false);
//		this.timeout = builder.getTimeout();
	}
	
	/*
	protected void setID(String key)
	{
		this.hashKey = key;
	}
	*/
	
	/*
	protected String getHashKey()
	{
		return this.hashKey;
	}
	*/
	
	protected int getPort()
	{
		return this.port;
	}
	
	protected void setPort(int port) throws IOException
	{
		this.port = port;
		this.socket = new ServerSocket(this.port);
	}
	
	protected void setPort() throws IOException
	{
		this.socket = new ServerSocket(this.port);
	}
	
	protected boolean isStarted()
	{
		return this.isStarted.get();
	}
	
	protected ThreadPool getThreadPool()
	{
//		return SharedThreadPool.SHARED().getPool();
		return this.dispatcher.getThreadPool();
	}
	
	protected void start() throws ClassNotFoundException, RemoteReadException, IOException
	{
		// Set the ID as the server. 05/11/2017, Bing Li
		ServerStatus.FREE().addServerID(this.serverKey);
		
		// Usually, the server is a singleton in a process. Thus, the thread pool employed as a singleton is reasonable. 05/11/2017, Bing Li
//		SharedThreadPool.SHARED().init(this.listenerThreadPoolSize, this.listenerThreadKeepAliveTime);
//		this.pool = new ThreadPool(this.serverThreadPoolSize, this.serverThreadKeepAliveTime);
		
//		this.dispatcher.setThreadPool(this.pool);

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(this.schedulerPoolSize, this.schedulerKeepAliveTime);

		// Initialize a disposer which collects the server listener. 08/10/2014, Bing Li
//		CSListenerDisposer<Dispatcher> disposer = new CSListenerDisposer<Dispatcher>();
		this.messageProducer.init(this.dispatcher);
		for (int i = 0; i < this.listenerCount; i++)
		{
//			this.listenerRunnerList.add(new Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>>(new CSListener<Dispatcher>(this.socket, this.listenerThreadPool, this.messageProducer, this.ioRegistry), disposer, true));
//			this.listenerRunnerList.add(new Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>>(new CSListener<Dispatcher>(this.socket, SharedThreadPool.SHARED().getPool(), this.messageProducer, this.ioRegistry), disposer, true));
//			this.listenerRunnerList.add(new Runner<CSListener<Dispatcher>>(new CSListener<Dispatcher>(this.socket, SharedThreadPool.SHARED().getPool(), this.messageProducer, this.ioRegistry), true));
			this.listenerRunnerList.add(new Runner<CSListener<Dispatcher>>(new CSListener<Dispatcher>(this.socket, this.dispatcher.getThreadPool(), this.messageProducer, this.ioRegistry), true));
			this.listenerRunnerList.get(i).start();
		}

		// Set the flag to indicate the server is started. 07/05/2017, Bing Li
		this.isStarted.set(true);
	}
	
	protected void stop(long timeout) throws IOException, InterruptedException, RemoteReadException, ClassNotFoundException
	{
		// Close the socket for the server. 08/10/2014, Bing Li
		this.socket.close();

		// Stop all of the threads that listen to clients' connecting to the server. 08/10/2014, Bing Li
//		for (Runner<CSListener<Dispatcher>, CSListenerDisposer<Dispatcher>> runner : this.listenerRunnerList)
		for (Runner<CSListener<Dispatcher>> runner : this.listenerRunnerList)
		{
			runner.stop();
		}

		// Dispose the message producer. 11/23/2014, Bing Li
		this.messageProducer.dispose(timeout);

		// Dispose the thread pool. 05/11/2017, Bing Li
//		SharedThreadPool.SHARED().dispose(timeout);
//		this.pool.shutdown(timeout);
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Shutdown the IO registry. 11/07/2014, Bing Li
		this.ioRegistry.removeAllIOs();
		// Set the flag to indicate the server is stopped. 07/05/2017, Bing Li
		this.isStarted.set(false);
	}
}
