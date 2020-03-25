package org.greatfree.server.container;

import java.io.IOException;

import org.greatfree.client.CSClient;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.AbstractCSServer;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;
import org.greatfree.util.ServerStatus;

/*
 * The reason to design the class, Peer, intends to connect with the registry server which is implemented in the container manner. 01/13/2019, Bing Li
 */

public class Peer<Dispatcher extends ServerDispatcher<ServerMessage>> extends AbstractCSServer<Dispatcher>
{
	private Register<Dispatcher> register;
	private CSClient client;

	public Peer(PeerBuilder<Dispatcher> builder) throws IOException
	{
//		super(builder.getPeerPort(), builder.getListenerCount(), builder.getListenerThreadPoolSize(), builder.getListenerThreadKeepAliveTime(), builder.getDispatcher(), true);
		super(builder.getPeerPort(), builder.getListenerCount(), builder.getDispatcher(), true);
		
		this.register = new Register<Dispatcher>(builder.getPeerName(), builder.getRegistryServerIP(), builder.getRegistryServerPort(), builder.isRegistryNeeded());
		
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
//				.scheduler(builder.getDispatcher().getScheduler())
				.readerClientSize(builder.getReaderClientSize())
				.schedulerPoolSize(builder.getSchedulerPoolSize())
				.schedulerKeepAliveTime(builder.getSchedulerKeepAliveTime())
				.build();
	}

	/*
	 * A builder pattern that beautifies the constructor of the class. 04/30/2017, Bing Li
	 */
	public static class PeerBuilder<Dispatcher extends ServerDispatcher<ServerMessage>> implements Builder<Peer<Dispatcher>>
	{
		private String peerName;
		private int peerPort;
		private String registryServerIP;
		private int registryServerPort;
		private boolean isRegistryNeeded;
		private int listenerCount;
		// The size of the thread pool that manages the threads to listen the port. 05/11/2017, Bing Li
//		private int listenerThreadPoolSize;
		// The time to keep alive for threads that listen to the port. 05/11/2017, Bing Li
//		private long listenerThreadKeepAliveTime;
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
		private int schedulerPoolSize;
		private long schedulerKeepAliveTime;
		
		public PeerBuilder()
		{
		}
		
		public PeerBuilder<Dispatcher> peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}
		
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
			return this;
		}

		public PeerBuilder<Dispatcher> listenerCount(int listenerCount)
		{
			this.listenerCount = listenerCount;
			return this;
		}

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
		
		public PeerBuilder<Dispatcher> scheulerKeepAliveTime(long scheulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = scheulerKeepAliveTime;
			return this;
		}

		@Override
		public Peer<Dispatcher> build() throws IOException
		{
			return new Peer<Dispatcher>(this);
		}
		
		public String getPeerName()
		{
			return this.peerName;
		}
		
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
		
		public int getListenerCount()
		{
			return this.listenerCount;
		}

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
		
		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}
	}

	/*
	 * For testing only/. 01/16/2019, Bing Li
	 */
	/*
	public CSClient getPeerClient()
	{
		return this.client;
	}
	*/
	
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
	 * Start the peer. 04/29/2017, Bing Li
	 */
//	public synchronized void start(boolean isRegistryNeeded) throws ClassNotFoundException, RemoteReadException, IOException
	public synchronized void start() throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.register.register(this.client, this);
		// Start the server. 04/29/2017, Bing Li
		super.start();
		this.client.init(super.getThreadPool());
	}

	/*
	 * When unregistering, if the message is sent to the registry server which is implemented on the container technique, it is required to invoke the method. 01/13/2019, Bing Li
	 */
	/*
	 * Shutdown the peer. 04/29/2017, Bing Li
	 */
	public synchronized void stop(long timeout) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		// The line aims to hide exceptions when the system is shutdown. 03/19/2020, Bing Li
		ServerStatus.FREE().setShutdown();
		System.out.println("===> Peer-stop(): starting ...");
		this.register.unregister(this.client);
		// Since here is a request, it is required to wait for the response before shutting down. 01/16/2019, Bing Li
		try
		{
			Thread.sleep(timeout);
		}
		catch (InterruptedException e)
		{
			System.out.println("===> Peer-stop(): sleep is INTERRUPTED ...");
			ServerStatus.FREE().printException(e);
		}
		this.client.dispose();
		super.stop(timeout);
		System.out.println("===> Peer-stop(): done ...");
	}

	/*
	 * Send notifications synchronously. 05/01/2017, Bing Li
	 */
	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.client.syncNotify(ip, port, notification);
	}

	/*
	 * Send notifications asynchronously. 05/01/2017, Bing Li
	 */
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		this.client.asyncNotify(ip, port, notification);
	}

	/*
	 * Read remotely, i.e., send a request and wait until a response is received. 05/01/2017, Bing Li
	 */
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.client.read(ip, port, request);
	}

	/*
	 * Expose the TCP client pool. 05/08/2017, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.client.getClientPool();
	}
	
	public int getPartnerCount()
	{
		return this.client.getClientPool().getClientSize();
	}

	/*
	 * Add the partners of the peer. 08/26/2018, Bing Li
	 */
	public void addPartners(String ip, int port)
	{
		this.client.getClientPool().addIP(ip, port);
	}
	
	public IPAddress getIP(String ipKey)
	{
		return this.client.getClientPool().getIPAddress(ipKey);
	}
	
	public void removePartner(String ipKey) throws IOException
	{
		this.client.getClientPool().removeSource(ipKey);
		this.client.getClientPool().removeClient(ipKey);
	}
}
