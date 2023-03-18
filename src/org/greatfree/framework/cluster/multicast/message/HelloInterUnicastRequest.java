package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 06/20/2022
 *
 */
public class HelloInterUnicastRequest extends IntercastRequest
{
	private static final long serialVersionUID = 1568498855360861077L;
	
	private String message;

	public HelloInterUnicastRequest(String message)
	{
		super(Tools.generateUniqueKey(), Tools.generateUniqueKey(), ClusterAppID.HELLO_INTER_UNICAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
