package org.greatfree.cluster.root.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.cluster.ClusterConfig;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.dsf.container.p2p.message.ClusterIPRequest;
import org.greatfree.dsf.multicast.message.RootIPAddressBroadcastNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ChildResponse;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.multicast.root.RootClient;
import org.greatfree.server.container.Peer;
import org.greatfree.server.container.Peer.PeerBuilder;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * The reason to design the class, ClusterRoot, intends to connect with the registry server which is implemented in the container manner. 01/13/2019, Bing Li
 */

// Created: 01/13/2019, Bing Li
class ClusterRoot
{
	private Peer<RootDispatcher> root;
	private RootClient client;
	private Map<String, String> children;
	
	private ClusterRoot()
	{
	}
	
	private static ClusterRoot instance = new ClusterRoot();
	
	public static ClusterRoot CONTAINER()
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
	
	public void init(PeerBuilder<RootDispatcher> builder,  int rootBranchCount, int treeBranchCount, long waitTime) throws IOException
	{
		this.root = new Peer<RootDispatcher>(builder);
		this.client = new RootClient(this.root.getClientPool(), rootBranchCount, treeBranchCount, waitTime, this.root.getPool());
		this.children = new ConcurrentHashMap<String, String>();
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		this.root.start();
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.readRegistry(new ClusterIPRequest(this.root.getPeerID()));
		
		if (ipResponse.getIPs() != null)
		{
//			System.out.println("RootPeer-ipResponse: ip size = " + ipResponse.getIPs().size());
			
			// Add the IP addresses to the client pool. 05/08/2017, Bing Li
			for (IPAddress ip : ipResponse.getIPs().values())
			{
//				System.out.println("Distributed IPs = " + ip.getIP() + ", " + ip.getPort());
				this.root.addPartners(ip.getIP(), ip.getPort());
			}
			
			this.broadcastNotify(new RootIPAddressBroadcastNotification(new IPAddress(this.root.getPeerID(), this.root.getPeerIP(), this.root.getPort())));
		}
	}
	
	/*
	 * For testing only. 01/16/2019, Bing Li
	 */
	/*
	public CSClient getPeerClient()
	{
		return this.root.getPeerClient();
	}
	*/

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
	
	public ThreadPool getThreadPool()
	{
		return this.root.getPool();
	}
	
	public int getChildrenCount()
	{
//		return this.root.getPartnerCount();
		return this.children.size();
	}
	

	/*
	 * Now I need to implement the root based intercasting. So the method is not necessary temporarily. I will implement the children-based intercasing later. 02/15/2019, Bing Li 
	 */
	/*
	private IPAddress getChildIP(String childID)
	{
		return this.root.getIP(this.children.get(childID));
	}
	*/
	
	public ServerMessage readRegistry(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.root.read(this.root.getRegistryServerIP(), this.root.getRegistryServerPort(), request);
	}
	
	/*
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
	*/
	
	/*
	 * 
	 * The method is not so concise. So it is replaced by the one saveResponse(ChildResponse response). 03/04/2019, Bing Li
	 * 
	 * The method needs to be called. But how to handle the call and the RootDispatcher is not completed for that. 10/01/2018, Bing Li
	 */
	/*
	public RootRendezvousPoint getRP()
	{
		return this.client.getRP();
	}
	*/

	/*
	 * The method is more concise than the above one.03/04/2019, Bing Li
	*/
	public void saveResponse(ChildResponse response) throws InterruptedException
	{
		this.client.getRP().saveResponse(response.getResponse());
	}

	public void broadcastNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification);
	}

	public void asyncBroadcastNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncBroadcastNotify(notification);
	}
	
	public void anycastNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.anycastNotify(notification);
	}
	
	public void asyncAnycastNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncAnycastNotify(notification);
	}
	
	public void unicastNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.unicastNotify(notification);
	}
	
	public void asyncUnicastNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncUnicastNotify(notification);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.broadcastRead(request);
	}
	
	public List<MulticastResponse> asyncBroadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.asyncBroadcastRead(request);
	}
	
	public List<MulticastResponse> anycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return this.client.anycastRead(request, n);
	}
	
	public List<MulticastResponse> asyncAnycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return this.client.asyncAnycastRead(request, n);
	}
	
	public List<MulticastResponse> unicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return this.client.unicastRead(request);
	}
	
	public List<MulticastResponse> asyncUnicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return this.client.asyncUnicastRead(request);
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

				/*
				 * I am implementing the root-based intercasting. It seems that the below lines are not necessary temporarily. 02/15/2019, Bing Li
				 */
				/*
			case MulticastMessageType.INTERCAST_NOTIFICATION:
				if (this.children.size() > 0)
				{
					IntercastNotification in = (IntercastNotification)notification;
//					in.setDestinationIP(this.getChildIP(in.getDestinationKey()));
//					this.client.unicastNearestNotify(in.getSourceKey(), in);
//					this.client.unicastNearestNotify(in.getDestinationKey(), in);
//					this.client.unicastNearestNotify(in.getClientKey(), in);

					if (!in.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
					{
						in.setDestinationIP(this.root.getIP(this.children.get(in.getDestinationKey())));
					}
					else
					{
						Set<String> destinationKeys = in.getDestinationKeys();
						Set<IPAddress> ips = Sets.newHashSet();
						Map<String, Set<String>> cds = new HashMap<String, Set<String>>();
						IPAddress ip;
						for (String entry : destinationKeys)
						{
							ip = this.root.getIP(this.children.get(entry));
							ips.add(ip);
							if (!cds.containsKey(ip.getIPKey()))
							{
								Set<String> ds = Sets.newHashSet();
								cds.put(ip.getIPKey(), ds);
							}
							cds.get(ip.getIPKey()).add(entry);
						}
						in.setDestinationIPs(ips);
						in.setChildDestination(cds);
					}
					
					this.client.unicastNearestNotify(in.getSourceKey(), in);
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				*/

				/*
				 * The intercasting is based on notifications. But the related messages have a superficial view as a request if responses are required. But the typical request/response based on TCP stream is available between a typical client and the server. Within a cluster, it is preferred to employ notifications. I need to verify it works or not. 02/26/2019, Bing Li
				 */
				/*
			case MulticastMessageType.INTERCAST_REQUEST:
				if (this.children.size() > 0)
				{
					IntercastRequest ir = (IntercastRequest)notification;
//					ir.setDestinationIP(this.getChildIP(ir.getDestinationKey()));
//					this.client.unicastNearestNotify(ir.getSourceKey(), ir);
//					return new Response(this.client.unicastNearestRead(ir.getDestinationKey(), ir));
//					return new Response(this.client.unicastNearestRead(ir.getClientKey(), ir));
//					this.client.unicastNearestNotify(ir.getClientKey(), ir);
					this.client.unicastNearestNotify(ir.getSourceKey(), ir);
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				*/

				/*
				 * Since the root-based intercasting is abandoned, the below code is not useful, i.e., it is not necessary to perform intercasting through the root. 03/02/2019, Bing Li
				 * 
			case MulticastMessageType.INTER_CHILD_UNICAST_NOTIFICATION:
				InterChildrenNotification uicn = (InterChildrenNotification)notification;
				this.client.unicastNearestNotify(uicn.getDestinationKey(), uicn);
				break;
				
			case MulticastMessageType.INTER_CHILD_BROADCAST_NOTIFICATION:
				InterChildrenNotification bicn = (InterChildrenNotification)notification;
				this.client.broadcastNotify(bicn, bicn.getDestinationKeys());
				break;
				
			case MulticastMessageType.INTER_CHILD_ANYCAST_NOTIFICATION:
				InterChildrenNotification aicn = (InterChildrenNotification)notification;
				this.client.anycastNotify(aicn, aicn.getDestinationKeys());
				break;
				*/

				/*
			case MulticastMessageType.INTER_CHILD_UNICAST_REQUEST:
				IntercastChildRequest uicr = (IntercastChildRequest)notification;
				this.client.unicastNearestRead(uicr.getDestinationKey(), uicr);
				break;
				
			case MulticastMessageType.INTER_CHILD_BROADCAST_REQUEST:
				break;
				
			case MulticastMessageType.INTER_CHILD_ANYCAST_REQUEST:
				break;
				*/
		}
	}
	
	public Response processRequest(Request request) throws DistributedNodeFailedException, IOException, ClassNotFoundException, RemoteReadException
	{
		switch (request.getRequestType())
		{
			case MulticastMessageType.BROADCAST_REQUEST:
				if (this.children.size() > 0)
				{
					return new Response(MulticastMessageType.BROADCAST_RESPONSE, this.client.broadcastRead(request));
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				
			case MulticastMessageType.ANYCAST_REQUEST:
				if (this.children.size() > 0)
				{
					return new Response(MulticastMessageType.ANYCAST_RESPONSE, this.client.anycastRead(request, ClusterConfig.ANYCAST_REQUEST_LEAST_COUNT));
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				
			case MulticastMessageType.UNICAST_REQUEST:
				if (this.children.size() > 0)
				{
					// This key is important. Developers can set the value. So they can decide how to balance the load. For example, in the case of S3, all of the encoded data slices for the same encoding block can be sent to a unique child for merging. The client key can be the ID of the encoding block. 07/11/2020, Bing Li
					if (request.getClientKey() != null)
					{
						return new Response(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastNearestRead(request.getClientKey(), request));
					}
					else
					{
						return new Response(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastRead(request));
					}
				}
				else
				{
					System.out.println("No children join!");
				}
				break;

				/*
				 * I am implementing the root-based intercasting. It seems that the below lines are not necessary temporarily. 02/15/2019, Bing Li
				 */
				/*
			case MulticastMessageType.INTERCAST_REQUEST:
				if (this.children.size() > 0)
				{
					IntercastRequest ir = (IntercastRequest)request;
//					ir.setDestinationIP(this.getChildIP(ir.getDestinationKey()));
//					this.client.unicastNearestNotify(ir.getSourceKey(), ir);
//					return new Response(this.client.unicastNearestRead(ir.getDestinationKey(), ir));
					return new Response(this.client.unicastNearestRead(ir.getClientKey(), ir));
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				*/
		
			case MulticastMessageType.LOCAL_REQUEST:
				return RootServiceProvider.ROOT().processRequest(request);

				/*
			case MulticastMessageType.INTERCAST_REQUEST:
				if (this.children.size() > 0)
				{
					IntercastRequest ir = (IntercastRequest)request;
//					ir.setDestinationIP(this.getChildIP(ir.getDestinationKey()));
//					this.client.unicastNearestNotify(ir.getSourceKey(), ir);
//					return new Response(this.client.unicastNearestRead(ir.getDestinationKey(), ir));
//					return new Response(this.client.unicastNearestRead(ir.getClientKey(), ir));
//					this.client.unicastNearestNotify(ir.getClientKey(), ir);
					
					if (!ir.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
					{
						ir.setDestinationIP(this.root.getIP(this.children.get(ir.getDestinationKey())));
					}
					else
					{
						Set<String> destinationKeys = ir.getDestinationKeys();
						Set<IPAddress> ips = Sets.newHashSet();
						Map<String, Set<String>> cds = new HashMap<String, Set<String>>();
						IPAddress ip;
						for (String entry : destinationKeys)
						{
							ip = this.root.getIP(this.children.get(entry));
							ips.add(ip);
							if (!cds.containsKey(ip.getIPKey()))
							{
								Set<String> ds = Sets.newHashSet();
								cds.put(ip.getIPKey(), ds);
							}
							cds.get(ip.getIPKey()).add(entry);
						}
						ir.setDestinationIPs(ips);
						ir.setChildDestination(cds);
					}
					
//					this.client.unicastNearestNotify(ir.getSourceKey(), ir);
//					return this.client.unicastNearestRead(ir.getSourceKey(), ir);
					IPAddress sourceIP = this.root.getIP(this.children.get(ir.getSourceKey()));
					return (Response)this.root.read(sourceIP.getIP(), sourceIP.getPort(), ir);
				}
				else
				{
					System.out.println("No children join!");
				}
				break;
				*/

				/*
				 * Since the root-based intercasting is abandoned, the below code is not useful, i.e., it is not necessary to perform intercasting through the root. 03/02/2019, Bing Li
				 * 
			case MulticastMessageType.INTER_CHILD_UNICAST_REQUEST:
				InterChildrenRequest uicr = (InterChildrenRequest)request;
				return new Response(this.client.unicastNearestRead(uicr.getDestinationKey(), uicr));
				
			case MulticastMessageType.INTER_CHILD_BROADCAST_REQUEST:
				InterChildrenRequest bicr = (InterChildrenRequest)request;
				return new Response(this.client.broadcastRead(bicr, bicr.getDestinationKeys()));
				
			case MulticastMessageType.INTER_CHILD_ANYCAST_REQUEST:
				InterChildrenRequest aicr = (InterChildrenRequest)request;
				return new Response(this.client.anycastRead(aicr, aicr.getDestinationKeys()));
				*/
		}
		return ClusterConfig.NO_RESPONSE;
	}
	
	public void processIntercastNotification(IntercastNotification in) throws IOException, InterruptedException
	{
		if (this.children.size() > 0)
		{
//			IntercastNotification in = (IntercastNotification)notification;
//			in.setDestinationIP(this.getChildIP(in.getDestinationKey()));
//			this.client.unicastNearestNotify(in.getSourceKey(), in);
//			this.client.unicastNearestNotify(in.getDestinationKey(), in);
//			this.client.unicastNearestNotify(in.getClientKey(), in);

			IPAddress ip;
			Set<IPAddress> ips = Sets.newHashSet();
			Map<String, Set<String>> cds = new HashMap<String, Set<String>>();
			/*
			 * When receiving an intercast notification, the root needs to set the destination IP addresses for its children since it has all of the IP addresses of the children in the cluster. 02/28/2019, Bing Li
			 */
			if (!in.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
			{
//				in.setDestinationIP(this.root.getIP(this.children.get(in.getDestinationKey())));
				ip = this.root.getIP(this.client.getNearestChildKey(in.getDestinationKey()));
				in.setDestinationIP(ip);
				Set<String> ds = Sets.newHashSet();
				cds.put(ip.getIPKey(), ds);
				cds.get(ip.getIPKey()).add(in.getDestinationKey());
				in.setChildDestination(cds);
			}
			else
			{
				Set<String> destinationKeys = in.getDestinationKeys();
				for (String entry : destinationKeys)
				{
//					ip = this.root.getIP(this.children.get(entry));
					ip = this.root.getIP(this.client.getNearestChildKey(entry));
					ips.add(ip);
					if (!cds.containsKey(ip.getIPKey()))
					{
						Set<String> ds = Sets.newHashSet();
						cds.put(ip.getIPKey(), ds);
					}
					cds.get(ip.getIPKey()).add(entry);
				}
				in.setDestinationIPs(ips);
				in.setChildDestination(cds);
			}
			
//			this.client.unicastNearestNotify(in.getSourceKey(), in);
//			IPAddress ip = this.root.getIP(this.children.get(in.getSourceKey()));
			ip = this.root.getIP(this.client.getNearestChildKey(in.getSourceKey()));
			this.root.syncNotify(ip.getIP(), ip.getPort(), in);
		}
		else
		{
			System.out.println("No children join!");
		}
	}

	public Response processIntercastRequest(IntercastRequest ir) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (this.children.size() > 0)
		{
//			IntercastRequest ir = (IntercastRequest)request;
//			ir.setDestinationIP(this.getChildIP(ir.getDestinationKey()));
//			this.client.unicastNearestNotify(ir.getSourceKey(), ir);
//			return new Response(this.client.unicastNearestRead(ir.getDestinationKey(), ir));
//			return new Response(this.client.unicastNearestRead(ir.getClientKey(), ir));
//			this.client.unicastNearestNotify(ir.getClientKey(), ir);
			
			IPAddress ip;
			Set<IPAddress> ips = Sets.newHashSet();
			Map<String, Set<String>> cds = new HashMap<String, Set<String>>();
			/*
			 * When receiving an intercast notification, the root needs to set the destination IP addresses for its children since it has all of the IP addresses of the children in the cluster. 02/28/2019, Bing Li
			 */
			if (!ir.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
			{
//				ir.setDestinationIP(this.root.getIP(this.children.get(ir.getDestinationKey())));
				ip = this.root.getIP(this.client.getNearestChildKey(ir.getDestinationKey()));
				ir.setDestinationIP(ip);
				Set<String> ds = Sets.newHashSet();
				cds.put(ip.getIPKey(), ds);
				cds.get(ip.getIPKey()).add(ir.getDestinationKey());
				ir.setChildDestination(cds);
			}
			else
			{
				Set<String> destinationKeys = ir.getDestinationKeys();
				for (String entry : destinationKeys)
				{
//					ip = this.root.getIP(this.children.get(entry));
					ip = this.root.getIP(this.client.getNearestChildKey(entry));
					ips.add(ip);
					if (!cds.containsKey(ip.getIPKey()))
					{
						Set<String> ds = Sets.newHashSet();
						cds.put(ip.getIPKey(), ds);
					}
					cds.get(ip.getIPKey()).add(entry);
				}
				ir.setDestinationIPs(ips);
				ir.setChildDestination(cds);
			}
			
//			this.client.unicastNearestNotify(ir.getSourceKey(), ir);
//			return this.client.unicastNearestRead(ir.getSourceKey(), ir);
//			IPAddress sourceIP = this.root.getIP(this.children.get(ir.getSourceKey()));
			IPAddress sourceIP = this.root.getIP(this.client.getNearestChildKey(ir.getSourceKey()));
			return (Response)this.root.read(sourceIP.getIP(), sourceIP.getPort(), ir);
		}
		else
		{
			System.out.println("No children join!");
		}
		return ClusterConfig.NO_RESPONSE;
	}
}
