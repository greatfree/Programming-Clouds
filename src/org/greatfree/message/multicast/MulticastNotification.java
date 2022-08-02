package org.greatfree.message.multicast;

import java.util.Map;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

/*
 * This is the base class to implement the message that can be multicast among a cluster of nodes. 05/12/2017, Bing Li
 */

// Created: 05/12/2017, Bing Li
public abstract class MulticastNotification extends ServerMessage
{
	private static final long serialVersionUID = 6752213037140344467L;
	
	// Yes, I think so. 08/24/2018, Bing Li
	// Is it necessary to add one attribute to indicate the message is for anycast? 08/23/2018, Bing Li
//	private boolean isAnycast;
	
	// The nodes to receive the message to be multicast. 05/12/2017, Bing Li
	private Map<String, IPAddress> children;
//	private int applicationID;

	/*
	 * Initialize the message to be multicast. 05/12/2017, Bing Li
	 * 
	 * The argument, type, represents the type of the message;
	 * 
	 * The argument, key, represents the unique key of the message;
	 * 
	 * The argument, children, keeps all of the nodes that receive the message. The key of the collection is the unique key of the client and the value is the IPPort, which can be retrieved from the registry server. 05/12/2017, Bing Li
	 * 
	 */
//	public MulticastMessage(int type, String key, Map<String, IPAddress> children, boolean isAnycast)
//	public MulticastMessage(int type, String key, Map<String, IPAddress> children)

	/*
	public MulticastMessage(int type, Map<String, IPAddress> children)
	{
		super(type, Tools.generateUniqueKey());
		this.children = children;
//		this.isAnycast = isAnycast;
	}
	*/

	/*
	 * Another constructor of the message. This is invoked by the node which has no grandsons. 11/10/2014, Bing Li
	 */
//	public MulticastMessage(int type, String key)
//	public MulticastNotification(int applicationID)
	public MulticastNotification(int type)
	{
		super(type, Tools.generateUniqueKey());
		this.children = null;
//		this.isAnycast = false;
//		this.applicationID = applicationID;
	}

	/*
	public MulticastNotification(int type, int applicationID)
	{
		super(type, Tools.generateUniqueKey());
		this.children = null;
//		this.isAnycast = false;
		this.applicationID = applicationID;
	}

	public int getApplicationID()
	{
		return this.applicationID;
	}
	*/
	
	/*
	 * Return the indicator whether the message 
	 */
	/*
	public boolean isAnycast()
	{
		return this.isAnycast;
	}
	*/
		
	/*
	 * Set the children nodes. The method is usually invoked when it is failed to send a message to a particular node. Therefore, it is necessary to choose another node and reset the children nodes to it after removing the failed one. 11/10/2014, Bing Li
	 */
	public void setChildrenNodes(Map<String, IPAddress> ips)
	{
		this.children = ips;
	}
	
	/*
	 * For the leaf node, it is required to clear the children. 12/16/2018, Bing Li
	 */
	/*
	public void clearChildren()
	{
		this.children.clear();
	}
	*/
	
	/*
	 * Get the IP of a specific child by its key. With the IP, it is convenient to connect the child by the FreeClient pool. 11/10/2014, Bing Li
	 */
	/*
	public IPAddress getIP(String childKey)
	{
		return this.children.get(childKey);
	}
	*/
	
	/*
	 * Get the collection of all of the children. The method is invoked when it is received by a particular node and it is required for the node to keep transfer the message to those children. After getting the keys of the children, the node is able to construct a tree for those children to raise the multicast efficiency. 11/10/2014, Bing Li
	 */
	public Map<String, IPAddress> getChildrenIPs()
	{
		return this.children;
	}
}