package org.greatfree.dip.multicast.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessage;

/*
 * The notification is a testing message to be sent to distributed data in the manner of anycasting. 05/08/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
public class HelloWorldAnycastNotification extends MulticastMessage
{
	private static final long serialVersionUID = -8596454328337543079L;
	
	private HelloWorld hl;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldAnycastNotification(String key, HelloWorld hl)
	public HelloWorldAnycastNotification(HelloWorld hl)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_ANYCAST_NOTIFICATION);
		this.hl = hl;
	}
	
	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldAnycastNotification(String key, HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	/*
	public HelloWorldAnycastNotification(HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	{
		super(MulticastMessageType.HELLO_WORLD_ANYCAST_NOTIFICATION, childrenServers);
		this.hl = hl;
	}
	*/

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
