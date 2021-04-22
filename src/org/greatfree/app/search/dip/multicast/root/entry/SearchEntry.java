package org.greatfree.app.search.dip.multicast.root.entry;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.message.RootIPAddressBroadcastNotification;
import org.greatfree.framework.multicast.root.RootMulticastor;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;

// Created: 09/28/2018, Bing Li
class SearchEntry
{
	private Peer<SearchEntryDispatcher> entryPeer;

	private SearchEntry()
	{
	}
	
	private static SearchEntry instance = new SearchEntry();
	
	public static SearchEntry SEARCH()
	{
		if (instance == null)
		{
			instance = new SearchEntry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.entryPeer.stop(timeout);
		RootMulticastor.ROOT().stop();
		TerminateSignal.SIGNAL().setTerminated();
	}
	
	public void start() throws IOException, ClassNotFoundException, RemoteReadException, InstantiationException, IllegalAccessException, InterruptedException, DistributedNodeFailedException
	{
		this.entryPeer = new Peer.PeerBuilder<SearchEntryDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(MulticastConfig.CLUSTER_SERVER_ROOT_NAME)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new SearchEntryDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new SearchEntryDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
//				.asyncEventerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();

		// Start up the peer. 06/14/2017, Bing Li
		this.entryPeer.start();

		// Retrieve all of the registered IP addresses of the distributed nodes in the cluster from the registry server. 05/08/2017, Bing Li
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.entryPeer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new ClusterIPRequest());

		if (ipResponse.getIPs() != null)
		{
			System.out.println("Search Entry Peer: ipResponse: ip size = " + ipResponse.getIPs().size());
			
			// Add the IP addresses to the client pool. 05/08/2017, Bing Li
			for (IPAddress ip : ipResponse.getIPs().values())
			{
				System.out.println("Distributed IPs = " + ip.getIP() + ", " + ip.getPort());
//				this.entryPeer.addPartners(ip.getPeerKey(), ip.getIP(), ip.getPort());
				this.entryPeer.addPartners(ip.getIP(), ip.getPort());
			}
			
			RootMulticastor.ROOT().start(this.entryPeer.getClientPool(), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.entryPeer.getPool());
			System.out.println("entry peer IP = " + this.entryPeer.getPeerIP());
			RootMulticastor.ROOT().broadcastNotify(new RootIPAddressBroadcastNotification(new IPAddress(this.entryPeer.getPeerID(), this.entryPeer.getPeerIP(), this.entryPeer.getPort())));
		}
		else
		{
			RootMulticastor.ROOT().start(this.entryPeer.getClientPool(), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.entryPeer.getPool());
		}
	}
}
