package org.greatfree.server.container;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;

// Created: 12/31/2018, Bing Li
public class PeerContainer
{
	private Peer<CSDispatcher> peer;
	
	public PeerContainer(String peerName, int port, ServerTask task, boolean isRegistryNeeded) throws IOException
	{
		CSDispatcher csd = new CSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		this.peer = new Peer.PeerBuilder<CSDispatcher>()
				.peerPort(port)
				.peerName(peerName)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(isRegistryNeeded)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new CSDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(csd)
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.scheulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
//				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
//				.asyncEventerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();
		
		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();

//		ServiceProvider.CS().init(this.peer.getPeerID(), task);
		ServiceProvider.CS().init(csd.getServerKey(), task);
	}

	
	public PeerContainer(ServerTask task, String configXML) throws IOException
	{
		PeerProfile.P2P().init(configXML);

		CSDispatcher csd = new CSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		this.peer = new Peer.PeerBuilder<CSDispatcher>()
				.peerPort(ServerProfile.CS().getPort())
				.peerName(PeerProfile.P2P().getPeerName())
				.registryServerIP(PeerProfile.P2P().getRegistryServerIP())
				.registryServerPort(PeerProfile.P2P().getRegistryServerPort())
				.isRegistryNeeded(PeerProfile.P2P().isRegistryNeeded())
				.listenerCount(ServerProfile.CS().getListeningThreadCount())
//				.serverThreadPoolSize(ServerProfile.CS().getServerThreadPoolSize())
//				.serverThreadKeepAliveTime(ServerProfile.CS().getServerThreadKeepAliveTime())
				.dispatcher(csd)
				.freeClientPoolSize(PeerProfile.P2P().getFreeClientPoolSize())
				.readerClientSize(PeerProfile.P2P().getReaderClientSize())
				.syncEventerIdleCheckDelay(PeerProfile.P2P().getSyncEventerIdleCheckDelay())
				.syncEventerIdleCheckPeriod(PeerProfile.P2P().getSyncEventerIdleCheckPeriod())
				.syncEventerMaxIdleTime(PeerProfile.P2P().getSyncEventerMaxIdleTime())
				.asyncEventQueueSize(PeerProfile.P2P().getAsyncEventQueueSize())
				.asyncEventerSize(PeerProfile.P2P().getAsyncEventerSize())
				.asyncEventingWaitTime(PeerProfile.P2P().getAsyncEventingWaitTime())
				.asyncEventerWaitTime(PeerProfile.P2P().getAsyncEventerWaitTime())
				.asyncEventerWaitRound(PeerProfile.P2P().getAsyncEventerWaitRound())
				.asyncEventIdleCheckDelay(PeerProfile.P2P().getAsyncEventIdleCheckDelay())
				.asyncEventIdleCheckPeriod(PeerProfile.P2P().getAsyncEventIdleCheckPeriod())
				.schedulerPoolSize(PeerProfile.P2P().getSchedulerPoolSize())
				.scheulerKeepAliveTime(PeerProfile.P2P().getSchedulerKeepAliveTime())
				.build();
		
		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();

//		ServiceProvider.CS().init(this.peer.getPeerID(), task);
		ServiceProvider.CS().init(csd.getServerKey(), task);
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.peer.stop(timeout);
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.peer.start();
	}
	
	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		this.peer.asyncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.peer.read(ip, port, request);
	}
	
	public void selfSyncNotify(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.peer.getPeerIP(), this.peer.getPort(), notification);
	}
	
	public void selfAsyncNotify(ServerMessage notification)
	{
		this.peer.asyncNotify(this.peer.getPeerIP(), this.peer.getPort(), notification);
	}
	
	public ServerMessage selfRead(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.peer.read(this.peer.getPeerIP(), this.peer.getPort(), request);
	}
	
	public void addPartners(String ip, int port)
	{
		this.peer.addPartners(ip, port);
	}

	public FreeClientPool getClientPool()
	{
		return this.peer.getClientPool();
	}
	
	public ThreadPool getPool()
	{
		return this.peer.getPool();
	}
	
	public String getPeerName()
	{
		return this.peer.getPeerName();
	}
	
	public String getPeerID()
	{
		return this.peer.getPeerID();
	}
	
	public String getPeerIP()
	{
		return this.peer.getPeerIP();
	}
	
	public int getPeerPort()
	{
		return this.peer.getPort();
	}
	
	public String getRegistryIP()
	{
		return this.peer.getRegistryServerIP();
	}
	
	public int getRegistryPort()
	{
		return this.peer.getRegistryServerPort();
	}
}
