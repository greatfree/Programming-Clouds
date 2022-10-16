package org.greatfree.framework.container.p2p.message;

import java.util.Map;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 10/14/2022
 *
 */
public class MulticastChildrenResponse extends ServerMessage
{
	private static final long serialVersionUID = 7938185948734589734L;

	private Map<String, IPAddress> ips;

	public MulticastChildrenResponse(Map<String, IPAddress> ips)
	{
		super(SystemMessageType.MULTICAST_CHILDREN_RESPONSE);
		this.ips = ips;
	}

	public MulticastChildrenResponse()
	{
		super(SystemMessageType.MULTICAST_CHILDREN_RESPONSE);
		this.ips = null;
	}

	public Map<String, IPAddress> getIPs()
	{
		return this.ips;
	}
}
