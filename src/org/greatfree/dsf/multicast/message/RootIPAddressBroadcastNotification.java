package org.greatfree.dsf.multicast.message;

import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.util.IPAddress;

/*
 * The notification is a message to be sent to distributed nodes about the root IP address in the manner of broadcasting. 05/08/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class RootIPAddressBroadcastNotification extends MulticastNotification
{
	private static final long serialVersionUID = -3404599339423965092L;
	
	private IPAddress rootAddress;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public RootIPAddressBroadcastNotification(String key, IPAddress rootAddress)
	public RootIPAddressBroadcastNotification(IPAddress rootAddress)
	{
//		super(MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION, Tools.generateUniqueKey());
		super(MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION);
		this.rootAddress = rootAddress;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public RootIPAddressBroadcastNotification(String key, HashMap<String, IPAddress> childrenServers, IPAddress rootAddress)
	/*
	public RootIPAddressBroadcastNotification(HashMap<String, IPAddress> childrenServers, IPAddress rootAddress)
	{
//		super(MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION, Tools.generateUniqueKey(), childrenServers);
		super(MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION, childrenServers);
		this.rootAddress = rootAddress;
	}
	*/

	public IPAddress getRootAddress()
	{
		return this.rootAddress;
	}
}
