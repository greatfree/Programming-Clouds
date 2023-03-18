package org.greatfree.server.container;

import java.io.IOException;
import java.util.List;

import org.greatfree.cluster.message.ClusterSizeRequest;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.PartitionSizeRequest;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.container.p2p.message.PeerDisableStateRequest;
import org.greatfree.framework.container.p2p.message.PeerDisableStateResponse;
import org.greatfree.framework.container.p2p.message.RegisterPeerRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

// Created: 12/31/2018, Bing Li
public class PeerContainer
{
	private Peer<CSDispatcher> peer;

	public PeerContainer(String peerName, int port, int listenerCount, int maxIOCount, String registryServerIP, int registryServerPort, ServerTask task, boolean isRegistryNeeded, boolean isServerDisabled) throws IOException
	{
		CSDispatcher csd = new CSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		this.peer = new Peer.PeerBuilder<CSDispatcher>()
				.peerPort(port)
				.peerName(peerName)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.isRegistryNeeded(isRegistryNeeded)
				.listenerCount(listenerCount)
				.maxIOCount(maxIOCount)
				.dispatcher(csd)
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.isServerDisabled(isServerDisabled)
				.build();
		
		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();
		ServiceProvider.CS().init(csd.getServerKey(), task);
	}

	public PeerContainer(String peerName, int port, int listenerCount, int maxIOCount, String registryServerIP, int registryServerPort, ServerTask task, boolean isRegistryNeeded) throws IOException
	{
		CSDispatcher csd = new CSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		this.peer = new Peer.PeerBuilder<CSDispatcher>()
				.peerPort(port)
				.peerName(peerName)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.isRegistryNeeded(isRegistryNeeded)
				.listenerCount(listenerCount)
				.maxIOCount(maxIOCount)
				.dispatcher(csd)
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();
		
		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();

		ServiceProvider.CS().init(csd.getServerKey(), task);
	}
	
	public PeerContainer(String peerName, int port, String registryServerIP, int registryServerPort, ServerTask task, boolean isRegistryNeeded) throws IOException
	{
		CSDispatcher csd = new CSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		this.peer = new Peer.PeerBuilder<CSDispatcher>()
				.peerPort(port)
				.peerName(peerName)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.isRegistryNeeded(isRegistryNeeded)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
				.dispatcher(csd)
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();
		
		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();

		ServiceProvider.CS().init(csd.getServerKey(), task);
	}

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
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
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
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
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
				.maxIOCount(ServerProfile.CS().getMaxIOCount())
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
				.asyncEventQueueWaitTime(PeerProfile.P2P().getAsyncEventQueueWaitTime())
//				.asyncEventerWaitRound(PeerProfile.P2P().getAsyncEventerWaitRound())
				.asyncEventIdleCheckDelay(PeerProfile.P2P().getAsyncEventIdleCheckDelay())
				.asyncEventIdleCheckPeriod(PeerProfile.P2P().getAsyncEventIdleCheckPeriod())
				.schedulerPoolSize(PeerProfile.P2P().getSchedulerPoolSize())
				.schedulerKeepAliveTime(PeerProfile.P2P().getSchedulerKeepAliveTime())
				.build();
		
		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();

//		ServiceProvider.CS().init(this.peer.getPeerID(), task);
		ServiceProvider.CS().init(csd.getServerKey(), task);
	}

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
//		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(timeout);
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer.start();
	}
	
	public boolean isDisabled()
	{
		return this.peer.isServerDisabled();
	}
	
	public void processNotification(Notification notification)
	{
		ServiceProvider.CS().processNotification(this.peer.getServerKey(), notification);
	}
	
	public ServerMessage processRequest(Request request)
	{
		return ServiceProvider.CS().processRequest(this.peer.getServerKey(), request);
	}

	/*
	 * It is useful. The incompatible servers need to be retrieved through the registry. 10/19/2021, Bing Li
	 * 
	 * It is not useful. If the local peer is registered, it can replace the incompatible servers. The incompatible ones can be controlled by the local peer. 10/19/2021, Bing Li
	 * 
	 * The method is specially designed for registering for other servers rather than the local peer. The servers run in the same process with the local peer. But they have different port. Usually, the servers are not GreatFree-compatible. Instead, they are introduced by other vendors, such as the HTTP server. So those servers cannot register with GreatFree's registry server. They need the support from the local peer. Then, they are controlled by or integrated with GreatFree. 10/19/2021, Bing Li
	 */
	public RegisterPeerResponse register(String registryIP, int registryPort, String name, int port) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return (RegisterPeerResponse)this.peer.read(registryIP, registryPort, new RegisterPeerRequest(PeerContainer.getPeerKey(name), name, this.getPeerIP(), port));
	}

	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		this.peer.asyncNotify(ip, port, notification);
	}
	
	public IPAddress getIPAddress(String registryIP, int registryPort, String nodeKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((PeerAddressResponse)this.peer.read(registryIP,  registryPort, new PeerAddressRequest(nodeKey))).getPeerAddress();
	}
	
	public boolean isServerDisabled(String registryIP, int registryPort, String nodeKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((PeerDisableStateResponse)this.peer.read(registryIP,  registryPort, new PeerDisableStateRequest(nodeKey))).isServerDisabled();
	}
	
	/*
	 * The method is useful for most storage systems, which need the partition information to design the upper level distribution strategy. 09/09/2020, Bing Li
	 */
	public int getPartitionSize(String clusterIP, int clusterPort) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((PartitionSizeResponse)this.peer.read(clusterIP,  clusterPort, new PartitionSizeRequest())).getPartitionSize();
	}

	/*
	 * The message is designed for the scalability such that all of the current children are replaced by new coming ones. In the storage system, the current ones is full in the disk space. In the case, they have to be replaced. But in other cases, it depends on the application level how to raise the scale and deal with the existing children. The system level cannot help. 09/12/2020, Bing Li
	 * 
	 * The message is an internal one, like the PartitionSizeRequest/PartitionSizeResponse, which is processed by the cluster root only. Programmers do not need to do anything but send it. So it inherits ServerMessage. 09/12/2020, Bing Li
	 */
	public int getClusterSize(String clusterIP, int clusterPort) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((ClusterSizeResponse)this.peer.read(clusterIP,  clusterPort, new ClusterSizeRequest())).getSize();
	}

	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return this.peer.read(ip, port, request);
	}

	public <T> List<T> read(String ip, int port, ServerMessage request, Class<T> c) throws ClassNotFoundException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		CollectedClusterResponse response = (CollectedClusterResponse)this.peer.read(ip, port, request);
		return Tools.filter(response.getResponses(), c);
	}
	
	public void selfSyncNotify(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.peer.getPeerIP(), this.peer.getPort(), notification);
	}
	
	public void selfAsyncNotify(ServerMessage notification)
	{
		this.peer.asyncNotify(this.peer.getPeerIP(), this.peer.getPort(), notification);
	}
	
	public ServerMessage selfRead(ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return this.peer.read(this.peer.getPeerIP(), this.peer.getPort(), request);
	}
	
	/*
	public void addPartners(String ip, int port)
	{
		this.peer.addPartners(ip, port);
	}
	*/

	/*
	public FreeClientPool getClientPool()
	{
		return this.peer.getClientPool();
	}
	*/

	/*
	public ThreadPool getPool()
	{
		return this.peer.getPool();
	}
	*/
	
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
	
	public static String getPeerKey(String peerName)
	{
		return Tools.getHash(peerName);
	}
}
