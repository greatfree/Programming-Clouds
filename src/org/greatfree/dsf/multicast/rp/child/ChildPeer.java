package org.greatfree.dsf.multicast.rp.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.dsf.p2p.message.ChatRegistryRequest;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 10/21/2018, Bing Li
public class ChildPeer
{
	private Peer<ChildDispatcher> peer;
	
	private IPAddress localIP;
	private IPAddress parentAddress;

	private ChildPeer()
	{
	}
	
	private static ChildPeer instance = new ChildPeer();
	
	public static ChildPeer CHILD()
	{
		if (instance == null)
		{
			instance = new ChildPeer();
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
		ChildMulticastor.CHILD().stop();

		TerminateSignal.SIGNAL().setTerminated();
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the child peer. 05/12/2017, Bing Li
		this.peer = new Peer.PeerBuilder<ChildDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new ChildDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChildDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
				.build();

		// Start the child peer. 05/12/2017, Bing Li
		this.peer.start();

		this.localIP = new IPAddress(this.peer.getPeerIP(), this.peer.getPort());

		// Start the client. 08/26/2018, Bing Li
		ChildMulticastor.CHILD().start(this.peer.getLocalIPKey(), this.peer.getClientPool(), MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.peer.getPool());

		// No. The line is useful although it is a little bit tedious. 10/09/2018, Bing Li
		// The below should not be useful in the multicasting DIP. 10/09/2018, Bing Li
		// Register the peer to the chatting registry. 06/15/2017, Bing Li
		this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.peer.getPeerID()));
	}

	public IPAddress getLocalIP()
	{
		return this.localIP;
	}

	/*
	 * Keep the parent IP address. 12/17/2018, Bing Li
	 */
	public void setParentIP(IPAddress parentAddress)
	{
		this.parentAddress = parentAddress;
	}
	
	public IPAddress getParentAddress()
	{
		return this.parentAddress;
	}

	/*
	 * Respond the response to the root of the cluster. 05/20/2017, Bing Li
	 */
	/*
	public void notifyRP(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip.getIP(), ip.getPort(), notification);
	}
	*/
}
