package org.greatfree.cluster.root.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.greatfree.cluster.ClusterConfig;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.ClusterIPRequest;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ChildResponse;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.message.multicast.container.RootAddressNotification;
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
	private int replicas;
	private List<Set<String>> partitionedChildren;

	private final static Logger log = Logger.getLogger("org.greatfree.cluster.root.container");

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
		if (this.partitionedChildren != null)
		{
			this.partitionedChildren.clear();
			this.partitionedChildren = null;
		}
	}
	
	public void init(PeerBuilder<RootDispatcher> builder,  int rootBranchCount, int treeBranchCount, long waitTime) throws IOException
	{
		this.root = new Peer<RootDispatcher>(builder);
		this.client = new RootClient(this.root.getClientPool(), rootBranchCount, treeBranchCount, waitTime, this.root.getPool());
		this.children = new ConcurrentHashMap<String, String>();
		this.replicas = 0;
		this.partitionedChildren = null;
	}
	
	public void init(PeerBuilder<RootDispatcher> builder,  int rootBranchCount, int treeBranchCount, long waitTime, int replicas) throws IOException
	{
		this.root = new Peer<RootDispatcher>(builder);
		this.client = new RootClient(this.root.getClientPool(), rootBranchCount, treeBranchCount, waitTime, this.root.getPool());
		this.children = new ConcurrentHashMap<String, String>();
		this.replicas = replicas;
		this.partitionedChildren = new CopyOnWriteArrayList<Set<String>>();
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		this.root.start();
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.readRegistry(new ClusterIPRequest(this.root.getPeerID()));
		
		if (ipResponse.getIPs() != null)
		{
//			log.info("ClusterRoot-start(): ip size = " + ipResponse.getIPs().size());

			// Add the IP addresses to the client pool. 05/08/2017, Bing Li
			for (IPAddress ip : ipResponse.getIPs().values())
			{
//				log.info("Distributed IPs = " + ip.getIP() + ", " + ip.getPort());
				this.root.addPartners(ip.getIP(), ip.getPort());
//				this.partitionChild(ip.getIPKey());
			}
			
			this.broadcastNotify(new RootAddressNotification(new IPAddress(this.root.getPeerID(), this.root.getPeerIP(), this.root.getPort())));
		}
	
//		int partitionSize = ipResponse.getIPs().size() / this.replicas + 1;
//		Set<String> childrenKeys;
		// The commented line is a bug. 09/08/2020, Bing Li
		/*
		for (int i = 0; i < this.replicas; i++)
		{
			childrenKeys = Sets.newHashSet();
			this.partitionedChildren.add(childrenKeys);
		}
		*/
	}

	/*
	 * Partition the IP keys to implement the replication among the partitions. 09/07/2020, Bing Li
	 */
	private void partitionChild(String ipKey)
	{
		if (this.partitionedChildren.size() > 0)
		{
			int index = 0;
//			int currentMaxSize = this.partitionedChildren.get(minIndex).size();
			boolean isFound = false;
			for (int i = 0; i < this.partitionedChildren.size(); i++)
			{
//				if (this.partitionedChildren.get(i).size() < currentMaxSize)
				if (this.partitionedChildren.get(i).size() < this.replicas)
				{
//					currentMaxSize = this.partitionedChildren.get(i).size();
					index = i;
					isFound = true;
					break;
				}
			}
			if (!isFound)
			{
				Set<String> childrenKeys = Sets.newHashSet();
				childrenKeys.add(ipKey);
				this.partitionedChildren.add(childrenKeys);
			}
			else
			{
				this.partitionedChildren.get(index).add(ipKey);
			}
		}
		else
		{
			Set<String> childrenKeys = Sets.newHashSet();
			childrenKeys.add(ipKey);
			this.partitionedChildren.add(childrenKeys);
		}
	}
	
	public int getPartitionSize()
	{
		return this.partitionedChildren.size();
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
		if (this.replicas != ClusterConfig.NO_REPLICAS)
		{
			this.partitionChild(ipKey);
		}
	}
	
	public void removeChild(String childID) throws IOException
	{
		String ipKey = this.children.get(childID);
		this.root.removePartner(ipKey);
		this.children.remove(childID);
		
		if (this.replicas != ClusterConfig.NO_REPLICAS)
		{
			// Usually, the size of the partition is NOT large. So the below algorithm is acceptable. 09/07/2020, Bing Li
			int index = 0;
			for (int i = 0; i < this.partitionedChildren.size(); i++)
			{
				if (this.partitionedChildren.get(i).contains(ipKey))
				{
					index = i;
					this.partitionedChildren.get(index).remove(ipKey);
					break;
				}
			}
			if (this.partitionedChildren.get(index).size() <= 0)
			{
				this.partitionedChildren.remove(index);
			}
		}
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
	 * The method is invoked when the cluster plays the role of a pool. The children are selected to leave for the task cluster. 09/13/2020, Bing Li
	 */
	public Set<String> getChildrenKeys(int size)
	{
		return this.client.getChildrenKeys(size);
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

	public void broadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification);
	}
	
	private void printPartition()
	{
		log.info("=====================");
		for (int i = 0; i < this.partitionedChildren.size(); i++)
		{
			for (String childKey : this.partitionedChildren.get(i))
			{
				log.info(i + ") " + childKey);
			}
		}
		log.info("=====================");
	}
	
//	public void broadcastNotifyByPartition(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	public void broadcastNotifyByPartition(MulticastNotification notification, int partitionIndex) throws IOException, DistributedNodeFailedException
	{
		log.info("ClusterRoot-broadcastNotify(): partitionIndex = " + partitionIndex);
		this.printPartition();
		this.client.broadcastNotify(notification, this.partitionedChildren.get(partitionIndex));
	}
	
	public void broadcastNotify(MulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification, childrenKeys);
	}
	
	public void broadcastNotifyWithinNChildren(MulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification, childrenSize);
	}

	public void asyncBroadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncBroadcastNotify(notification);
	}
	
	public void asyncBroadcastNotify(MulticastNotification notification, int partitionIndex)
	{
		this.client.asyncBroadcastNotify(notification, this.partitionedChildren.get(partitionIndex));
	}
	
	public void anycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.anycastNotify(notification);
	}
	
	public void asyncAnycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncAnycastNotify(notification);
	}
	
	public void unicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.unicastNotify(notification);
	}
	
	public void asyncUnicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncUnicastNotify(notification);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.broadcastRead(request);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException
	{
		return this.client.broadcastRead(request, childrenKeys);
	}
	
	public MulticastResponse broadcastReadByPartition(MulticastRequest request, int partitionIndex) throws DistributedNodeFailedException, IOException
	{
//		log.info("ClusterRoot-broadcastRead(): partitionIndex = " + partitionIndex);
		this.printPartition();
		return this.client.broadcastReadByPartition(request, this.partitionedChildren.get(partitionIndex));
	}
	
	public List<MulticastResponse> asyncBroadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.asyncBroadcastRead(request);
	}
	
	public MulticastResponse asyncBroadcastRead(MulticastRequest request, int partitionIndex)
	{
		return this.client.asyncBroadcastReadByPartition(request, this.partitionedChildren.get(partitionIndex));
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

	public void processNotification(ClusterNotification notification) throws IOException, DistributedNodeFailedException
	{
		switch (notification.getNotificationType())
		{
			case MulticastMessageType.BROADCAST_NOTIFICATION:
				if (this.children.size() > 0)
				{
//					log.info("ClusterRoot-processNotification(): replicas = " + this.replicas);
//					if (this.replicas == ClusterConfig.NO_REPLICAS)
//					if (notification.getPartitionIndex() == ClusterConfig.NO_PARTITION_INDEX)
					if (notification.getChildrenKeys() != ClusterConfig.NO_CHILDREN_KEYS)
					{
						this.client.broadcastNotify(notification, notification.getChildrenKeys());
					}
					else if (notification.getPartitionIndex() != ClusterConfig.NO_PARTITION_INDEX)
					{
						this.broadcastNotifyByPartition(notification, notification.getPartitionIndex());
					}
					else
					{
						this.client.broadcastNotify(notification);
					}
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.ANYCAST_NOTIFICATION:
				if (this.children.size() > 0)
				{
					this.client.anycastNotify(notification);
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.UNICAST_NOTIFICATION:
				if (this.children.size() > 0)
				{
					if (notification.getClientKey() != null)
					{
						log.info("unicastNearestNotify ...");
						this.client.unicastNearestNotify(notification.getClientKey(), notification);
					}
					else
					{
						log.info("unicastNotify ...");
						this.client.unicastNotify(notification);
					}
				}
				else
				{
					log.info("No children join!");
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
					log.info("No children join!");
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
					log.info("No children join!");
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
	
	public CollectedClusterResponse processRequest(ClusterRequest request) throws DistributedNodeFailedException, IOException, ClassNotFoundException, RemoteReadException
	{
		switch (request.getRequestType())
		{
			case MulticastMessageType.BROADCAST_REQUEST:
				if (this.children.size() > 0)
				{
//					if (this.replicas != ClusterConfig.NO_REPLICAS)
//					if (request.getPartitionIndex() == ClusterConfig.NO_PARTITION_INDEX)
					if (request.getChildrenKeys() != ClusterConfig.NO_CHILDREN_KEYS)
					{
						return new CollectedClusterResponse(MulticastMessageType.BROADCAST_RESPONSE, this.client.broadcastRead(request, request.getChildrenKeys()));
					}
					else if (request.getPartitionIndex() != ClusterConfig.NO_PARTITION_INDEX)
					{
						return new CollectedClusterResponse(MulticastMessageType.BROADCAST_RESPONSE, this.broadcastReadByPartition(request, request.getPartitionIndex()));
					}
					else
					{
						return new CollectedClusterResponse(MulticastMessageType.BROADCAST_RESPONSE, this.client.broadcastRead(request));
					}
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.ANYCAST_REQUEST:
				if (this.children.size() > 0)
				{
					return new CollectedClusterResponse(MulticastMessageType.ANYCAST_RESPONSE, this.client.anycastRead(request, ClusterConfig.ANYCAST_REQUEST_LEAST_COUNT));
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.UNICAST_REQUEST:
				if (this.children.size() > 0)
				{
					// This key is important. Developers can set the value. So they can decide how to balance the load. For example, in the case of S3, all of the encoded data slices for the same encoding block can be sent to a unique child for merging. The client key can be the ID of the encoding block. 07/11/2020, Bing Li
					if (request.getClientKey() != null)
					{
						log.info("ClusterRoot-processRequest(): clientKey = " + request.getClientKey());
						
//						List<MulticastResponse> res = this.client.unicastNearestRead(request.getClientKey(), request);
//						log.info("ClusterRoot-processRequest(): res received (size) = " + res.size());
//						return new CollectedClusterResponse(MulticastMessageType.UNICAST_RESPONSE, res);
						return new CollectedClusterResponse(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastNearestRead(request.getClientKey(), request));
					}
					else
					{
						return new CollectedClusterResponse(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastRead(request));
					}
				}
				else
				{
					log.info("No children join!");
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
					log.info("No children join!");
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
					log.info("No children join!");
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
			log.info("No children join!");
		}
	}

	public CollectedClusterResponse processIntercastRequest(IntercastRequest ir) throws ClassNotFoundException, RemoteReadException, IOException
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
			return (CollectedClusterResponse)this.root.read(sourceIP.getIP(), sourceIP.getPort(), ir);
		}
		else
		{
			log.info("No children join!");
		}
		return ClusterConfig.NO_RESPONSE;
	}
	
	public ChildRootResponse processRequest(ChildRootRequest request)
	{
		return RootServiceProvider.ROOT().processChildRequest(request);
	}
}
