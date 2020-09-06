package org.greatfree.dsf.streaming.unicast.root;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.multicast.message.RootIPAddressBroadcastNotification;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;

// Created: 03/22/2020, Bing Li
class StreamRootPeer
{
	private Peer<StreamRootDispatcher> peer;
	
	private StreamRootPeer()
	{
	}
	
	private static StreamRootPeer instance = new StreamRootPeer();
	
	public static StreamRootPeer UNICAST()
	{
		if (instance == null)
		{
			instance = new StreamRootPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.peer.stop(timeout);
		RootMulticastor.UNI_STREAM().stop();

		TerminateSignal.SIGNAL().setTerminated();
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException, InstantiationException, IllegalAccessException, InterruptedException, DistributedNodeFailedException
	{
		this.peer = new Peer.PeerBuilder<StreamRootDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(MulticastConfig.CLUSTER_SERVER_ROOT_NAME)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new StreamRootDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();

		this.peer.start();

		ClusterIPResponse ipResponse = (ClusterIPResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new ClusterIPRequest());
		
		if (ipResponse.getIPs() != null)
		{
			System.out.println("RootPeer-ipResponse: ip size = " + ipResponse.getIPs().size());
			
			for (IPAddress ip : ipResponse.getIPs().values())
			{
				System.out.println("Distributed IPs = " + ip.getIP() + ", " + ip.getPort());
				this.peer.addPartners(ip.getIP(), ip.getPort());
			}
			
			RootMulticastor.UNI_STREAM().start(this.peer.getClientPool(), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.peer.getPool());
			RootMulticastor.UNI_STREAM().broadcastNotify(new RootIPAddressBroadcastNotification(new IPAddress(this.peer.getPeerID(), this.peer.getPeerIP(), this.peer.getPort())));
		}
		else
		{
			RootMulticastor.UNI_STREAM().start(this.peer.getClientPool(), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.peer.getPool());
		}
	}

}
