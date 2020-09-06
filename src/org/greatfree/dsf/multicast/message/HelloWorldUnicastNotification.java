package org.greatfree.dsf.multicast.message;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessage;

/*
 * The notification is a testing message to be sent to distributed data in the manner of unicasting. 05/19/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
public class HelloWorldUnicastNotification extends MulticastMessage
{
	private static final long serialVersionUID = 8129038077864608395L;
	
	private HelloWorld hl;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldUnicastNotification(String key, HelloWorld hl)
	public HelloWorldUnicastNotification(HelloWorld hl)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_UNICAST_NOTIFICATION);
		this.hl = hl;
	}
	
	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	/*
//	public HelloWorldUnicastNotification(String key, HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	public HelloWorldUnicastNotification(HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	{
		super(MulticastMessageType.HELLO_WORLD_UNICAST_NOTIFICATION, childrenServers);
		this.hl = hl;
	}
	*/

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
