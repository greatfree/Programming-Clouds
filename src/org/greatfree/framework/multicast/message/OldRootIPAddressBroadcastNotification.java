package org.greatfree.framework.multicast.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldRootIPAddressBroadcastNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = -2510474973227273290L;

	private IPAddress rootAddress;

	public OldRootIPAddressBroadcastNotification(IPAddress rootAddress)
	{
		super(MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION);
		this.rootAddress = rootAddress;
	}

	public OldRootIPAddressBroadcastNotification(HashMap<String, IPAddress> childrenServers, IPAddress rootAddress)
	{
		super(MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION, childrenServers);
		this.rootAddress = rootAddress;
	}

	public IPAddress getRootAddress()
	{
		return this.rootAddress;
	}
}
