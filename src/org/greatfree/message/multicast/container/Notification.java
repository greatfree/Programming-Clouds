package org.greatfree.message.multicast.container;

import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastMessageType;

// Created: 09/23/2018, Bing Li
public abstract class Notification extends MulticastMessage
{
	private static final long serialVersionUID = 3659819496588962078L;
	
	private String clientKey;
	private int notificationType;
	private int applicationID;

	/*
	 * The constructor is usually used for the nearest unicasting. So the client key is required for nearest measurement. 10/28/2018, Bing Li 
	 */
	public Notification(String clientKey, int notificationType, int applicationID)
	{
		super(MulticastMessageType.NOTIFICATION);
		this.clientKey = clientKey;
		this.notificationType = notificationType;
//		this.notificationType = MulticastMessageType.UNICAST_NOTIFICATION;
		this.applicationID = applicationID;
	}

	/*
	 * The below two constructors are used together for the root and the children, respectively. 10/28/2018, Bing Li
	 */

	/*
	 * This constructor is usually used for normal broadcasting. 10/28/2018, Bing Li
	 */
	public Notification(int notificationType, int applicationID)
	{
		super(MulticastMessageType.NOTIFICATION);
		this.notificationType = notificationType;
		this.applicationID = applicationID;
	}

	/*
	 * This constructor is usually used for normal broadcasting. 10/28/2018, Bing Li
	 */
	/*
	public Notification(int notificationType, int applicationID, Map<String, IPAddress> childrenIPs)
	{
		super(ClusterMessageType.NOTIFICATION, childrenIPs);
		this.notificationType = notificationType;
		this.applicationID = applicationID;
	}
	*/

	/*
	 * The below two constructors are used together for the root and the children, respectively. 10/28/2018, Bing Li
	 */
	
	/*
	 * When shutting down a server, it is associated with the one for one particular application. So the StopServerOnClusterNotification should not be a system level one. Thus, the below design is not necessary. 10/28/2018. Bing Li
	 * 
	 * For some system messages, such as StopServerOnClusterNotification, it is unnecessary to assign the application ID. 10/28/2018, Bing Li
	 */
	/*
	public Notification(int notificationType)
	{
		super(ClusterMessageType.NOTIFICATION);
		this.notificationType = notificationType;
	}
	*/

	/*
	 * For some system messages, such as StopServerOnClusterNotification, it is unnecessary to assign the application ID. 10/28/2018, Bing Li
	 */
	/*
	public Notification(int notificationType, Map<String, IPAddress> childrenIPs)
	{
		super(ClusterMessageType.NOTIFICATION, childrenIPs);
		this.notificationType = notificationType;
	}
	*/

	public String getClientKey()
	{
		return this.clientKey;
	}

	public int getNotificationType()
	{
		return this.notificationType;
	}
	
	public int getApplicationID()
	{
		return this.applicationID;
	}
}
