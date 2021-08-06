package org.greatfree.message.multicast.container;

import org.greatfree.message.multicast.MulticastMessageType;

/*
 * Some of the code is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
 */

/*
 * The message is retained in the children-based intercasting. It needs to enclose the original intercasting message with the additional information from the source child. 03/02/2019, Bing Li
 * 
 * Maybe the message is not useful in the children-based intercasting. 03/02/2019, Bing Li
 * 
 * For the children-based intercasting, the message appends some information from the source child before it is sent to the destination children. 03/02/2019, Bing Li
 */

// Created: 02/23/2019, Bing Li
public abstract class InterChildrenNotification extends ClusterNotification
{
	private static final long serialVersionUID = -6646920185310225985L;
	
	private IntercastNotification in;

//	private String destinationKey;
//	private Set<String> destinationKeys;

//	public IntercastChildNotification(int notificationType, int applicationID, Set<String> dstKeys)
	public InterChildrenNotification(IntercastNotification in)
	{
		super(MulticastMessageType.INTER_CHILDEN_NOTIFICATION, in.getApplicationID());
//		this.destinationKey = UtilConfig.EMPTY_STRING;
//		this.destinationKeys = dstKeys;
		this.in = in;
	}
	
	public IntercastNotification getIntercastNotification()
	{
		return this.in;
	}

	/*
	public IntercastChildNotification(String dstKey, int applicationID)
	{
		super(dstKey, MulticastMessageType.INTER_CHILD_UNICAST_NOTIFICATION, applicationID);
		this.destinationKey = dstKey;
		this.destinationKeys = null;
	}
	
	public String getDestinationKey()
	{
		return this.destinationKey;
	}
	
	public Set<String> getDestinationKeys()
	{
		return this.destinationKeys;
	}
	*/
}
