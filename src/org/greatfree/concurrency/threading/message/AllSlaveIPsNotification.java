package org.greatfree.concurrency.threading.message;

import java.util.Map;

import org.greatfree.message.container.Notification;
import org.greatfree.util.IPPort;

// Created: 09/21/2019, Bing Li
public class AllSlaveIPsNotification extends Notification
{
	private static final long serialVersionUID = -7171758359562562649L;
	
	private IPPort rendezvousPoint;
	private Map<String, IPPort> ips;

	public AllSlaveIPsNotification(IPPort rp, Map<String, IPPort> ips)
	{
		super(ThreadingMessageType.ALL_SLAVE_IPS_NOTIFICATION);
		this.rendezvousPoint = rp;
		this.ips = ips;
	}
	
	public IPPort getRendezvousPoint()
	{
		return this.rendezvousPoint;
	}

	public Map<String, IPPort> getIPs()
	{
		return this.ips;
	}
}
