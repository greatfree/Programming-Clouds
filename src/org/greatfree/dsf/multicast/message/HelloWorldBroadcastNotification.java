package org.greatfree.dsf.multicast.message;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessage;

/*
 * The notification is a testing message to be sent to distributed nodes in the manner of broadcasting. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class HelloWorldBroadcastNotification extends MulticastMessage
{
	private static final long serialVersionUID = 6393056123607868530L;
	
	private HelloWorld hl;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldBroadcastNotification(String key, HelloWorld hl)
	public HelloWorldBroadcastNotification(HelloWorld hl)
	{
//		super(MulticastMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION, Tools.generateUniqueKey());
		super(MulticastDIPMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION);
		this.hl = hl;
	}
	
	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldBroadcastNotification(String key, HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	/*
	public HelloWorldBroadcastNotification(HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	{
//		super(MulticastMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION, Tools.generateUniqueKey(), childrenServers);
		super(MulticastMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION, childrenServers);
		this.hl = hl;
	}
	*/

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
