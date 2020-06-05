package ca.streaming.news.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.multicast.child.ChildMulticastor;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.dip.p2p.message.ChatRegistryRequest;
import org.greatfree.dip.streaming.broadcast.child.RootMulticastor;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 04/01/2020, Bing Li
class NewsChildPeer
{
	private Peer<NewsChildDispatcher> peer;

	private IPAddress rootAddress;

	private NewsChildPeer()
	{
	}
	
	private static NewsChildPeer instance = new NewsChildPeer();
	
	public static NewsChildPeer UNICAST()
	{
		if (instance == null)
		{
			instance = new NewsChildPeer();
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
		RootMulticastor.CHILD_STREAM().stop();

		TerminateSignal.SIGNAL().setTerminated();
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder<NewsChildDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new NewsChildDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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

		ChildMulticastor.CHILD().start(this.peer.getLocalIPKey(), this.peer.getClientPool(), MulticastConfig.SUB_BRANCH_COUNT, this.peer.getPool());

		RootMulticastor.CHILD_STREAM().start(this.peer.getClientPool(), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.peer.getPool());
		
		this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.peer.getPeerID()));
	}

	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}

	public void notifyRoot(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}

}
