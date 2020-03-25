package org.greatfree.cluster.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.cluster.message.IsRootOnlineRequest;
import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.dip.p2p.message.ChatRegistryRequest;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ChildResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.multicast.child.ChildClient;
import org.greatfree.server.Peer;
import org.greatfree.server.Peer.PeerBuilder;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.util.IPAddress;

// Created: 09/23/2018, Bing Li
class Child
{
//	private ChildClient client;
	// The IP address of the cluster root. 06/15/2017, Bing Li
	private IPAddress rootAddress;
//	private ClusterChild child;
	private Peer<ChildDispatcher> child;
	private ChildClient client;
	
	private Child()
	{
	}
	
	private static Child instance = new Child();
	
	public static Child CLUSTER()
	{
		if (instance == null)
		{
			instance = new Child();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		this.child.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new LeaveNotification(this.child.getPeerID()));
		this.child.stop(timeout);
		this.client.close();
	}
	
//	public void init(PeerBuilder<ChildDispatcher> builder, int treeBranchCount) throws IOException
	public void init(PeerBuilder<ChildDispatcher> builder) throws IOException
	{
		this.child = new Peer<ChildDispatcher>(builder);
	}
	
	public void start(String rootKey, int treeBranchCount) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		this.child.start();
		this.client = new ChildClient(child.getLocalIPKey(), child.getClientPool(), treeBranchCount, child.getPool());
		IsRootOnlineResponse response;
		// Register the peer to the chatting registry. 06/15/2017, Bing Li
		if (!ServerProfile.CS().isDefault())
		{
			this.child.read(PeerProfile.P2P().getRegistryServerIP(), PeerProfile.P2P().getRegistryServerPort(), new ChatRegistryRequest(this.child.getPeerID()));
			response = (IsRootOnlineResponse)this.child.read(PeerProfile.P2P().getRegistryServerIP(), PeerProfile.P2P().getRegistryServerPort(), new IsRootOnlineRequest(rootKey));
		}
		else
		{
			this.child.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.child.getPeerID()));
			response = (IsRootOnlineResponse)this.child.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new IsRootOnlineRequest(rootKey));
		}
		if (response.isOnline())
		{
			System.out.println("ClusterChild-start(): root address = " + response.getRootAddress().getIP() + ":" + response.getRootAddress().getPort());
			this.setRootIP(response.getRootAddress());
//			this.child.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new JoinNotification(this.child.getPeerID()));
			this.joinCluster();
			System.out.println("ClusterChild-start(): done!");
		}
		else
		{
			System.out.println("ClusterChild-start(): root is not online! ");
		}
	}

	public void joinCluster() throws IOException, InterruptedException
	{
		this.child.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new JoinNotification(this.child.getPeerID()));
	}

	/*
	 * Keep the root IP address. 05/20/2017, Bing Li
	 */
	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}
	
	public IPAddress getIPAddress()
	{
		return this.rootAddress;
	}
	
	public void forward(Notification notification)
	{
		/*
		if (notification.getNotificationType() == ClusterMessageType.BROADCAST_NOTIFICATION)
		{
			this.asyncNotify(notification);
		}
		*/
		this.asyncNotify(notification);
	}
	
	public void forward(Request request)
	{
		/*
		if (request.getRequestType() == ClusterMessageType.BROADCAST_REQUEST)
		{
			this.asyncRead(request);
		}
		*/
		this.asyncRead(request);
	}

	public void notify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.notify(notification);
	}
	
	public void asyncNotify(MulticastMessage notification)
	{
		this.client.asynNotify(notification);
	}
	
	public void read(MulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.read(request);
	}
	
	public void asyncRead(MulticastRequest request)
	{
		this.client.asyncRead(request);
	}
	
	public void notifyRoot(MulticastResponse response) throws IOException, InterruptedException
	{
//		System.out.println("ClusterChild-notifyRoot(): response is being sent ......");
		this.child.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), new ChildResponse(response));
//		System.out.println("ClusterChild-notifyRoot(): response was sent ......");
	}
}
