package org.greatfree.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.client.ServerIORegistry;
import org.greatfree.concurrency.Runner;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Builder;
import org.greatfree.util.ServerStatus;

/*
 * The class, CSServer, aims to provide developers with a server that forms the distributed model, Client/Server (C/S). 04/29/2017, Bing Li
 */

// Created: 04/21/2017, Bing Li
public class CSServer<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	// The server ID. 04/21/2017, Bing Li
//	private final String id;
	private String id;
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
//	public CSServer(int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher) throws IOException
	/*
	public CSServer(int port, int listenerCount, Dispatcher dispatcher) throws IOException
	{
		this.id = Tools.generateUniqueKey();
		this.port = port;
		this.socket = new ServerSocket(this.port);
		this.listenerCount = listenerCount;
//		this.listenerThreadPool = listenerThreadPool;
//		this.listenerThreadPoolSize = listenerThreadPoolSize;
//		this.listenerThreadKeepAliveTime = listenerThreadKeepAliveTime;
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
//	public CSServer(int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher, long timeout, boolean isPeer) throws IOException
	/*
	public CSServer(int port, int listenerCount, Dispatcher dispatcher, long timeout, boolean isPeer) throws IOException
	{
		this.id = Tools.generateUniqueKey();
		this.port = port;
		if (!isPeer)
		{
			this.socket = new ServerSocket(this.port);
		}
		this.listenerCount = listenerCount;
//		this.listenerThreadPool = listenerThreadPool;
//		this.listenerThreadPoolSize = listenerThreadPoolSize;
//		this.listenerThreadKeepAliveTime = listenerThreadKeepAliveTime;
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

	public CSServer(CSServerBuilder<Dispatcher> builder) throws IOException
	{
//		this.id = Tools.generateUniqueKey();
		this.id = builder.getDispatcher().getServerKey();
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
//		this.dispatcher.setServerKey(this.id);
		this.isStarted = new AtomicBoolean(false);
//		this.timeout = builder.getTimeout();
	}

	/*
	 * A builder pattern that beautifies the constructor of the class. 04/30/2017, Bing Li
	 */
	public static class CSServerBuilder<Dispatcher extends ServerDispatcher<ServerMessage>> implements Builder<CSServer<Dispatcher>>
	{
		// The port number for socket. 08/10/2014, Bing Li
		private int port;
		// The count of listeners. 04/21/2017, Bing Li
		private int listenerCount;
		// The thread pool that is employed by server listeners. 04/21/2017, Bing Li
//		private ThreadPool listenerThreadPool;
		// The message dispatcher to handle incoming messages concurrently. 04/21/2017, Bing Li
		private Dispatcher dispatcher;
		// The size of the thread pool that manages the threads to listen the port. 05/11/2017, Bing Li
//		private int serverThreadPoolSize;
		// The time to keep alive for threads that listen to the port. 05/11/2017, Bing Li
//		private long serverThreadKeepAliveTime;
		// The size of the thread pool for the scheduler. 05/11/2017, Bing Li
//		private int schedulerPoolSize;
		// The time to keep alive for the threads in the scheduler. 05/11/2017, Bing Li
//		private long schedulerKeepAliveTime;
		// Timeout to check incoming messages. 05/28/2018, Bing Li
//		private long timeout;
		
		public CSServerBuilder()
		{
		}
		
		public CSServerBuilder<Dispatcher> port(int port)
		{
			this.port = port;
			return this;
		}
		
		public CSServerBuilder<Dispatcher> listenerCount(int listenerCount)
		{
			this.listenerCount = listenerCount;
			return this;
		}

		/*
		public CSServerBuilder<Dispatcher> listenerThreadPool(ThreadPool listenerThreadPool)
		{
			this.listenerThreadPool = listenerThreadPool;
			return this;
		}
		*/

		/*
		public CSServerBuilder<Dispatcher> serverThreadPoolSize(int serverThreadPoolSize)
		{
			this.serverThreadPoolSize = serverThreadPoolSize;
			return this;
		}
		
		public CSServerBuilder<Dispatcher> serverThreadKeepAliveTime(long serverThreadKeepAliveTime)
		{
			this.serverThreadKeepAliveTime = serverThreadKeepAliveTime;
			return this;
		}
		*/

		/*
		public CSServerBuilder<Dispatcher> schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}
		
		public CSServerBuilder<Dispatcher> schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}
		*/

		public CSServerBuilder<Dispatcher> dispatcher(Dispatcher dispatcher)
		{
			this.dispatcher = dispatcher;
			return this;
		}

		/*
		public CSServerBuilder<Dispatcher> timeout(long timeout)
		{
			this.timeout = timeout;
			return this;
		}
		*/

		@Override
		public CSServer<Dispatcher> build() throws IOException
		{
			return new CSServer<Dispatcher>(this);
		}
		
		public int getPort()
		{
			return this.port;
		}
		
		public int getListenerCount()
		{
			return this.listenerCount;
		}

		/*
		public ThreadPool getListenerThreadPool()
		{
			return this.listenerThreadPool;
		}
		*/

		/*
		public int getServerThreadPoolSize()
		{
			return this.serverThreadPoolSize;
		}
		
		public long geServerThreadKeepAliveTime()
		{
			return this.serverThreadKeepAliveTime;
		}
		*/

		/*
		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}
		*/

		public Dispatcher getDispatcher()
		{
			return this.dispatcher;
		}

		/*
		public long getTimeout()
		{
			return this.timeout;
		}
		*/
	}

	/*
	public void setID(String key)
	{
		this.id = key;
	}
	*/
	
	public String getID()
	{
		return this.id;
	}
	
	/*
	public int getPort()
	{
		return this.port;
	}

	public void setPort(int port) throws IOException
	{
		this.port = port;
		this.socket = new ServerSocket(this.port);
	}
	*/

	/*
	public void setPort() throws IOException
	{
		this.socket = new ServerSocket(this.port);
	}
	*/
	
	public boolean isStarted()
	{
		return this.isStarted.get();
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException
	{
		// Set the ID as the server. 05/11/2017, Bing Li
		ServerStatus.FREE().addServerID(this.id);
		
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
//			this.listenerRunnerList.add(new Runner<CSListener<Dispatcher>>(new CSListener<Dispatcher>(this.socket, this.pool, this.messageProducer, this.ioRegistry), true));
			this.listenerRunnerList.add(new Runner<CSListener<Dispatcher>>(new CSListener<Dispatcher>(this.socket, this.dispatcher.getThreadPool(), this.messageProducer, this.ioRegistry), true));
			this.listenerRunnerList.get(i).start();
		}

		// Set the flag to indicate the server is started. 07/05/2017, Bing Li
		this.isStarted.set(true);
	}
	
	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		// The line aims to hide exceptions when the system is shutdown. 03/19/2020, Bing Li
		ServerStatus.FREE().setShutdown();
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
