package org.greatfree.cluster.root;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.cluster.ClusterConfig;
import org.greatfree.cluster.root.container.RootServiceProvider;
import org.greatfree.dsf.multicast.message.RootIPAddressBroadcastNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.multicast.root.RootClient;
import org.greatfree.multicast.root.RootRendezvousPoint;
import org.greatfree.server.Peer;
import org.greatfree.server.Peer.PeerBuilder;
import org.greatfree.util.IPAddress;

/*
 * The multicasting for the version employs the single-node-RP one. Another version that uses the intermediate-node-RP one will be designed later. 10/23/2018, Bing Li
 */

// Created: 09/23/2018, Bing Li
class ClusterRoot
{
	private Peer<RootDispatcher> root;
	private RootClient client;
	private Map<String, String> children;
	
	private ClusterRoot()
	{
	}
	
	private static ClusterRoot instance = new ClusterRoot();
	
	public static ClusterRoot CLUSTER()
	{
		if (instance == null)
		{
			instance = new ClusterRoot();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		this.root.stop(timeout);
		this.client.close();
		this.children.clear();
		this.children = null;
	}
	
//	public void init(ServerOnCluster root,  int rootBranchCount, int treeBranchCount, long waitTime)
	public void init(PeerBuilder<RootDispatcher> builder,  int rootBranchCount, int treeBranchCount, long waitTime) throws IOException
	{
		this.root = new Peer<RootDispatcher>(builder);
		/*
		if (this.root.getPool() == null)
		{
			System.out.println("ClusterRoot-init(): the root thread pool is NULL");
		}
		else
		{
			System.out.println("ClusterRoot-init(): the root thread pool is NOT NULL");
		}
		*/
		this.client = new RootClient(this.root.getClientPool(), rootBranchCount, treeBranchCount, waitTime, this.root.getPool());
		this.children = new ConcurrentHashMap<String, String>();
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		this.root.start();
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.readRegistry(new ClusterIPRequest());
		
		if (ipResponse.getIPs() != null)
		{
			System.out.println("RootPeer-ipResponse: ip size = " + ipResponse.getIPs().size());
			
			// Add the IP addresses to the client pool. 05/08/2017, Bing Li
			for (IPAddress ip : ipResponse.getIPs().values())
			{
				System.out.println("Distributed IPs = " + ip.getIP() + ", " + ip.getPort());
				this.root.addPartners(ip.getIP(), ip.getPort());
			}
			
			this.broadcastNotify(new RootIPAddressBroadcastNotification(new IPAddress(this.root.getPeerID(), this.root.getPeerIP(), this.root.getPort())));
		}
	}

	public void addChild(String childID, String ipKey, String ip, int port)
	{
		this.children.put(childID, ipKey);
		this.root.addPartners(ip, port);
	}
	
	public void removeChild(String childID) throws IOException
	{
		this.root.removePartner(this.children.get(childID));
		this.children.remove(childID);
	}
	
	public int getChildrenCount()
	{
//		return this.root.getPartnerCount();
		return this.children.size();
	}
	
	public ServerMessage readRegistry(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.root.read(this.root.getRegistryServerIP(), this.root.getRegistryServerPort(), request);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.root.read(ip, port, request);
	}
	
	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.root.syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		this.root.asyncNotify(ip, port, notification);
	}
	
	/*
	 * The method needs to be called. But how to handle the call and the RootDispatcher is not completed for that. 10/01/2018, Bing Li
	 */
	public RootRendezvousPoint getRP()
	{
		return this.client.getRP();
	}

	public void broadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification);
	}
	
	public void anycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.anycastNotify(notification);
	}
	
	public void unicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.unicastNotify(notification);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.broadcastRead(request);
	}
	
	public List<MulticastResponse> anycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return this.client.anycastRead(request, n);
	}
	
	public List<MulticastResponse> unicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return this.client.unicastRead(request);
	}

	public void processNotification(Notification notification) throws IOException, DistributedNodeFailedException
	{
		switch (notification.getNotificationType())
		{
			case MulticastMessageType.BROADCAST_NOTIFICATION:
				if (this.children.size() > 0)
				{
					this.client.broadcastNotify(notification);
				}
				else
				{
					System.out.println("No children join!");
				}
				/*
				if (notification.getApplicationID() == ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION)
				{
					DistributedTask.CLUSTER().processNotification(notification);
				}
				*/
				break;
				
			case MulticastMessageType.ANYCAST_NOTIFICATION:
				if (this.children.size() > 0)
				{
					this.client.anycastNotify(notification);
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				
			case MulticastMessageType.UNICAST_NOTIFICATION:
//				this.client.unicastNotify(notification);
				if (this.children.size() > 0)
				{
					if (notification.getClientKey() != null)
					{
						this.client.unicastNearestNotify(notification.getClientKey(), notification);
					}
					else
					{
						this.client.unicastNotify(notification);
					}
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				
			case MulticastMessageType.LOCAL_NOTIFICATION:
				RootServiceProvider.ROOT().processNotification(notification);
				break;
		}
	}
	
	public Response processRequest(Request request) throws DistributedNodeFailedException, IOException
	{
		switch (request.getRequestType())
		{
			case MulticastMessageType.BROADCAST_REQUEST:
				return new Response(MulticastMessageType.BROADCAST_RESPONSE, this.client.broadcastRead(request));

			case MulticastMessageType.ANYCAST_REQUEST:
				return new Response(MulticastMessageType.ANYCAST_RESPONSE, this.client.anycastRead(request, ClusterConfig.ANYCAST_REQUEST_LEAST_COUNT));
				
			case MulticastMessageType.UNICAST_REQUEST:
//				return new Response(this.client.unicastRead(request));
				if (this.children.size() > 0)
				{
					if (request.getClientKey() != null)
					{
						return new Response(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastNearestRead(request.getClientKey(), request));
					}
					else
					{
						return new Response(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastRead(request));
					}
				}
				
			case MulticastMessageType.LOCAL_REQUEST:
				return RootServiceProvider.ROOT().processRequest(request);
		}
		return ClusterConfig.NO_RESPONSE;
	}
}
