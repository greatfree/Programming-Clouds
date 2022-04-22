package org.greatfree.server;

import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;

import org.greatfree.client.CSClient;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Builder;
import org.greatfree.util.ServerStatus;

/*
 * The class, Peer, aims to provide developers with a node, called the peer, that forms the distributed model, Peer-to-Peer (P2P). 04/29/2017, Bing Li
 */

// Created: 04/29/2017, Bing Li
// public class Peer<Dispatcher extends ServerDispatcher<ServerMessage>> extends CSServer<Dispatcher>
public class Peer<Dispatcher extends ServerDispatcher<ServerMessage>> extends AbstractCSServer<Dispatcher>
{
//	private boolean isRegistryNeeded;
	// The name that makes sense to humans for the peer. 05/01/2017, Bing Li
//	private final String peerName;
	// The system registry server IP. 05/01/2017, Bing Li
//	private String sysRegistryServerIP;
	// The system registry server port. 05/01/2017, Bing Li
//	private int sysRegistryServerPort;
	// The application registry server IP. 05/01/2017, Bing Li
//	private String appRegistryServerIP;
	// The application registry server port. 05/01/2017, Bing Li
//	private int appRegistryServerPort;
	// The peer IP. 05/01/2017, Bing Li
//	private String peerIP;
	// The peer port. 05/01/2017, Bing Li
//	private int peerPort;
//	private int adminPort;
	
//	private IPAddress ipAddress;

	// The local IP key. The key is used to avoid the local node sends messages to itself. 05/19/2017, Bing Li
//	private String localIPKey;
	
	private Register<Dispatcher> register;

	/*
	// An instance of FreeClientPool to manages TCP clients. 04/17/2017, Bing Li
	private FreeClientPool clientPool;
	
	// Declare an eventer to send notifications synchronously. 05/01/2017, Bing Li
	private SyncRemoteEventer<ServerMessage> syncEventer;

	// Declare an eventer to send notifications asynchronously. 05/01/2017, Bing Li
	private AsyncRemoteEventer<ServerMessage> asyncEventer;
	*/
	
	private CSClient client;

	/*
	 * Initialize the peer. 04/29/2017, Bing Li
	 */
//	public Peer(String peerName, int port, int listenerCount, ThreadPool listenerThreadPool, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime, int schedulerPoolSize, long scheduleKeepAliveTime) throws IOException
//	public Peer(String peerName, int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime) throws IOException
//	public Peer(String peerName, int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime) throws IOException
	/*
	public Peer(String peerName, int port, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime) throws IOException
	{
//		super(port, listenerCount, listenerThreadPool, dispatcher);
//		super(port, listenerCount, listenerThreadPoolSize, listenerThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime, dispatcher);
		super(port, listenerCount, listenerThreadPoolSize, listenerThreadKeepAliveTime, dispatcher);
		
		// Initialize the peer name. 05/01/2017, Bing Li
		this.peerName = peerName;
		super.setID(Tools.getHash(this.peerName));

		// Initialize the peer port. 05/01/2017, Bing Li
//		this.peerPort = port;

		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(schedulerPoolSize, scheduleKeepAliveTime);

		// Initialize the eventer client. 11/23/2014, Bing Li
		Client.REMOTE().init(clientPoolSize, syncEventerIdleCheckDelay, syncEventerIdleCheckPeriod, syncEventerMaxIdleTime, clientThreadPoolSize, clientThreadKeepAliveTime);

		// Initialize the reader client. 04/29/2017, Bing Li
		RemoteReader.REMOTE().init(readerClientSize);

		// Initialize the asynchronous eventer. 05/01/2017, Bing Li
		this.asyncEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ServerMessage>()
//				.threadPool(Client.REMOTE().getThreadPool())
				.clientPool(Client.REMOTE().getClientPool())
				.eventQueueSize(asyncEventQueueSize)
				.eventerSize(asyncEventerSize)
				.eventingWaitTime(asyncEventingWaitTime)
				.eventerWaitTime(asyncEventerWaitTime)
				.waitRound(asyncEventerWaitRound)
				.idleCheckDelay(asyncEventIdleCheckDelay)
				.idleCheckPeriod(asyncEventIdleCheckPeriod)
//				.scheduler(Scheduler.GREATFREE().getSchedulerPool())
				.schedulerPoolSize(schedulerPoolSize)
				.schedulerKeepAliveTime(schedulerKeepAliveTime)
				.build();
	}
	*/

//	public Peer(String peerName, String sysRegistryIP, int sysRegistryPort, int listenerCount, ThreadPool listenerThreadPool, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime, int schedulerPoolSize, long scheduleKeepAliveTime) throws IOException
//	public Peer(String peerName, String sysRegistryIP, int sysRegistryPort, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime) throws IOException
//	public Peer(String peerName, String sysRegistryIP, int sysRegistryPort, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime) throws IOException
	/*
	public Peer(String peerName, String sysRegistryIP, int sysRegistryPort, int listenerCount, int listenerThreadPoolSize, long listenerThreadKeepAliveTime, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime) throws IOException
	{
//		super(UtilConfig.ZERO, listenerCount, listenerThreadPool, dispatcher);
//		super(UtilConfig.ZERO, listenerCount, listenerThreadPoolSize, listenerThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime, dispatcher);
		super(UtilConfig.ZERO, listenerCount, listenerThreadPoolSize, listenerThreadKeepAliveTime, dispatcher);

		// Initialize the peer name. 05/01/2017, Bing Li
		this.peerName = peerName;
		
		// Initialize the peer ID. 05/01/2017, Bing Li
		super.setID(Tools.getHash(this.peerName));
		
		// Initialize the system registry server IP. 05/01/2017, Bing Li
		this.sysRegistryServerIP = sysRegistryIP;
		// Initialize the system registry server port. 05/01/2017, Bing Li
		this.sysRegistryServerPort = sysRegistryPort;

		// Initialize the application registry server IP. 05/01/2017, Bing Li
//		this.appRegistryServerIP = appRegistryServerIP;
		// Initialize the application registry server port. 05/01/2017, Bing Li
//		this.appRegistryServerPort = appRegistryServerPort;

		// Initialize the peer port. The value of ZERO indicates that it is possible that multiple peers run on the same node. 05/01/2017, Bing Li
//		this.peerPort = UtilConfig.ZERO;

		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(schedulerPoolSize, scheduleKeepAliveTime);

		// Initialize the eventer client. 11/23/2014, Bing Li
		Client.REMOTE().init(clientPoolSize, syncEventerIdleCheckDelay, syncEventerIdleCheckPeriod, syncEventerMaxIdleTime, clientThreadPoolSize, clientThreadKeepAliveTime);

		// Initialize the reader client. 04/29/2017, Bing Li
		RemoteReader.REMOTE().init(readerClientSize);

		// Initialize the asynchronous eventer. 05/01/2017, Bing Li
		this.asyncEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ServerMessage>()
//				.threadPool(Client.REMOTE().getThreadPool())
				.clientPool(Client.REMOTE().getClientPool())
				.eventQueueSize(asyncEventQueueSize)
				.eventerSize(asyncEventerSize)
				.eventingWaitTime(asyncEventingWaitTime)
				.eventerWaitTime(asyncEventerWaitTime)
				.waitRound(asyncEventerWaitRound)
				.idleCheckDelay(asyncEventIdleCheckDelay)
				.idleCheckPeriod(asyncEventIdleCheckPeriod)
//				.scheduler(Scheduler.GREATFREE().getSchedulerPool())
				.schedulerPoolSize(schedulerPoolSize)
				.schedulerKeepAliveTime(schedulerKeepAliveTime)
				.build();
	}
	*/

	public Peer(PeerBuilder<Dispatcher> builder) throws IOException
	{
//		super(builder.getPeerPort(), builder.getListenerCount(), builder.getListenerThreadPool(), builder.getDispatcher(), true);
//		super(builder.getPeerPort(), builder.getListenerCount(), builder.getListenerThreadPoolSize(), builder.getListenerThreadKeepAliveTime(), builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime(), builder.getDispatcher(), true);
//		super(builder.getPeerPort(), builder.getListenerCount(), builder.getListenerThreadPoolSize(), builder.getListenerThreadKeepAliveTime(), builder.getDispatcher(), builder.getCheckMSGTimeout(), true);
//		super(builder.getPeerPort(), builder.getListenerCount(), builder.getListenerThreadPoolSize(), builder.getListenerThreadKeepAliveTime(), builder.getDispatcher(), true);
		super(builder.getPeerPort(), builder.getListenerCount(), builder.getDispatcher(), true);
		
		
		// Initialize the peer name. 05/01/2017, Bing Li
//		this.peerName = builder.getPeerName();
		// Initialize the peer ID. 05/01/2017, Bing Li
//		super.setID(Tools.getHash(this.peerName));
		
		// Get the local IP address. 05/01/2017, Bing Li
//		this.peerIP = Tools.getLocalIP();

		// Initialize the system registry server IP. 05/01/2017, Bing Li
//		this.sysRegistryServerIP = builder.getSysRegistryServerIP();
		// Initialize the system registry server port. 05/01/2017, Bing Li
//		this.sysRegistryServerPort = builder.getSysRegistryServerPort();
//		this.isRegistryNeeded = builder.isRegistryNeeded();
		
//		System.out.println("Peer: isRegistryNeeded = " + builder.isRegistryNeeded());

		this.register = new Register<Dispatcher>(builder.getPeerName(), builder.getRegistryServerIP(), builder.getRegistryServerPort(), builder.isRegistryNeeded());

		// Initialize the registry server IP. 05/01/2017, Bing Li
//		this.appRegistryServerIP = builder.getAppRegistryServerIP();
		// Initialize the registry server port. 05/01/2017, Bing Li
//		this.appRegistryServerPort = builder.getAppRegistryServerPort();
		// Initialize the peer port. The value of ZERO indicates that it is possible that multiple peers run on the same node. 05/01/2017, Bing Li
//		this.peerPort = UtilConfig.ZERO;

		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime());

		// Initialize the eventer client. 11/23/2014, Bing Li
//		Client.REMOTE().init(builder.getClientPoolSize(), builder.getSyncEventerIdleCheckDelay(), builder.getSyncEventerIdleCheckPeriod(), builder.getSyncEventerMaxIdleTime(), builder.getClientThreadPoolSize(), builder.getClientThreadKeepAliveTime());
		
		// Initialize the reader client. 04/29/2017, Bing Li
//		RemoteReader.REMOTE().init(builder.getReaderClientSize());
		
		/*
		// Initialize the client pool eventers. 05/01/2017, Bing Li
		this.clientPool = new FreeClientPool(builder.getClientPoolSize());
		this.clientPool.setIdleChecker(builder.getSyncEventerIdleCheckDelay(), builder.getSyncEventerIdleCheckPeriod(), builder.getSyncEventerMaxIdleTime());

		// Initialize the asynchronous eventer. 05/01/2017, Bing Li
		this.syncEventer = new SyncRemoteEventer<ServerMessage>(this.clientPool);

		// Initialize the asynchronous eventer. 05/01/2017, Bing Li
		this.asyncEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ServerMessage>()
//				.threadPool(Client.REMOTE().getThreadPool())
//				.clientPool(Client.REMOTE().getClientPool())
				.clientPool(this.clientPool)
				.eventQueueSize(builder.getAsyncEventQueueSize())
				.eventerSize(builder.getAsyncEventerSize())
				.eventingWaitTime(builder.getAsyncEventingWaitTime())
				.eventerWaitTime(builder.getAsyncEventerWaitTime())
				.waitRound(builder.getAsyncEventerWaitRound())
				.idleCheckDelay(builder.getAsyncEventIdleCheckDelay())
				.idleCheckPeriod(builder.getAsyncEventIdleCheckPeriod())
//				.scheduler(Scheduler.GREATFREE().getSchedulerPool())
				.schedulerPoolSize(builder.getSchedulerPoolSize())
				.schedulerKeepAliveTime(builder.getSchedulerKeepAliveTime())
				.build();
				*/
		
		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(builder.getClientPoolSize())
				.clientIdleCheckDelay(builder.getClientIdleCheckDelay())
				.clientIdleCheckPeriod(builder.getClientIdleCheckPeriod())
				.clientMaxIdleTime(builder.getClientMaxIdleTime())
				.asyncEventQueueSize(builder.getAsyncEventQueueSize())
				.asyncEventerSize(builder.getAsyncEventerSize())
				.asyncEventingWaitTime(builder.getAsyncEventingWaitTime())
				.asyncEventerWaitTime(builder.getAsyncEventerWaitTime())
				.asyncEventerWaitRound(builder.getAsyncEventerWaitRound())
				.asyncEventIdleCheckDelay(builder.getAsyncEventIdleCheckDelay())
				.asyncEventIdleCheckPeriod(builder.getAsyncEventIdleCheckPeriod())
				.schedulerPoolSize(builder.getSchedulerPoolSize())
				.schedulerKeepAliveTime(builder.getSchedulerKeepAliveTime())
//				.pool(super.getThreadPool())
//				.scheduler(builder.getDispatcher().getScheduler())
				.readerClientSize(builder.getReaderClientSize())
				.build();
	}
	
	/*
	 * A builder pattern that beautifies the constructor of the class. 04/30/2017, Bing Li
	 */
	public static class PeerBuilder<Dispatcher extends ServerDispatcher<ServerMessage>> implements Builder<Peer<Dispatcher>>
	{
		private String peerName;
//		private String peerIP;
		private int peerPort;
		private String registryServerIP;
		private int registryServerPort;
		private boolean isRegistryNeeded;
//		private String appRegistryServerIP;
//		private int appRegistryServerPort;
		private int listenerCount;
		// The size of the thread pool that manages the threads to listen the port. 05/11/2017, Bing Li
//		private int listenerThreadPoolSize;
		// The time to keep alive for threads that listen to the port. 05/11/2017, Bing Li
//		private long listenerThreadKeepAliveTime;
//		private ThreadPool listenerThreadPool;
		private Dispatcher dispatcher;
		private int clientPoolSize;
		private int readerClientSize;
		private long clientIdleCheckDelay;
		private long clientIdleCheckPeriod;
		private long clientMaxIdleTime;
		
		private int asyncEventQueueSize;
		private int asyncEventerSize;
		private long asyncEventingWaitTime;
		private long asyncEventerWaitTime;
		private int asyncEventerWaitRound;
		private long asyncEventIdleCheckDelay;
		private long asyncEventIdleCheckPeriod;
		
//		private int clientThreadPoolSize;
//		private long clientThreadKeepAliveTime;
		
		private int schedulerPoolSize;
		private long schedulerKeepAliveTime;
//		private long asyncEventerShutdownTimeout;
		
//		private long checkMSGTimeout;
		
		public PeerBuilder()
		{
		}
		
		public PeerBuilder<Dispatcher> peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}

		/*
		public PeerBuilder<Dispatcher> peerIP(String peerIP)
		{
			this.peerIP = peerIP;
			return this;
		}
		*/
		
		public PeerBuilder<Dispatcher> peerPort(int peerPort)
		{
			this.peerPort = peerPort;
			return this;
		}
		
		public PeerBuilder<Dispatcher> registryServerIP(String registryServerIP)
		{
			this.registryServerIP = registryServerIP;
			return this;
		}
		
		public PeerBuilder<Dispatcher> registryServerPort(int registryServerPort)
		{
			this.registryServerPort = registryServerPort;
			return this;
		}
		
		public PeerBuilder<Dispatcher> isRegistryNeeded(boolean isRegistryNeeded)
		{
			this.isRegistryNeeded = isRegistryNeeded;
//			System.out.println("PeerBuilder: isRegistryNeeded = " + this.isRegistryNeeded);
			return this;
		}

		/*
		public PeerBuilder<Dispatcher> appRegistryServerIP(String registryServerIP)
		{
			this.appRegistryServerIP = registryServerIP;
			return this;
		}
		
		public PeerBuilder<Dispatcher> appRegistryServerPort(int registryServerPort)
		{
			this.appRegistryServerPort = registryServerPort;
			return this;
		}
		*/
		
		public PeerBuilder<Dispatcher> listenerCount(int listenerCount)
		{
			this.listenerCount = listenerCount;
			return this;
		}

		/*
		public PeerBuilder<Dispatcher> listenerThreadPool(ThreadPool listenerThreadPool)
		{
			this.listenerThreadPool = listenerThreadPool;
			return this;
		}
		*/
		
		/*
		public PeerBuilder<Dispatcher> serverThreadPoolSize(int listenerThreadPoolSize)
		{
			this.listenerThreadPoolSize = listenerThreadPoolSize;
			return this;
		}
		
		public PeerBuilder<Dispatcher> serverThreadKeepAliveTime(long listenerThreadKeepAliveTime)
		{
			this.listenerThreadKeepAliveTime = listenerThreadKeepAliveTime;
			return this;
		}
		*/
		
		public PeerBuilder<Dispatcher> dispatcher(Dispatcher dispatcher)
		{
			this.dispatcher = dispatcher;
			return this;
		}
		
		public PeerBuilder<Dispatcher> freeClientPoolSize(int clientPoolSize)
		{
			this.clientPoolSize = clientPoolSize;
			return this;
		}
		
		public PeerBuilder<Dispatcher> readerClientSize(int readerClientSize)
		{
			this.readerClientSize = readerClientSize;
			return this;
		}
		
		public PeerBuilder<Dispatcher> syncEventerIdleCheckDelay(long idleCheckDelay)
		{
			this.clientIdleCheckDelay = idleCheckDelay;
			return this;
		}
		
		public PeerBuilder<Dispatcher> syncEventerIdleCheckPeriod(long idleCheckPeriod)
		{
			this.clientIdleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public PeerBuilder<Dispatcher> syncEventerMaxIdleTime(long maxIdleTime)
		{
			this.clientMaxIdleTime = maxIdleTime;
			return this;
		}

		public PeerBuilder<Dispatcher> asyncEventQueueSize(int asyncEventQueueSize)
		{
			this.asyncEventQueueSize = asyncEventQueueSize;
			return this;
		}

		public PeerBuilder<Dispatcher> asyncEventerSize(int asyncEventerSize)
		{
			this.asyncEventerSize = asyncEventerSize;
			return this;
		}

		public PeerBuilder<Dispatcher> asyncEventingWaitTime(long asyncEventingWaitTime)
		{
			this.asyncEventingWaitTime = asyncEventingWaitTime;
			return this;
		}

		public PeerBuilder<Dispatcher> asyncEventerWaitTime(long asyncEventerWaitTime)
		{
			this.asyncEventerWaitTime = asyncEventerWaitTime;
			return this;
		}

		public PeerBuilder<Dispatcher> asyncEventerWaitRound(int asyncEventerWaitRound)
		{
			this.asyncEventerWaitRound = asyncEventerWaitRound;
			return this;
		}

		public PeerBuilder<Dispatcher> asyncEventIdleCheckDelay(long asyncEventIdleCheckDelay)
		{
			this.asyncEventIdleCheckDelay = asyncEventIdleCheckDelay;
			return this;
		}

		public PeerBuilder<Dispatcher> asyncEventIdleCheckPeriod(long asyncEventIdleCheckPeriod)
		{
			this.asyncEventIdleCheckPeriod = asyncEventIdleCheckPeriod;
			return this;
		}

		public PeerBuilder<Dispatcher> schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}

		public PeerBuilder<Dispatcher> schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}

		/*
		public PeerBuilder<Dispatcher> asyncEventerShutdownTimeout(long asyncEventerShutdownTimeout)
		{
			this.asyncEventerShutdownTimeout = asyncEventerShutdownTimeout;
			return this;
		}
		
		public PeerBuilder<Dispatcher> clientThreadPoolSize(int threadPoolSize)
		{
			this.clientThreadPoolSize = threadPoolSize;
			return this;
		}
		
		public PeerBuilder<Dispatcher> clientThreadKeepAliveTime(long threadKeepAliveTime)
		{
			this.clientThreadKeepAliveTime = threadKeepAliveTime;
			return this;
		}
		*/

		/*
		public PeerBuilder<Dispatcher> checkMSGTimeout(long checkMSGTimeout)
		{
			this.checkMSGTimeout = checkMSGTimeout;
			return this;
		}
		*/
		
		@Override
		public Peer<Dispatcher> build() throws IOException
		{
			return new Peer<Dispatcher>(this);
		}
		
		public String getPeerName()
		{
			return this.peerName;
		}

		/*
		public String getPeerIP()
		{
			return this.peerIP;
		}
		*/
		
		public int getPeerPort()
		{
			return this.peerPort;
		}
		
		public String getRegistryServerIP()
		{
			return this.registryServerIP;
		}
		
		public int getRegistryServerPort()
		{
			return this.registryServerPort;
		}
		
		public boolean isRegistryNeeded()
		{
			return this.isRegistryNeeded;
		}

		/*
		public String getAppRegistryServerIP()
		{
			return this.appRegistryServerIP;
		}
		
		public int getAppRegistryServerPort()
		{
			return this.appRegistryServerPort;
		}
		*/
		
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
		public int getListenerThreadPoolSize()
		{
			return this.listenerThreadPoolSize;
		}
		
		public long getListenerThreadKeepAliveTime()
		{
			return this.listenerThreadKeepAliveTime;
		}
		*/

		public Dispatcher getDispatcher()
		{
			return this.dispatcher;
		}
		
		public int getClientPoolSize()
		{
			return this.clientPoolSize;
		}
		
		public int getReaderClientSize()
		{
			return this.readerClientSize;
		}
		
		public long getClientIdleCheckDelay()
		{
			return this.clientIdleCheckDelay;
		}
		
		public long getClientIdleCheckPeriod()
		{
			return this.clientIdleCheckPeriod;
		}
		
		public long getClientMaxIdleTime()
		{
			return this.clientMaxIdleTime;
		}
		
		public int getAsyncEventQueueSize()
		{
			return this.asyncEventQueueSize;
		}
		
		public int getAsyncEventerSize()
		{
			return this.asyncEventerSize;
		}
		
		public long getAsyncEventingWaitTime()
		{
			return this.asyncEventingWaitTime;
		}
		
		public long getAsyncEventerWaitTime()
		{
			return this.asyncEventerWaitTime;
		}
		
		public int getAsyncEventerWaitRound()
		{
			return this.asyncEventerWaitRound;
		}
		
		public long getAsyncEventIdleCheckDelay()
		{
			return this.asyncEventIdleCheckDelay;
		}
		
		public long getAsyncEventIdleCheckPeriod()
		{
			return this.asyncEventIdleCheckPeriod;
		}

		/*
		public long getAsyncEventerShutdownTimeout()
		{
			return this.asyncEventerShutdownTimeout;
		}

		public int getClientThreadPoolSize()
		{
			return this.clientThreadPoolSize;
		}
		
		public long getClientThreadKeepAliveTime()
		{
			return this.clientThreadKeepAliveTime;
		}
		*/

		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}

		/*
		public long getCheckMSGTimeout()
		{
			return this.checkMSGTimeout;
		}
		*/
	}
	
	public boolean isStarted()
	{
		return super.isStarted();
	}
	
	public String getPeerName()
	{
		return this.register.getPeerName();
	}
	
	public String getPeerID()
	{
//		return super.getHashKey();
		return this.register.getPeerID();
	}
	
	public String getPeerIP()
	{
//		return this.peerIP;
		return this.register.getPeerIP();
	}
	
	public int getPort()
	{
		return super.getPort();
	}
	
	public void setPort(int port) throws IOException
	{
		super.setPort(port);
	}
	
	public void setPort() throws IOException
	{
		super.setPort();
	}
	
	public String getRegistryServerIP()
	{
		return this.register.getRegistryIP();
	}
	
	public int getRegistryServerPort()
	{
		return this.register.getRegistryPort();
	}
	
	public String getLocalIPKey()
	{
//		return this.localIPKey;
		return this.register.getLocalIPKey();
	}
	
	public ThreadPool getPool()
	{
		return super.getThreadPool();
	}

	/*
	public IPAddress getAddress()
	{
		return this.ipAddress;
	}
	*/

	/*
	 * Start the peer. 04/29/2017, Bing Li
	 */
//	public synchronized void start(boolean isRegistryNeeded) throws ClassNotFoundException, RemoteReadException, IOException
	public synchronized void start() throws ClassNotFoundException, RemoteReadException, IOException
	{
//		this.isRegistryNeeded = isRegistryNeeded;
		// Get the local IP address. 05/01/2017, Bing Li
//		this.peerIP = Tools.getLocalIP();
//			System.out.println("\n===================");
//			System.out.println("peerIP = " + this.peerIP);
//			System.out.println(peerName + ": " + Tools.getHash(this.peerName));
//			System.out.println("key = " + Tools.getHash(this.peerName));
//			System.out.println("super.getID() = " + super.getID());
			
		// To avoid the possibility of port conflict, which happens when multiple peers run on the same machine, it is necessary to obtain an idle port from the registry server. 05/01/2017, Bing Li
		/*
		if (isRegistryNeeded)
		{
			RegisterPeerResponse response = (RegisterPeerResponse)this.client.read(this.sysRegistryServerIP, this.sysRegistryServerPort, new RegisterPeerRequest(super.getID(), this.peerName, this.peerIP, super.getPort()));
//			System.out.println("\n===================");
//			System.out.println("response.getPeerPort() = " + response.getPeerPort());
//			System.out.println("response.getAdminPort() = " + response.getAdminPort());
//			System.out.println("===================\n");
			
			if (super.getPort() != response.getPeerPort())
			{
					// Initialize the peer port, which is obtained from the registry server. 05/01/2017, Bing Li
	//				super.getPort() = response.getPort();
					// Set the port of the parent class, CSServer, to get ready for startup. 05/01/2017, Bing Li
	//				this.setPort(this.peerPort);
	//				this.adminPort = response.getAdminPort();
				super.setPort(response.getPeerPort());
			}
			else
			{
				super.setPort();
			}
		}
		else
		{
			super.setPort();
		}
			
		this.localIPKey = Tools.getKeyOfFreeClient(this.peerIP, super.getPort());
		*/
		this.register.register(this.client, this);
//			this.ipAddress = new IPAddress(super.getID(), this.peerIP, super.getPort());
		// Start the server. 04/29/2017, Bing Li
		super.start();
		this.client.init(super.getThreadPool());
	}

	/*
	 * Shutdown the peer. 04/29/2017, Bing Li
	 */
	public synchronized void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		// The line aims to hide exceptions when the system is shutdown. 03/19/2020, Bing Li
		ServerStatus.FREE().setShutdown();
		/*
		if (this.isRegistryNeeded)
		{
			this.client.read(this.sysRegistryServerIP, this.sysRegistryServerPort, new UnregisterPeerRequest(super.getID()));
		}
		*/
		this.register.unregister(this.client);
		// Dispose the synchronous eventer. 05/01/2017, Bing Li
//		this.syncEventer.dispose();
		// Dispose the asynchronous eventer. 05/01/2017, Bing Li
//		this.asyncEventer.dispose();
		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().dispose();
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();
		// Dispose the event client. 04/29/2017, Bing Li
//		Client.REMOTE().dispose(timeout);
//		this.clientPool.dispose();
		this.client.dispose();
		// Shutdown the reader client. 04/29/2017, Bing Li
//		RemoteReader.REMOTE().shutdown();
		// Stop the server. 04/29/2017, Bing Li
		super.stop(timeout);
	}

	/*
	 * The method is useful when ports get conflicts. 05/02/2017, Bing Li
	 */
	/*
	public int getAdminPort()
	{
		return this.adminPort;
	}
	*/

	/*
	 * Send notifications synchronously. 05/01/2017, Bing Li
	 */
	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
//		Client.REMOTE().getClientPool().send(new IPPort(ip, port), notification);
//		this.syncEventer.notify(ip, port, notification);
		this.client.syncNotify(ip, port, notification);
	}
	
	public void syncNotify(String clientIPKey, ServerMessage notification) throws IOException
	{
		this.client.syncNotify(clientIPKey, notification);
	}

	/*
	 * Send notifications asynchronously. 05/01/2017, Bing Li
	 */
	public void asyncNotify(String ip, int port, ServerMessage notification) throws RejectedExecutionException
	{
		/*
		if (!this.asyncEventer.isReady())
		{
//			Client.REMOTE().getThreadPool().execute(this.asyncEventer);
			super.getThreadPool().execute(this.asyncEventer);
		}
		this.asyncEventer.notify(ip, port, notification);
		*/
		this.client.asyncNotify(ip, port, notification);
	}

	/*
	 * Read remotely, i.e., send a request and wait until a response is received. 05/01/2017, Bing Li
	 */
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ip, port, request);
		return this.client.read(ip, port, request);
	}

	/*
	 * Expose the TCP client pool. 05/08/2017, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
//		return Client.REMOTE().getClientPool();
//		return this.clientPool;
		return this.client.getClientPool();
	}
	
	public int getPartnerCount()
	{
		return this.client.getClientPool().getClientSize();
	}

	/*
	 * Add the partners of the peer. 08/26/2018, Bing Li
	 */
//	public void addPartners(String ipKey, String ip, int port)
	public void addPartners(String ip, int port)
	{
//		this.client.getClientPool().addIP(ipKey, ip, port);
		this.client.getClientPool().addIP(ip, port);
	}
	
	public void removePartner(String ipKey) throws IOException
	{
		this.client.getClientPool().removeSource(ipKey);
		this.client.getClientPool().removeClient(ipKey);
	}
}
