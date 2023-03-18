package org.greatfree.framework.cluster.multicast.message;

import java.util.Set;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 06/21/2022
 *
 */
public class HelloInterAnycastRequest extends IntercastRequest
{
	private static final long serialVersionUID = -8702576599145804189L;

	private String message;

	public HelloInterAnycastRequest(String message, Set<String> dstKeys)
	{
		super(MulticastMessageType.INTER_ANYCAST_REQUEST, Tools.generateUniqueKey(), dstKeys, ClusterAppID.HELLO_INTER_ANYCAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
